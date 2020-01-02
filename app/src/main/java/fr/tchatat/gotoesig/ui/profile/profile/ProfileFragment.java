package fr.tchatat.gotoesig.ui.profile.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import fr.tchatat.gotoesig.HomeActivity;
import fr.tchatat.gotoesig.MainActivity;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;
import fr.tchatat.gotoesig.ui.roads.NouveauTrajetFragment;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private User user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        Intent intent = getActivity().getIntent();
        user = intent.getParcelableExtra("user");
        ImageView avatar = root.findViewById(R.id.avatar);

        Picasso.get().load(user.getProfileImage()).into(avatar);

   /*     avatar.setImageURI(null);
        avatar.setImageURI(Uri.parse(user.getProfileImage()));*/
        Log.d("user", Uri.parse(user.getProfileImage()) + "");
        final TextView pseudo = root.findViewById(R.id.profile_pnom);
        final TextView tel = root.findViewById(R.id.profile_tel);
        final TextView location = root.findViewById(R.id.profile_location);
        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                pseudo.setText(user.getPseudo());
                tel.setText(user.getTel());
                location.setText(user.getAdresse());
//                avatar.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/gotoesig-50c46.appspot.com/o/images%2F53326439-e77d-41cc-81b6-ace6bf45612f?alt=media&token=f6e89453-36a6-4488-aeb7-a3e9b5331247"));
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.drawer_profile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


/*

 //   @Override
    public void onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.drawer_profile, menu);
    }
*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update:
                // User chose the "Settings" item, show the app settings UI...
                Intent i = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(i);
                /*Fragment fragment = new NouveauTrajetFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}