package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import fr.tchatat.gotoesig.models.User;

public class LauncherActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "lancement";
    private Global vars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        vars = (Global) getApplicationContext();


    }
    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = FirebaseAuth.getInstance().getUid();
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("userStart", new Gson().toJson(user));
                    vars.setUser(user);
                    updateUI(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                    // ...
                }
            };
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
            DatabaseReference usersRef = ref.child(uid + "/account");
            usersRef.addValueEventListener(userListener);

        }
        else {
            updateUI(null);
        }
    }

    public void updateUI(User user) {
        if (user == null) {
            mAuth.signOut();
            Intent connexion = new Intent(LauncherActivity.this, LoginActivity.class);
            startActivity(connexion);
        } else {
            Intent accueil = new Intent(LauncherActivity.this, HomeActivity.class);
            accueil.putExtra("user", user);
            startActivity(accueil);
        }
    }

}
