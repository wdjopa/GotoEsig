package fr.tchatat.gotoesig.authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import fr.tchatat.gotoesig.tools.Global;
import fr.tchatat.gotoesig.views.HomeActivity;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;

public class Inscription extends AppCompatActivity implements   View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String TAG = "Inscription";
    private String email = null;
    private String pseudo = null;
    private Global vars;
    final ProgressDialog dialog = ProgressDialog.show(Inscription.this, "","Inscription en cours ..." , true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);
        vars = (Global) getApplicationContext();

        findViewById(R.id.btnValidateInscription).setOnClickListener(this);
        findViewById(R.id.btnConnect).setOnClickListener(this);

    }

    private void createAccount(final String email, String password) {
    //    Log.d(TAG, "createAccount:" + email);
        dialog.show();
//        showProgressBar();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
               //             Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(Inscription.this, "Votre compte a été créé avec succès.",
                                    Toast.LENGTH_SHORT).show();

                            //Sauvegarde de l'utilisateur dans Firebase
                            String uid = FirebaseAuth.getInstance().getUid();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");

                            DatabaseReference usersRef = ref.child(uid + "/account");
                            User user = new User(uid);
                            user.setEmail(email);
                            user.setPseudo(pseudo);
                 //           Log.d("User", new Gson().toJson(user));
                            Map<String, User> users = new HashMap<>();
                            users.put(uid, user);

                            usersRef.setValue(user);
                            vars.setUser(user);

                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
              //              Log.w(TAG, "createUserWithEmail:failure", task.getException());
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

    private void updateUI(User user) {
//        hideProgressBar();

        dialog.dismiss();
        if (user != null) {
       //     Log.e("REUSSITE", "Inscription reussite");
            Intent accueil = new Intent(Inscription.this, HomeActivity.class);
            accueil.putExtra("user", user);
            startActivity(accueil);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnValidateInscription) {
            email = ((EditText) findViewById(R.id.etEmail)).getText().toString();
            pseudo = ((EditText) findViewById(R.id.etPseudo)).getText().toString();
            String password = ((EditText) findViewById(R.id.etPass)).getText().toString();
            String confirm = ((EditText) findViewById(R.id.etConfirm)).getText().toString();

        //    Log.d("champs", email);
          //  Log.d("champs", pseudo);
          //  Log.d("champs", password);
         //   Log.d("champs", confirm);
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
            Intent connexion = new Intent(Inscription.this, LoginActivity.class);
            startActivity(connexion);
        }
    }
}
