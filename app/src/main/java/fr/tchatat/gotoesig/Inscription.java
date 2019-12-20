package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Inscription extends AppCompatActivity implements View.OnClickListener {

    private Button inscription, connexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        inscription = findViewById(R.id.btnValidateInscription);
        inscription.setOnClickListener(this);

        connexion = findViewById(R.id.btnConnect);
        connexion.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == inscription){
            Intent register = new Intent(this, HomeActivity.class);
            startActivity(register);
        } else if (view == connexion) {
            Intent home = new Intent(this, MainActivity.class);
            startActivity(home);
        }
    }
}
