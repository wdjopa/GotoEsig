package fr.tchatat.gotoesig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth= FirebaseAuth.getInstance();
    private String TAG = "connexion";
    private Button inscription, connexion;
// ...
// Initialize Firebase Auth
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent accueil = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(accueil);
    }
    @Override
    public void onClick(View view) {
        if (view == inscription){
            Intent register = new Intent(this, Inscription.class);
            startActivity(register);
        } else if (view == connexion) {
            Log.d(TAG, "Connexion");

            String email = ((EditText) findViewById(R.id.etEmailConnexion)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPassConnexion)).getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            String errorCode = ((FirebaseAuthException) e).getErrorCode();
                            String errorMessage = e.getMessage();
                            Log.d(TAG, errorCode);
                            Log.d(TAG, errorMessage);
                        }
                    })
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(home);
                            } else {
                                try {
                                    throw task.getException();

                                } catch(Exception e) {
                                    Log.e(TAG, e.getMessage());
                                }
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });

        }
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Gson gson = new Gson();
        String currentUserJson = gson.toJson(currentUser);
        if (currentUser != null) {

            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
            Toast.makeText(this, "Déjà connecté", Toast.LENGTH_SHORT).show();
        }
        Log.d("Connexion", currentUserJson);
//        updateUI(currentUser);
    }*/
}
