package fr.tchatat.gotoesig.ui.roads;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.tchatat.gotoesig.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;

public class RechercheTrajetFragment extends Fragment {

    private RechercherTrajetViewModel rechercherTrajetViewModel;

    private EditText etPoint;
    private EditText etDate;
    private RecyclerView resultats;
    private TrajetAdapter resultatsAdapter;
    Global vars;

    private ArrayList<TrajetCard> search(final String adress, String date){
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query trajetsQuery = databaseRef.child("trajets");
        if (!date.matches("")) trajetsQuery = trajetsQuery.orderByChild("date").equalTo(date);

        final ArrayList<TrajetCard> trajets = new ArrayList<TrajetCard>();
        trajetsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot trajet : dataSnapshot.getChildren()) {
                    Trajet t = trajet.getValue(Trajet.class);
                    if (t.getAdresse().contains(adress)) trajets.add(new TrajetCard(vars.getUser(), t));
                }
                Log.d("result", adress);
                Log.d("result", new Gson().toJson(trajets));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return trajets;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rechercherTrajetViewModel =
                ViewModelProviders.of(this).get(RechercherTrajetViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chercher_trajet, container, false);
        /*final TextView textView = root.findViewById(R.id.text_gallery);
        nouveauTrajetViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        vars = (Global) getActivity().getApplicationContext();

        etPoint = root.findViewById(R.id.etPointSearch);
        etDate = root.findViewById(R.id.etDateSearch);
        resultats = root.findViewById(R.id.resultatsRecherche);

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
                etDate.setText(sdf.format(myCalendar.getTime()));
                ArrayList<TrajetCard> results =  search(etPoint.getText().toString(), etDate.getText().toString());
                resultatsAdapter = new TrajetAdapter(getActivity(), results);
                resultats.scrollToPosition(results.size() );
                resultatsAdapter.notifyItemInserted(results.size());
                resultatsAdapter.notifyDataSetChanged();
            }

        };

        etPoint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                //Log.d("result", etPoint.getText().toString());
                ArrayList<TrajetCard> results =  search(etPoint.getText().toString(), etDate.getText().toString());
                resultatsAdapter = new TrajetAdapter(getActivity(), results);
                resultats.scrollToPosition(results.size() );
                resultatsAdapter.notifyItemInserted(results.size());
                resultatsAdapter.notifyDataSetChanged();
            }
        });

        etDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), datePick, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        return root;
    }
}