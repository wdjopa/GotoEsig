package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AjoutTrajet extends AppCompatActivity{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_trajet);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("NOUVEAU TRAJET");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitleMarginStart(400);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent accueil = new Intent(AjoutTrajet.this, MainActivity.class);
                    startActivity(accueil);
                }
            });
        }
    }

}
