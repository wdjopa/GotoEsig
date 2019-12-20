package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth = new FirebaseAuth(FirebaseApp.getInstance());

    private Button inscription, connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inscription = findViewById(R.id.btnRegister);
        inscription.setOnClickListener(this);

        connexion = findViewById(R.id.btnValidateConnexion);
        connexion.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == inscription){
            Intent register = new Intent(this, Inscription.class);
            startActivity(register);
        } else if (view == connexion) {
            Intent home = new Intent(this, HomeActivity.class);
            startActivity(home);
        }
    }

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
    }
}
