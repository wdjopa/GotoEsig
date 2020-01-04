package fr.tchatat.gotoesig.ui.roads;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.tchatat.gotoesig.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.UserTrajet;

public class MesTrajetsFragment extends Fragment {

    private MesTrajetsViewModel mesTrajetsViewModel;
    RecyclerView listeTrajetsTermines, listeTrajetsEnCours;
    TrajetAdapter listeTrajetsAdapterTermines, listeTrajetsAdapterEnCours;
    ArrayList<TrajetCard> trajetsTermines = new ArrayList<>();
    ArrayList<TrajetCard> trajetsEnCours = new ArrayList<>();
    Global vars;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mesTrajetsViewModel = ViewModelProviders.of(this).get(MesTrajetsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_mes_trajets, container, false);
        getActivity().setTitle("Mes Trajets");
        vars = (Global) getActivity().getApplicationContext();
/*
        Trajet test= new Trajet();
        test.setAdresse("2 Rue Hélène Boucher, 76140 Le Petit-Quevilly, France");
        test.setAutoroute("Non");
        test.setContribution(0);
        test.setDate("15/08/2020");
        test.setDistance("148km");
        test.setHeure("07:30");
        test.setId("1577990065826");
        test.setMoyen("Moto");
        test.setRetard(5);
        test.setNombre(2);
        test.setUid(vars.getUser().getUid());

        trajets.add(new TrajetCard(vars.getUser(), test));
*/

        listeTrajetsEnCours= root.findViewById(R.id.trajets_list_en_cours);
        listeTrajetsAdapterEnCours= new TrajetAdapter(getActivity(), trajetsEnCours, new TrajetAdapter.onClickInterface() {
            @Override
            public void onItemClick(TrajetCard item) {
                Intent intent = new Intent(getActivity(), TrajetMap.class);
                intent.putExtra("trajet", item);
                startActivity(intent);
                Toast.makeText(getActivity(), new Gson().toJson(item), Toast.LENGTH_SHORT);
            }
        });

        listeTrajetsEnCours.setLayoutManager(new LinearLayoutManager(getActivity()));
        listeTrajetsEnCours.setAdapter(listeTrajetsAdapterEnCours);

        listeTrajetsTermines = root.findViewById(R.id.trajets_list_termines);
        listeTrajetsAdapterTermines= new TrajetAdapter(getActivity(), trajetsTermines, new TrajetAdapter.onClickInterface() {
            @Override
            public void onItemClick(TrajetCard item) {
                Intent intent = new Intent(getActivity(), TrajetMap.class);
                intent.putExtra("trajet", item);
                startActivity(intent);
                Toast.makeText(getActivity(), "Ce trajet est terminé", Toast.LENGTH_SHORT);
            }
        });
        listeTrajetsTermines.setLayoutManager(new LinearLayoutManager(getActivity()));
        listeTrajetsTermines.setAdapter(listeTrajetsAdapterTermines);


        if(trajetsTermines.size()==0){
            listeTrajetsTermines.setVisibility(View.GONE);
            root.findViewById(R.id.emptyTermines).setVisibility(View.VISIBLE);
        }

        if(trajetsEnCours.size()==0) {
            listeTrajetsEnCours.setVisibility(View.GONE);
            root.findViewById(R.id.emptyEnCours).setVisibility(View.VISIBLE);
        }


        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {

                        UserTrajet userTrajet = new UserTrajet();
                        userTrajet.setId(snapshot.child("id").getValue().toString());
                        if(userTrajet.getId() != null){
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets");
                            DatabaseReference trajetRef = ref.child(userTrajet.getId());

                            ValueEventListener userListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Trajet trajet = dataSnapshot.getValue(Trajet.class);
                                    if(trajet != null){
                                        String dtStart = trajet.getDate()+"T"+trajet.getHeure()+"Z";
                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm'Z'");
                                        try {
                                            Date date = format.parse(dtStart);
                                            if(new Date().after(date)){
                                                Log.d("trajet de l'utilisateur", new Gson().toJson(trajet ));
                                                trajetsTermines.add(new TrajetCard(vars.getUser(), trajet));
                                                listeTrajetsTermines.setVisibility(View.VISIBLE);
                                                root.findViewById(R.id.emptyTermines).setVisibility(View.GONE);
                                                Log.w("Liste des trajets", trajetsTermines.toString());
                                                listeTrajetsTermines.scrollToPosition(trajetsTermines.size() );
                                                listeTrajetsAdapterTermines.notifyItemInserted(trajetsTermines.size());
                                                listeTrajetsAdapterTermines.notifyDataSetChanged();
                                            }
                                            else{
                                                Log.d("trajet de l'utilisateur", new Gson().toJson(trajet ));
                                                trajetsEnCours.add(new TrajetCard(vars.getUser(), trajet));
                                                listeTrajetsEnCours.setVisibility(View.VISIBLE);
                                                root.findViewById(R.id.emptyEnCours).setVisibility(View.GONE);
                                                Log.w("Liste des trajets", trajetsEnCours.toString());
                                                listeTrajetsEnCours.scrollToPosition(trajetsEnCours.size() );
                                                listeTrajetsAdapterEnCours.notifyItemInserted(trajetsEnCours.size());
                                                listeTrajetsAdapterEnCours.notifyDataSetChanged();
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    // Getting Post failed, log a message
                                    Log.w("Liste trajets", "loadUser:onCancelled", databaseError.toException());
                                    // ...
                                }
                            };
                            trajetRef.addValueEventListener(userListener);

                        }
                        else{
                            Toast.makeText(vars, "Aucun trajet pour cet utilisateur", Toast.LENGTH_SHORT).show();
                        }
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("trajets", "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        DatabaseReference usersRef = ref.child(vars.getUser().getUid()+ "/trajets");
        usersRef.addValueEventListener(userListener);



        //final TextView textView = root.findViewById(R.id.text_gallery);
        /*
        nouveauTrajetViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }

}