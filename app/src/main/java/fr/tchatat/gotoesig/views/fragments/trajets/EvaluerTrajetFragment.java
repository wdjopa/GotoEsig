package fr.tchatat.gotoesig.views.fragments.trajets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import fr.tchatat.gotoesig.R;

public class EvaluerTrajetFragment extends Fragment {

    private NouveauTrajetViewModel nouveauTrajetViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        nouveauTrajetViewModel =
                ViewModelProviders.of(this).get(NouveauTrajetViewModel.class);
        View root = inflater.inflate(R.layout.fragment_evaluer, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        /*nouveauTrajetViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}