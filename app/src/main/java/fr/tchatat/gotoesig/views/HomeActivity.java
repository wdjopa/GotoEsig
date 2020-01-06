package fr.tchatat.gotoesig.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.tools.Global;
import fr.tchatat.gotoesig.authentification.LoginActivity;
import fr.tchatat.gotoesig.models.AvisTrajet;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.models.UserTrajet;
import fr.tchatat.gotoesig.views.fragments.profile.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AppBarConfiguration mAppBarConfiguration;
    private User user;
    private Global vars;

    private Handler handler = new Handler();
    private ProgressDialog dialog;
    TextView navScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Accueil");
        vars = (Global)getApplicationContext();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( HomeActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String updatedToken = instanceIdResult.getToken();
                Log.e("Updated Token",updatedToken);
                final String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("/users/"+uid+"/token");
                tokenRef.setValue(updatedToken);
            }
        });


        Intent intent = getIntent();
        user = intent.getParcelableExtra("user");
        Log.d("userHome", user.getPseudo());



//        TextView nomPrenom = findViewById(R.id.nomPrenom);
//        nomPrenom.setText(user.getPseudo());
        // ((TextView)findViewById(R.id.pointsHeader)).setText(user.getScore()+" pt(s)");

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.nomPrenom);
        navScore = headerView.findViewById(R.id.pointsHeader);
        ImageView imageAvatar = headerView.findViewById(R.id.profilePicture);
        if(user.getProfileImage() != null && !user.getProfileImage().equals("")){
            String imageUrl = user.getProfileImage();
            if(imageUrl.equals("")){
                imageUrl =  "drawable://" + R.drawable.user;
            }
            Picasso.get().load(imageUrl).into(imageAvatar);
        }
        navUsername.setText(user.getPseudo());
        navScore.setText(vars.note + " pts");
        calculate();

        CircularImageView profilePicture = headerView.findViewById(R.id.profilePicture);
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                getFragmentManager().popBackStack();
                fragmentTransaction.commit();
                drawer.closeDrawers();
            }
        });

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_add_road, R.id.nav_my_roads, R.id.nav_chercher_trajet,
                R.id.nav_profile, R.id.nav_evaluer, R.id.nav_statistiques,R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


    }

    public void calculate(){

        dialog = ProgressDialog.show(HomeActivity.this, "","Chargement ..." , true);
        dialog.show();
        handler.postDelayed(new Runnable() {public void run() {                                dialog.dismiss();
        }}, 3000);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {


                    UserTrajet userTrajet = new UserTrajet();
                    userTrajet.setId(snapshot.child("id").getValue().toString());
                    if(userTrajet.getId() != null){
                        vars.trajets++;
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/trajets");
                        DatabaseReference trajetRef = ref.child(userTrajet.getId());

                        ValueEventListener userListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Trajet trajet = dataSnapshot.child("trajet").getValue(Trajet.class);
                                if(trajet != null){
                                    vars.montant+=trajet.getContribution();
                                    vars.note=0;
                                    vars.parmoi=0;
                                    for (DataSnapshot lesAvis : dataSnapshot.child("avis").getChildren()) {
                                        AvisTrajet avis = lesAvis.getValue(AvisTrajet.class);

                                        if(avis != null && trajet.getUid().equals(vars.getUser().getUid())) {
                                            vars.note+=avis.getNote();
                                            vars.parmoi++;
                                            navScore.setText(vars.note + " pts");

                                        }
                                    }
                                    dialog.dismiss();
                                }
                                else{
                                    dialog.dismiss();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Getting Post failed, log a message
                                Log.w("Liste trajets", "loadUser:onCancelled", databaseError.toException());
                                dialog.dismiss();

                                // ...
                            }
                        };
                        trajetRef.addValueEventListener(userListener);

                    }
                    else{
                        dialog.dismiss();

                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("trajets", "loadUser:onCancelled", databaseError.toException());
                dialog.dismiss();

                // ...
            }
        };
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        DatabaseReference usersRef = ref.child(vars.getUser().getUid()+ "/trajets");
        usersRef.addValueEventListener(userListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                signOut();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void signOut() {
        mAuth.signOut();
        Intent register = new Intent(this, LoginActivity.class);
        startActivity(register);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.w("test", id + "");

        return true;
    }
}
