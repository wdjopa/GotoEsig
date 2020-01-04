package fr.tchatat.gotoesig.ui.roads;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import fr.tchatat.gotoesig.Inscription;
import fr.tchatat.gotoesig.LoginActivity;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;

public class RechercheTrajetFragment extends Fragment  {

    private RechercherTrajetViewModel rechercherTrajetViewModel;

    private EditText etPoint;
    private EditText etDate;
    private RecyclerView resultats;
    private TrajetAdapter resultatsAdapter;
    private ArrayList<TrajetCard> results = new ArrayList<TrajetCard>();
    private TrajetAdapter.onClickInterface onclickInterface;
    private Handler handler = new Handler();
    private ProgressDialog dialog;

    Global vars;

    private void search(final String adress, String date){
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query trajetsQuery = databaseRef.child("trajets");
        results.clear();
        if (!date.matches("")) trajetsQuery = trajetsQuery.orderByChild("date").equalTo(date);

        trajetsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot trajet : dataSnapshot.getChildren()) {
                    final Trajet t = trajet.getValue(Trajet.class);
                    if (t.getAdresse().contains(adress.toLowerCase())) {
                        Query userQuery = databaseRef.child("users/"+t.getUid()+"/account");
                        userQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);
                                results.add(new TrajetCard(u, t));
                                Log.d("result", adress);
                                Log.d("result", new Gson().toJson(results));
                                resultats.scrollToPosition(results.size());
                                resultatsAdapter.notifyItemInserted(results.size());
                                resultatsAdapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else{
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        resultatsAdapter = new TrajetAdapter(getActivity(), results, new TrajetAdapter.onClickInterface() {
            @Override
            public void onItemClick(TrajetCard item) {
                Intent intent = new Intent(getActivity(), TrajetMap.class);
                intent.putExtra("trajet", item);
                startActivity(intent);
                Toast.makeText(getActivity(),item.toString(),Toast.LENGTH_LONG).show();
            }
        });
        resultats.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultats.setAdapter(resultatsAdapter);

        dialog = ProgressDialog.show(getActivity(), "","Récupération des données ..." , true);
        dialog.show();
        handler.postDelayed(new Runnable() {public void run() {                                dialog.dismiss();
        }}, 3000);
        search("", "");

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

                search(etPoint.getText().toString(), etDate.getText().toString());
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
                dialog = ProgressDialog.show(getActivity(), "","Recherche ..." , true);
                dialog.show();
                handler.postDelayed(new Runnable() {public void run() {                                dialog.dismiss();
                }}, 3000);
                search(etPoint.getText().toString(), etDate.getText().toString());
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