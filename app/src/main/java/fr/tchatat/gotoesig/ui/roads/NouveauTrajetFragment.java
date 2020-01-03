package fr.tchatat.gotoesig.ui.roads;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Response;
import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.UserTrajet;
import fr.tchatat.gotoesig.ui.home.HomeFragment;

public class NouveauTrajetFragment extends Fragment {

    private NouveauTrajetViewModel nouveauTrajetViewModel;
    private Spinner spinnerMoyen;
    private Spinner spinnerAutoroute;
    private EditText ptDepart;
    private EditText dDepart;
    private EditText hDepart;
    private EditText retard;
    private EditText contribution;
    private EditText nbPlaces;
    private ConstraintLayout voiturelayout;
    private static List<String> moyens;
    private static ArrayAdapter<String> adapter1;
    private Button btnVal;
    private RequestQueue requestQueue;
    private String baseUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=";
    private String url;
    private final String key = "AIzaSyCRNIOy2kuxSgiwTkTOEgCetao9-s3uWjY";
    private static String dist = "";
    private static String temps = "";
    private static String mode = "";
    private static String transit = "";
    private static String depTime = "";


    public void setSpinner(){
        adapter1 = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, moyens);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoyen.setAdapter(adapter1);
    }

    private void popup(String key, final String adresse, final String auto, final float fcontrib, final String date, final String heure, final String moyenT, final int iret, final int iplaces) {
        // First, we insert the username into the repo url.
        // The repo url is defined in GitHubs API docs (https://developer.github.com/v3/repos/).

        if (!transit.equals("")) transit = "&transit_mode=" + transit;
        if (!mode.equals("")) mode = "&mode=" + mode;

        this.url = this.baseUrl + adresse + "&destinations=ESIGELEC,SER,France"+ mode + transit + "&departure_time="+ depTime +"&key=" + key;

        // Next, we create a new JsonArrayRequest. This will use Volley to make a HTTP request
        // that expects a JSON Array Response.
        // To fully understand this, I'd recommend readng the office docs: https://developer.android.com/training/volley/index.html
        JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("data", response.toString());
                        try{
                            JSONArray rows = response.getJSONArray("rows");
                            JSONObject elements0 = rows.getJSONObject(0);
                            JSONArray elements = elements0.getJSONArray("elements");
                            JSONObject element = elements.getJSONObject(0);
                            JSONObject distance = element.getJSONObject("distance");
                            JSONObject duration = element.getJSONObject("duration");

                            dist = distance.getString("text");
                            temps = duration.getString("text");

                        }catch( JSONException je){
                            Toast.makeText(getActivity(), "Une erreur s'est produite", Toast.LENGTH_SHORT).show();
                            Log.d("JSONException", je.getMessage());
                        }

                        if((!dist.matches("")) && (!temps.matches(""))){
                            new AlertDialog.Builder(getActivity())
                                .setTitle("Confirmation")
                                .setMessage("Le trajet fera "+ dist +" et durera "+ temps +". Voulez vous le sélectioner ?\nAttention, cette action ne pourra être annulée !")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String uid = FirebaseAuth.getInstance().getUid();
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets");

                                        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("/users");
                                        String id = String.valueOf(System.currentTimeMillis());

                                        DatabaseReference trajetsRef = ref.child(id);
                                        DatabaseReference usersRef = ref2.child(uid + "/trajets/" +id);
                                        Trajet trajet = new Trajet();
                                        trajet.setAdresse(adresse.toLowerCase().trim());
                                        trajet.setAutoroute(auto);
                                        trajet.setContribution(fcontrib);
                                        trajet.setDate(date);
                                        trajet.setDistance(dist);
                                        trajet.setHeure(heure);
                                        trajet.setId(id);
                                        trajet.setUid(uid);
                                        trajet.setMoyen(moyenT);
                                        trajet.setNombre(iplaces);
                                        trajet.setTemps(temps);
                                        trajet.setRetard(iret);
                                        Log.d("Trajet", new Gson().toJson(trajet));
                                        Map<String, Trajet> trajets = new HashMap<>();
                                        trajets.put(id, trajet);

                                        trajetsRef.setValue(trajet);
                                        usersRef.setValue(new UserTrajet(id));

                                        Fragment fragment = new HomeFragment();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();

                                        Toast.makeText(getActivity(), "Trajet créé avec succès !", Toast.LENGTH_SHORT).show();
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Error calculatiing the distance and time. Please check your Internet connection", Toast.LENGTH_SHORT).show();
                        Log.e("Volley", error.toString());
                    }
                }
        );
        // Add the request we just defined to our request queue.
        // The request queue will automatically handle the request as soon as it can.
        requestQueue.add(arrReq);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        nouveauTrajetViewModel =
                ViewModelProviders.of(this).get(NouveauTrajetViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_road, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        nouveauTrajetViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        requestQueue = Volley.newRequestQueue(getActivity());

        voiturelayout = root.findViewById(R.id.voitureLayout);
        voiturelayout.setVisibility(ConstraintLayout.GONE);
        ptDepart = root.findViewById(R.id.etPointSearch);
        dDepart = root.findViewById(R.id.etDateSearch);
        hDepart = root.findViewById(R.id.etTime);
        retard = root.findViewById(R.id.etRetard);
        contribution = root.findViewById(R.id.etContribution);
        nbPlaces = root.findViewById(R.id.etNombre);
        btnVal = root.findViewById(R.id.ajoutTrajetBtn);

        FirebaseApp.initializeApp(this.getActivity());
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        moyens = new ArrayList<String>();

        spinnerMoyen = root.findViewById(R.id.spinMoyen);

        DocumentReference user = db.collection("moyensTransport").document("xLOCQO7bas6ToVKUezOq");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    NouveauTrajetFragment.moyens = (ArrayList<String>) task.getResult().get("moyens");
                    //Log.d("data", moyens.toString());
                    setSpinner();
                }
            }
        });

        spinnerAutoroute = root.findViewById(R.id.spinAutoroute);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(), R.array.autoroute_array, android.R.layout.simple_spinner_item);
        //adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAutoroute.setAdapter(adapter2);

        spinnerMoyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinnerMoyen.getSelectedItem().toString().equals("Voiture")){
                    voiturelayout.setVisibility(ConstraintLayout.VISIBLE);
                    mode = "driving";
                }
                else {
                    voiturelayout.setVisibility(ConstraintLayout.GONE);
                    if(spinnerMoyen.getSelectedItem().toString().equals("Métro")) {
                        mode = "";
                        transit = "rail";

                    }
                    if(spinnerMoyen.getSelectedItem().toString().equals("Bus")) {
                        mode = "";
                        transit = "bus";
                    }
                    if(spinnerMoyen.getSelectedItem().toString().equals("Vélo")){
                        mode = "bicycling";
                        transit = "";
                    }
                    if(spinnerMoyen.getSelectedItem().toString().equals("Marche")){
                        mode = "Walking";
                        transit = "";
                    }
                    if(spinnerMoyen.getSelectedItem().toString().equals("Moto")){
                        mode = "driving";
                        transit = "";
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener datePick = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                dDepart.setText(sdf.format(myCalendar.getTime()));
            }

        };

        dDepart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), datePick, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        hDepart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        hDepart.setText(hourOfDay + ":" + minutes);
                    }
                }, 0, 0, true).show();
            }
        });

        btnVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String moyenT = spinnerMoyen.getSelectedItem().toString();
                final String adresse = ptDepart.getText().toString();
                final String date = dDepart.getText().toString();
                final String heure = hDepart.getText().toString();
                String ret = retard.getText().toString();
                String places = nbPlaces.getText().toString();
                String contrib = contribution.getText().toString();
                final String auto = (moyenT.equals("Voiture") ? spinnerAutoroute.getSelectedItem().toString() : "Non");

                if(adresse.matches("") || date.matches("") || heure.matches("") || ret.matches("") || places.matches("") || places.matches("") || (moyenT.equals("Voiture") && contrib.matches(""))){
                    Toast.makeText(getActivity(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
                }else{
                    try{
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                        Date laDate = (Date)formatter.parse(date + " " + heure + ":00");
                        depTime = String.valueOf(laDate.getTime());

                        final int iplaces = Integer.parseInt(places);
                        final int iret = Integer.parseInt(ret);
                        final float fcontrib = (moyenT.equals("Voiture") ? Float.parseFloat(contrib) : 0);

                        Log.d("ok", "Clic");

                        popup(key, adresse, auto, fcontrib, date, heure, moyenT, iret, iplaces);
                    }catch(ParseException pe){
                        Toast.makeText(getActivity(), "La date et/ou l'heure entrée est incorrecte", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        return root;
    }
}
