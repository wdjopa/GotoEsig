package fr.tchatat.gotoesig.ui.roads;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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

public class NouveauTrajetFragment extends Fragment implements View.OnClickListener {

    private NouveauTrajetViewModel nouveauTrajetViewModel;
    private Spinner spinnerMoyen;
    private Spinner spinnerAutoroute;
    private ConstraintLayout voiturelayout;
    private static List<String> moyens;
    private static ArrayAdapter<String> adapter1;

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

        return root;
    }

    @Override
    public void onClick(View view) {

    }
}