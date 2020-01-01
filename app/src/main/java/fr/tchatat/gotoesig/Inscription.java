package fr.tchatat.gotoesig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inscription extends AppCompatActivity implements   View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "Inscription";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        findViewById(R.id.btnValidateInscription).setOnClickListener(this);
        findViewById(R.id.btnConnect).setOnClickListener(this);

    }

    public void register(String email, String password){

        mAuth.signOut();
        Log.d(TAG, "createUserWithEmail:attente");
        Log.w(TAG,mAuth.getFirebaseAuthSettings().toString());
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, e.getMessage());
                }
            })
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(Inscription.this, "Authentication en cours.",
                            Toast.LENGTH_SHORT).show();
                   // Log.d("FirebaseAuth", "onComplete" + task.getException().getMessage());
                   // Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        Intent home = new Intent(Inscription.this, HomeActivity.class);
                        startActivity(home);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(Inscription.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
//                                            updateUI(null);
                    }
                    // [START_EXCLUDE]
                    //hideProgressDialog();
                    // [END_EXCLUDE]
                }
            })
            .addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Log.d(TAG, authResult.getUser().getEmail());
                }
            });

       /* mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent home = new Intent(Inscription.this, HomeActivity.class);
                            startActivity(home);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Inscription.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                                            updateUI(null);
                        }

                        // ...
                    }
                });*/
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

//        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Inscription.this, "Votre compte a été créé avec succès.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Inscription.this, "Inscription échouée. Adresse email déjà utilisée",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private void updateUI(FirebaseUser user) {
//        hideProgressBar();
        Log.e("REUSSITE", "Inscription reussite");
        Intent accueil = new Intent(Inscription.this, HomeActivity.class);
        startActivity(accueil);
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnValidateInscription) {
            String email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
            String pseudo = ((EditText) findViewById(R.id.etPseudo)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPass)).getText().toString();
            String confirm = ((EditText) findViewById(R.id.etConfirm)).getText().toString();

            Log.d("champs", email);
            Log.d("champs", pseudo);
            Log.d("champs", password);
            Log.d("champs", confirm);
            if (!email.equals("") && !pseudo.equals("") && !password.equals("") && !confirm.equals("")) {
                if (password.equals(confirm)) {
//                    register(email, password);
                    createAccount(email, password);

                } else {
                    Toast.makeText(Inscription.this, "Les mots de passe ne sont pas égaux", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Inscription.this, "L'un des champs est vide", Toast.LENGTH_SHORT).show();

            }
        } else if (i == R.id.btnConnect) {
            Intent connexion = new Intent(Inscription.this, MainActivity.class);
            startActivity(connexion);
        }
    }
}
