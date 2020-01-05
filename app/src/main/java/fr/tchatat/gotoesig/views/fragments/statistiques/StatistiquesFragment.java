package fr.tchatat.gotoesig.views.fragments.statistiques;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.tchatat.gotoesig.tools.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.AvisTrajet;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.UserTrajet;

public class StatistiquesFragment extends Fragment {

    private StatistiquesViewModel sendViewModel;
    private int trajets = 0, parmoi=0;
    private float montant,note;
    Global vars;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sendViewModel =
                ViewModelProviders.of(this).get(StatistiquesViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_statistiques, container, false);
        vars = (Global) getActivity().getApplicationContext();





        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {

                    UserTrajet userTrajet = new UserTrajet();
                    userTrajet.setId(snapshot.child("id").getValue().toString());
                    if(userTrajet.getId() != null){
                        trajets++;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets");
                        DatabaseReference trajetRef = ref.child(userTrajet.getId());

                        ValueEventListener userListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Trajet trajet = dataSnapshot.child("trajet").getValue(Trajet.class);
                                if(trajet != null){
                                    montant+=trajet.getContribution();
                                    for (DataSnapshot lesAvis : dataSnapshot.child("avis").getChildren()) {
                                        AvisTrajet avis = lesAvis.getValue(AvisTrajet.class);

                                        Toast.makeText(vars, trajet.getUid()+"---"+vars.getUser().getUid(), Toast.LENGTH_SHORT).show();
                                        if(avis != null && trajet.getUid().equals(vars.getUser().getUid())) {
                                            note+=avis.getNote();
                                            parmoi++;

                                            TextView t1 = root.findViewById(R.id.trajet_realises);
                                            t1.setText(trajets+"");
                                            TextView t2 = root.findViewById(R.id.montant_percu);
                                            t2.setText(montant+" €");
                                            TextView t3 = root.findViewById(R.id.nombre_avis);
                                            t3.setText("NOTE MOYENNE ("+parmoi+" Avis)");
                                            RatingBar r = root.findViewById(R.id.note);
                                            r.setRating(note/parmoi);
                                        }
                                    }
                                }

                                   /* String dtStart = trajet.getDate()+"T"+trajet.getHeure()+"Z";
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
                                    }*/
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


        return root;
    }
}