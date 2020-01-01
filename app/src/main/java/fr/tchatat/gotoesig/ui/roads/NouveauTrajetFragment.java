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

public class NouveauTrajetFragment extends Fragment {

    private NouveauTrajetViewModel nouveauTrajetViewModel;
    private Spinner spinnerMoyen;
    private Spinner spinnerAutoroute;
    private ConstraintLayout voiturelayout;

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

        spinnerMoyen = root.findViewById(R.id.spinMoyen);
        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(), R.array.moyens_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        List<String> moyens = new ArrayList<>();

        spinnerMoyen.setAdapter(adapter1);

        DocumentReference user = db.collection("moyensTransport").document("xLOCQO7bas6ToVKUezOq");
        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d("data", task.getResult().get("moyens").toString());
                    adapter1.notifyDataSetChanged();
                }
            }
        });


        spinnerAutoroute = root.findViewById(R.id.spinAutoroute);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(), R.array.autoroute_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
}