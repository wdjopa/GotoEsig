package fr.tchatat.gotoesig.ui.roads;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.User;

public class MesTrajetsFragment extends Fragment {

    private MesTrajetsViewModel mesTrajetsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mesTrajetsViewModel = ViewModelProviders.of(this).get(MesTrajetsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mes_trajets, container, false);
        getActivity().setTitle("Mes Trajets");

        String uid = FirebaseAuth.getInstance().getUid();
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 trajet = dataSnapshot.getValue(User.class);
                Log.d("userStart", new Gson().toJson(user));

                String []  etudiants = new String[db.getListEtudiants().size()];
                int i = 0;
                for(Etudiant e : db.getListEtudiants()){
                    Log.e("data", e.toString());
                    etudiants[i]  = e.getNoms();
                    i++;
                }

                ListView lv = findViewById(R.id.listeView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ListEtudiants.this, android.R.layout.simple_list_item_1, etudiants);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("trajets", "loadUser:onCancelled", databaseError.toException());
                // ...
            }
        };
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        DatabaseReference usersRef = ref.child(uid + "/trajets");
        usersRef.addValueEventListener(userListener);

        //final TextView textView = root.findViewById(R.id.text_gallery);
        /*
        nouveauTrajetViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}