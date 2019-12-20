package fr.tchatat.gotoesig.ui.roads;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import fr.tchatat.gotoesig.R;

public class MesTrajetsFragment extends Fragment {

    private MesTrajetsViewModel mesTrajetsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mesTrajetsViewModel = ViewModelProviders.of(this).get(MesTrajetsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mes_trajets, container, false);
        getActivity().setTitle("Mes Trajets");

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