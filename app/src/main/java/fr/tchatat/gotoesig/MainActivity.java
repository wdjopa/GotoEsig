package fr.tchatat.gotoesig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button inscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inscription = findViewById(R.id.btnRegister);
        inscription.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == inscription){
            Intent register = new Intent(this, AjoutTrajet.class);
            startActivity(register);
        }
    }
}
