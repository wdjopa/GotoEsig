package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.tchatat.gotoesig.models.AvisTrajet;
import fr.tchatat.gotoesig.models.AvisTrajetCard;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;

public class EvaluerTrajetActivity extends AppCompatActivity {
    TrajetCard trajet;

    RecyclerView avisRecycle;
    AvisAdapter avisAdapter;
    ArrayList<AvisTrajetCard> avis= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluer_trajet);

        Intent intent = getIntent();
        trajet = intent.getParcelableExtra("trajet");
        if(intent.getParcelableExtra("type").equals("laisser")){
            (findViewById(R.id.ratings_layout)).setVisibility(View.GONE);
            (findViewById(R.id.rating_layout)).setVisibility(View.VISIBLE);
        }
        else{
            afficherListe();
            (findViewById(R.id.ratings_layout)).setVisibility(View.VISIBLE);
            (findViewById(R.id.rating_layout)).setVisibility(View.GONE);
        }

        Picasso.get().load(trajet.getUser().getProfileImage()).into(((ImageView) findViewById(R.id.avatar_user_1_avis)));
        ((TextView) findViewById(R.id.myroads_current_departure_location_1_avis)).setText(trajet.getTrajet().getAdresse());
        ((TextView) findViewById(R.id.myroads_current_departure_date_1_avis)).setText(trajet.getTrajet().getDate()+" "+trajet.getTrajet().getHeure());
        ((TextView) findViewById(R.id.myroads_current_departure_mode_1_avis)).setText(trajet.getTrajet().getMoyen());
        if(trajet.getTrajet().getContribution()>0)
            ((TextView) findViewById(R.id.myroads_current_departure_price_1_avis)).setText(trajet.getTrajet().getContribution()+"€");

        int places = trajet.getTrajet().getNombre();
        ((TextView) findViewById(R.id.myroads_current_departure_places_1_avis)).setText(trajet.total()+"/"+places+" Place"+(places>1?"s":""));
        ((TextView) findViewById(R.id.myroads_current_departure_username_1_avis)).setText(trajet.getUser().getPseudo());


        findViewById(R.id.button_envoyer_avis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets/"+trajet.getTrajet().getId() + "/avis");
                AvisTrajet avis = new AvisTrajet(((EditText) findViewById(R.id.avis_message)).getText().toString(), new Date());
                ref.setValue(avis);
                afficherListe();
                Toast.makeText(EvaluerTrajetActivity.this, "Avis enregistré", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void afficherListe(){
        avisRecycle= findViewById(R.id.avis_trajets);
        avisAdapter = new AvisAdapter(this, avis);
        avisRecycle.setLayoutManager(new LinearLayoutManager(this));
        avisRecycle.setAdapter(avisAdapter);

        DatabaseReference avisRef = FirebaseDatabase.getInstance().getReference("/trajets/"+trajet.getTrajet().getId()+"/avis");

        ValueEventListener avisListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot lesAvis : dataSnapshot.getChildren()) {
                    AvisTrajet a = lesAvis.getValue(AvisTrajet.class);
                    if(avis != null){
//                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm'Z'");
                        //try {
/*
                            Log.d("trajet de l'utilisateur", new Gson().toJson(trajet ));

                            avis.add(new AvisTrajet(a));
                            listeTrajetsEnCours.setVisibility(View.VISIBLE);
                            root.findViewById(R.id.emptyEnCours).setVisibility(View.GONE);
                            Log.w("Liste des trajets", avis.toString());
                            listeTrajetsEnCours.scrollToPosition(avis.size() );
                            avisAdapter.notifyItemInserted(avis.size());
                            avisAdapter.notifyDataSetChanged();
    /*
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
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
        avisRef.addValueEventListener(avisListener);

    }
}
