package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
    Global vars;
    RecyclerView avisRecycle;
    AvisAdapter avisAdapter;
    ArrayList<AvisTrajetCard> avis= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluer_trajet);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Evaluer un trajet");

        vars = (Global) getApplicationContext();
        Intent intent = getIntent();
        trajet = intent.getParcelableExtra("trajet");
        Log.d("extract", intent.getExtras().getString("type").toString());
        Boolean check = intent.getExtras().getString("type").equals("laisser");


        avisRecycle= findViewById(R.id.avis_trajets);
        avisAdapter = new AvisAdapter(this, avis);
        avisRecycle.setLayoutManager(new LinearLayoutManager(this));
        avisRecycle.setAdapter(avisAdapter);

       if(check){
            (findViewById(R.id.ratings_layout)).setVisibility(View.GONE);
            (findViewById(R.id.rating_layout)).setVisibility(View.VISIBLE);
        }
        else{

           afficherListe();
       }

        Picasso.get().load(trajet.getUser().getProfileImage()).into(((ImageView) findViewById(R.id.avatar_user_1_avis)));
        ((TextView) findViewById(R.id.myroads_current_departure_location_1_avis)).setText(trajet.getTrajet().getAdresse());
        ((TextView) findViewById(R.id.myroads_current_departure_date_1_avis)).setText(trajet.getTrajet().getDate()+" "+trajet.getTrajet().getHeure());
        ((TextView) findViewById(R.id.myroads_current_departure_mode_1_avis)).setText(trajet.getTrajet().getMoyen());
        if(trajet.getTrajet().getContribution()>0)
            ((TextView) findViewById(R.id.myroads_current_departure_price_1_avis)).setText(trajet.getTrajet().getContribution()+"€");

        int places = trajet.getTrajet().getNombre();
        ((TextView) findViewById(R.id.myroads_current_departure_places_1_avis)).setText(trajet.getNombre()+"/"+places+" Place"+(places>1?"s":""));
        ((TextView) findViewById(R.id.myroads_current_departure_username_1_avis)).setText(trajet.getUser().getPseudo());


        findViewById(R.id.button_envoyer_avis).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets/"+trajet.getTrajet().getId() + "/avis/");
                DatabaseReference aRef = ref.child(ref.push().getKey());
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy 'à' hh:mm");

                String dateString = format.format( new Date()   );
                AvisTrajet avis = new AvisTrajet(((EditText) findViewById(R.id.avis_message)).getText().toString(), dateString,((RatingBar)findViewById(R.id.ratingBar_avis)).getRating(), vars.getUser());
                aRef.setValue(avis);
                afficherListe();
                ((EditText) findViewById(R.id.avis_message)).setText("");
                Toast.makeText(EvaluerTrajetActivity.this, "Avis enregistré", Toast.LENGTH_SHORT).show();
            }
        });


    }



    public boolean onOptionsItemSelected(MenuItem item){
        finish();
      /*  Intent myIntent = new Intent(this, MainActivity.class);
        startActivityForResult(myIntent, 0);*/
        return true;
    }

    private void afficherListe(){
        (findViewById(R.id.ratings_layout)).setVisibility(View.VISIBLE);
        (findViewById(R.id.rating_layout)).setVisibility(View.GONE);

        DatabaseReference avisRef = FirebaseDatabase.getInstance().getReference("/trajets/"+trajet.getTrajet().getId()+"/avis");

        ValueEventListener avisListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(vars, "cest bio", Toast.LENGTH_SHORT).show();
                int as= 0;
                float rating = 0;
                for (DataSnapshot lesAvis : dataSnapshot.getChildren()) {
                    Log.w("Liste des avis2", lesAvis.getValue().toString());

                    AvisTrajet a = lesAvis.getValue(AvisTrajet.class);
                    if(a != null){
                        Toast.makeText(vars, "cest bio "+as++, Toast.LENGTH_SHORT).show();

                        /* SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm'Z'");
                        try {

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/

                        avis.add(new AvisTrajetCard(a, trajet.getTrajet(), a.getUser()));
                        Log.w("Liste des avis2", avis.toString());

                        rating+=a.getNote();
                    }
                }

                ((RatingBar)findViewById(R.id.ratingBar_avis_moyen)).setRating(rating/as);
                avisAdapter.notifyItemInserted(avis.size());
                avisAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Liste des avis", "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        avisRef.addValueEventListener(avisListener);

    }
}
