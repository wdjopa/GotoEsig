package fr.tchatat.gotoesig.ui.roads;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import fr.tchatat.gotoesig.HttpConnection;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.Trajet;

public class TrajetMap extends FragmentActivity implements OnMapReadyCallback {

    private static final LatLng ESIGELEC= new LatLng(49.3832749,
            1.0746961);
    private static LatLng pointDepart;

    private MarkerOptions place1;
    private MarkerOptions place2;

    GoogleMap googleMap;
    final String TAG = "PathGoogleMapActivity";

    private Trajet t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajet_map);

        place2 = new MarkerOptions().position(ESIGELEC).title("ESIGELEC");

        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses;
        try{
            t = getIntent().getParcelableExtra("trajet");
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


        fm.getMapAsync(this);
    }

    private String getMapsApiDirectionsUrl() {
        String mode = "mode=";
        String transit = "transit_mode=";
        String avoid = "";
        if(t.getMoyen().equals("Voiture") || t.getMoyen().equals("Moto")){
            mode += "driving";
            if(t.getAutoroute().equals("Non")) avoid = "avoid=highways";
        }
        else {
            if(t.getMoyen().equals("Métro")) {
                mode = "";
            }
            if(t.getMoyen().equals("Bus")) {
                mode = "";
            }
            if(t.getMoyen().equals("Vélo")){
                mode += "bicycling";
            }
            if(t.getMoyen().equals("Marche")){
                mode += "Walking";
            }
        }

        String depTime = "";
        try{
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
            Date laDate = (Date)formatter.parse(t.getDate() + " " + t.getTemps() + ":00");
            depTime = "departure_time=" + String.valueOf(laDate.getTime());
        }catch(ParseException pe){
            Toast.makeText(this, "La date n'a pas pu être récupérée correctement", Toast.LENGTH_SHORT).show();
        }

        String origin = "origin=" + t.getAdresse();
        String destination = "destination=ESIGELEC, SER, France";
        String key = "key=AIzaSyCRNIOy2kuxSgiwTkTOEgCetao9-s3uWjY";
        String params = origin + "&" + destination + "&" + mode + "&" + transit + "&" + "&" + depTime + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
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