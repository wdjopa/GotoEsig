package fr.tchatat.gotoesig.ui.profile.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.squareup.picasso.Picasso;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private User user;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        setHasOptionsMenu(true);
        root = inflater.inflate(R.layout.fragment_profile, container, false);

        Intent intent = getActivity().getIntent();
        user = intent.getParcelableExtra("user");
        ImageView avatar = root.findViewById(R.id.avatar);
        if(user.getProfileImage() != null && !user.getProfileImage().equals("")) {
            Picasso.get().load(user.getProfileImage()).into(avatar);
        }
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
    public void onResume() {
        super.onResume();

    }

    // Call Back method  to get the Message from other Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            user = data.getParcelableExtra("user");
            ImageView avatar = root.findViewById(R.id.avatar);
            if(user.getProfileImage() != null && !user.getProfileImage().equals("")) {
                Picasso.get().load(user.getProfileImage()).into(avatar);
            }
   /*     avatar.setImageURI(null);
        avatar.setImageURI(Uri.parse(user.getProfileImage()));*/
            Log.d("user", "On Resume : "+Uri.parse(user.getProfileImage()) + "");
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
        }
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
                startActivityForResult(i, 2);
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