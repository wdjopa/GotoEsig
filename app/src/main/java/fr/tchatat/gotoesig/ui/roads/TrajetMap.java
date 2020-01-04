package fr.tchatat.gotoesig.ui.roads;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import fr.tchatat.gotoesig.HttpConnection;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.Notification;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.models.UserTrajet;

public class TrajetMap extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng ESIGELEC= new LatLng(49.3832749,
            1.0746961);
    private static LatLng pointDepart;

    private MarkerOptions place1;
    private MarkerOptions place2;

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    private TrajetCard tCard;
    private Trajet t;

    private Button btnVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_map);

        place2 = new MarkerOptions().position(ESIGELEC).title("ESIGELEC");

        btnVal = findViewById(R.id.btnValCarte);

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try{
            tCard = getIntent().getParcelableExtra("trajet");
            t = tCard.getTrajet();
            addresses = geocoder.getFromLocationName(t.getAdresse(), 1);
            if(addresses.size() > 0) {
                double latitude= addresses.get(0).getLatitude();
                double longitude= addresses.get(0).getLongitude();
                pointDepart = new LatLng(latitude, longitude);
                place1 = new MarkerOptions().position(pointDepart).title(t.getAdresse());
            }
        }catch(IOException ioe){
            Toast.makeText(this, "Une erreur est survenue", Toast.LENGTH_SHORT).show();
        }

        btnVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uid = FirebaseAuth.getInstance().getUid();
                final DatabaseReference trajetsRef = FirebaseDatabase.getInstance().getReference().child("trajets/"+t.getId());
                final Query participantsQuery = trajetsRef.child("participants");
                participantsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot part : dataSnapshot.getChildren()) {
                            if(part.getValue().toString().equals(uid)){
                                i = -1;
                                break;
                            }
                            i++;
                        }
                        if(i != -1){
                            if (i < t.getNombre()){
                                new AlertDialog.Builder(TrajetMap.this)
                                        .setTitle("Confirmation")
                                        .setMessage("Reserver ce trajet ?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                DatabaseReference participantRef = trajetsRef.child("participants/" + uid);
                                                participantRef.setValue(uid);
                                                UserTrajet ut = new UserTrajet(t.getId());
                                                FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/trajets/"+t.getId()).setValue(ut);
                                                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("/users/"+t.getUid()+"/token");
                                                tokenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        String token = dataSnapshot.getValue().toString();
                                                        sendPost(token);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, null)
                                        .show();
                            }else {
                                Toast.makeText(TrajetMap.this, "Il n'y a plus de places disponibles pour ce trajet", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(TrajetMap.this, "Vous avez déjà réservé ce trajet", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        fm.getMapAsync(this);
    }
    public void sendPost(final String token) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlAdress = "https://fcm.googleapis.com/fcm/send";
                    URL url = new URL(urlAdress);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("to", token);
                    jsonParam.put("notification", new Gson().toJson(new Notification("Réservation - GotoESIG","Votre trajet du "+tCard.getTrajet().getDate()+"a été réservé par "+tCard.getUser().getPseudo())));

                    Log.i("JSON", jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
    private String getMapsApiDirectionsUrl() {
        String mode = "&mode=";
        String transit = "&transit_mode=";
        String avoid = "";
        if(t.getMoyen().equals("Voiture") || t.getMoyen().equals("Moto")){
            mode += "driving";
            transit = "";
            if(t.getAutoroute().equals("Non")) avoid = "&avoid=highways";
        }
        else {
            if(t.getMoyen().equals("Métro")) {
                transit += "rail";
                mode = "";
            }
            if(t.getMoyen().equals("Bus")) {
                transit += "bus";
                mode = "";
            }
            if(t.getMoyen().equals("Vélo")){
                mode += "bicycling";
                transit = "";
            }
            if(t.getMoyen().equals("Marche")){
                mode += "Walking";
                transit = "";
            }
        }

        String depTime = "";
        try{
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Log.d("la date", t.getDate() + " " + t.getHeure() + ":00");
            Date laDate = (Date)formatter.parse(t.getDate() + " " + t.getHeure() + ":00");
            depTime = "&departure_time=" + String.valueOf(laDate.getTime());
        }catch(ParseException pe){
            Toast.makeText(this, t.getDate() + " " + t.getHeure() + ":00", Toast.LENGTH_SHORT).show();
        }

        String origin = "origin=" + t.getAdresse();
        String destination = "&destination=ESIGELEC, SER, France";
        String key = "&key=AIzaSyCRNIOy2kuxSgiwTkTOEgCetao9-s3uWjY";
        String params = origin + destination + mode + transit + depTime + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;

        Log.d("url", url);
        return url;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.addMarker(place1);
        googleMap.addMarker(place2);

        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ESIGELEC,
                12));
    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
                Log.d("routes", "routes : "+routes.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(12);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}