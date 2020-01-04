package fr.tchatat.gotoesig;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import fr.tchatat.gotoesig.models.AvisTrajet;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.models.UserTrajet;

public class Global extends Application {

    private User user = new User();
    public int trajets=0;
    public int parmoi=0;
    public float score=0;
    public float montant=0;
    public float note =0;

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


}
