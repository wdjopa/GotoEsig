package fr.tchatat.gotoesig.ui.roads;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fr.tchatat.gotoesig.R;

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


    public void setSpinner(){
        adapter1 = new ArrayAdapter<String>(this.getActivity().getApplicationContext(), android.R.layout.simple_spinner_item, moyens);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoyen.setAdapter(adapter1);
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

        voiturelayout = root.findViewById(R.id.voitureLayout);
        voiturelayout.setVisibility(ConstraintLayout.GONE);
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
                }
                else {
                    voiturelayout.setVisibility(ConstraintLayout.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnVal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String moyenT = spinnerMoyen.getSelectedItem().toString();
                String adresse = ptDepart.getText().toString();
                String date = dDepart.getText().toString();
                String heure = hDepart.getText().toString();
                String ret = retard.getText().toString();
                String places = nbPlaces.getText().toString();
                String contrib = contribution.getText().toString();

                if(moyenT == "" || adresse == "" || date == "" || heure == "" || ret == "" || places == "" || places == "" || contrib == ""){
                    Toast.makeText(getActivity(), "Tous les champs sont obligatoires", Toast.LENGTH_SHORT);
                }else{
                    int iplaces = Integer.parseInt(places);
                    float fcontrib = Float.parseFloat(contrib);

                    Log.d("ok", "Clic");
                    new AlertDialog.Builder(getActivity())
                        .setTitle("Delete entry")
                        .setMessage("Le trajet fera 7 km et durera 14 min. Voulez vous le sélectioner ?\nAttention, cette action ne pourra être annulée !")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                }

            }
        });

        return root;
    }
}
