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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.tchatat.gotoesig.Global;
import fr.tchatat.gotoesig.Inscription;
import fr.tchatat.gotoesig.LoginActivity;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.GlobalTrajet;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;
import java.text.Normalizer;

public class RechercheTrajetFragment extends Fragment  {

    private RechercherTrajetViewModel rechercherTrajetViewModel;

    private EditText etPoint;
    private EditText etDate;
    private RecyclerView resultats;
    private TrajetAdapter resultatsAdapter;
    private ArrayList<TrajetCard> results = new ArrayList<TrajetCard>();
    private TrajetAdapter.onClickInterface onclickInterface;
    private Handler handler;
    private ProgressDialog dialog;

    Global vars;

    public static String stripAccents(String s)
    {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    private void search(final String adress, String date){
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query trajetsQuery = databaseRef.child("trajets");
        results.clear();
        if (!date.matches("")) trajetsQuery = trajetsQuery.orderByChild("date").equalTo(date);
        dialog = ProgressDialog.show(getActivity(), "","Recherche ..." , true,true);
        dialog.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {public void run() {                                dialog.dismiss();
        }}, 3000);
        trajetsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot trajet : dataSnapshot.getChildren()) {
                    final Trajet t = trajet.child("trajet").getValue(Trajet.class);
                    final int nombre = Integer.parseInt(String.valueOf(trajet.child("participants").getChildrenCount()));

                    if (stripAccents(t.getAdresse()).contains(stripAccents(adress.toLowerCase()))) {
                        Query userQuery = databaseRef.child("users/"+t.getUid()+"/account");
                        userQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);

                                String dtStart = t.getDate()+"T"+t.getHeure()+"Z";
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm'Z'");
                                Date date = null;
                                try {
                                    date = format.parse(dtStart);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(!(new Date().after(date))){
                                    results.add(new TrajetCard(u, t, nombre));
                                    Log.d("result", adress);
                                    Log.d("result", new Gson().toJson(results));
                                    resultats.scrollToPosition(results.size());
                                    resultatsAdapter.notifyItemInserted(results.size());
                                    resultatsAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                dialog.dismiss();

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
                dialog.dismiss();

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