package fr.tchatat.gotoesig.ui.roads;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import fr.tchatat.gotoesig.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.models.UserTrajet;

public class MesTrajetsFragment extends Fragment {

    private MesTrajetsViewModel mesTrajetsViewModel;
    RecyclerView listeTrajets;
    TrajetAdapter listeTrajetsAdapter;
    ArrayList<TrajetCard> trajets = new ArrayList<TrajetCard>();
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
        listeTrajets = root.findViewById(R.id.trajets_list);
        listeTrajetsAdapter = new TrajetAdapter(getActivity(), trajets);

        listeTrajets.setLayoutManager(new LinearLayoutManager(getActivity()));
        listeTrajets.setAdapter(listeTrajetsAdapter);


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

                                        Log.d("trajet de l'utilisateur", new Gson().toJson(trajet ));
                                        trajets.add(new TrajetCard(vars.getUser(), trajet));
                                        Log.w("Liste des trajets", trajets.toString());
                                        listeTrajets.scrollToPosition(trajets.size() );
                                        listeTrajetsAdapter.notifyItemInserted(trajets.size());
                                        listeTrajetsAdapter.notifyDataSetChanged();
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