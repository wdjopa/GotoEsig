package fr.tchatat.gotoesig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Inscription extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "Inscription";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        mAuth = FirebaseAuth.getInstance();

        Button inscriptionBtn = findViewById(R.id.btnValidateInscription);
        inscriptionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = findViewById(R.id.etEmail).toString();
                String pseudo = findViewById(R.id.etPseudo).toString();
                String password = findViewById(R.id.etPass).toString();
                String confirm = findViewById(R.id.etConfirm).toString();
                if (!email.equals("") && !pseudo.equals("") && !password.equals("") && !confirm.equals("")) {
                    if (password.equals(confirm)) {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(Inscription.this, new OnCompleteListener<AuthResult>() {
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
                                });
                    } else {

                    }
                } else {

                }


            }
        });

    }

    public void updateUI(FirebaseUser user) {


    }

}
