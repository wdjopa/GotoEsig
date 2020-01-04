package fr.tchatat.gotoesig.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import fr.tchatat.gotoesig.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.TrajetAdapter;
import fr.tchatat.gotoesig.models.Trajet;
import fr.tchatat.gotoesig.models.TrajetCard;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.ui.profile.profile.ProfileFragment;
import fr.tchatat.gotoesig.ui.roads.MesTrajetsFragment;
import fr.tchatat.gotoesig.ui.roads.NouveauTrajetFragment;
import fr.tchatat.gotoesig.ui.roads.RechercheTrajetFragment;
import fr.tchatat.gotoesig.ui.roads.TrajetMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private CardView mesTrajets, ajoutTrajet, monCompte, recherche;


    private EditText etPoint;
    private SearchView searchView;
    private RecyclerView resultats;
    private TrajetAdapter resultatsAdapter;
    private ArrayList<TrajetCard> results = new ArrayList<TrajetCard>();
    private TrajetAdapter.onClickInterface onclickInterface;
    private Handler handler;
    private ProgressDialog dialog;

    Global vars;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home_2, container, false);
        getActivity().setTitle("Accueil");



        searchView = root.findViewById(R.id.searchView);
//        etDate = root.findViewById(R.id.etDateSearch);
        resultats = root.findViewById(R.id.resultatsRecherche);

        resultatsAdapter = new TrajetAdapter(getActivity(), results, new TrajetAdapter.onClickInterface() {
            @Override
            public void onItemClick(TrajetCard item) {
                Intent intent = new Intent(getActivity(), TrajetMap.class);
                intent.putExtra("trajet", item);
                startActivity(intent);
                Toast.makeText(getActivity(),item.toString(),Toast.LENGTH_LONG).show();
            }
        });
        resultats.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultats.setAdapter(resultatsAdapter);


        search("", "");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(searchView.getQuery().toString(), "");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(searchView.getQuery().toString(), "");
                return false;
            }
        });

        FloatingActionButton fab = root.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new NouveauTrajetFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

/*
        mesTrajets = root.findViewById(R.id.mesTrajetsBtn);
        mesTrajets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MesTrajetsFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        recherche = root.findViewById(R.id.rechercheAccueil);
        recherche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new RechercheTrajetFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        monCompte = root.findViewById(R.id.profileAccueilBtn);
        monCompte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ProfileFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


        /*
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
/*
        ajoutTrajet = root.findViewById(R.id.propTrajetBtn);
        ajoutTrajet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Fragment fragment = new NouveauTrajetFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
         //       fragmentManager.popBackStack();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });*/
        return root;
    }


    private void search(final String adress, String date){
        final DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        Query trajetsQuery = databaseRef.child("trajets");
        results.clear();
        if (!date.matches("")) trajetsQuery = trajetsQuery.orderByChild("date").equalTo(date);
        dialog = ProgressDialog.show(getActivity(), "","Recherche ..." , true,true);
        dialog.show();
        handler = new Handler();
        handler.postDelayed(new Runnable() {public void run() {                                dialog.dismiss();
        }}, 3000);
        trajetsQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot trajet : dataSnapshot.getChildren()) {
                    final Trajet t = trajet.child("trajet").getValue(Trajet.class);
                    final int nombre = Integer.parseInt(String.valueOf(trajet.child("participants").getChildrenCount()));

                    if (t.getAdresse().contains(adress.toLowerCase())) {
                        Query userQuery = databaseRef.child("users/"+t.getUid()+"/account");
                        userQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);

                                String dtStart = t.getDate()+"T"+t.getHeure()+"Z";
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm'Z'");
                                Date date = null;
                                try {
                                    date = format.parse(dtStart);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if(!(new Date().after(date))){
                                    results.add(new TrajetCard(u, t, nombre));
                                    Log.d("result", adress);
                                    Log.d("result", new Gson().toJson(results));
                                    resultats.scrollToPosition(results.size());
                                    resultatsAdapter.notifyItemInserted(results.size());
                                    resultatsAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                dialog.dismiss();

                            }
                        });
                    }
                    else{
                        dialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();

            }
        });

    }


}