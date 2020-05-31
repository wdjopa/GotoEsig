package fr.tchatat.gotoesig.authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import fr.tchatat.gotoesig.tools.Global;
import fr.tchatat.gotoesig.views.HomeActivity;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "connexion";
    private Button inscription, connexion;
    private Global vars;

    // ...
// Initialize Firebase Auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vars = (Global) getApplicationContext();


        inscription = findViewById(R.id.btnRegister);
        inscription.setOnClickListener(this);

        connexion = findViewById(R.id.btnValidateConnexion);
        connexion.setOnClickListener(this);
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
                //    Log.d("userStart", new Gson().toJson(user));
                    vars.setUser(user);
                    updateUI(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                //    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                    // ...
                }
            };
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
            DatabaseReference usersRef = ref.child(uid + "/account");
            usersRef.addValueEventListener(userListener);

        }
    }

    public void updateUI(User user) {
        if (user == null) {
            mAuth.signOut();

        } else {

            Intent accueil = new Intent(LoginActivity.this, HomeActivity.class);
            accueil.putExtra("user", user);
            startActivity(accueil);
        }
    }
    @Override
    public void onClick(View view) {
        if (view == inscription){
            Intent register = new Intent(this, Inscription.class);
            startActivity(register);
        } else if (view == connexion) {
         //   Log.d(TAG, "Connexion");
            String email = ((EditText) findViewById(R.id.etEmailConnexion)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPassConnexion)).getText().toString();

            final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "","Connexion en cours ..." , true);
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {public void run() {}}, 3000);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorCode = ((FirebaseAuthException) e).getErrorCode();
                            String errorMessage = e.getMessage();
                          //  Log.d(TAG, errorCode);
                          //  Log.d(TAG, errorMessage);
                            Toast.makeText(vars, errorMessage, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                           //     Log.d(TAG, "signInWithEmail:success");
                                String uid = FirebaseAuth.getInstance().getUid();
                                ValueEventListener userListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                //        Log.d("userStart", new Gson().toJson(user));
                                        vars.setUser(user);
                                        dialog.dismiss();
                                        updateUI(user);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        // Getting Post failed, log a message
                                    //    Log.w(TAG, "loadUser:onCancelled", databaseError.toException());
                                        // ...
                                    }
                                };
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                                DatabaseReference usersRef = ref.child(uid + "/account");
                                usersRef.addValueEventListener(userListener);
                            } else {
                                try {
                                    throw task.getException();

                                } catch(Exception e) {
                                //    Log.e(TAG, e.getMessage());
                                }
                                dialog.dismiss();

                                // If sign in fails, display a message to the user.
                             //   Log.w(TAG, "signInWithEmail:failure", task.getException());

                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }
    }
}
