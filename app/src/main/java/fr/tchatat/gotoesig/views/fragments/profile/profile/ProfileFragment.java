package fr.tchatat.gotoesig.views.fragments.profile.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import fr.tchatat.gotoesig.tools.Global;
import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private User user;
    private View root;
    private Uri pp = null;
    private Global vars;

    TextView pseudo ;
    TextView tel ;
    TextView location ;
    ImageView avatar;
    RatingBar note;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        getActivity().setTitle("Profil");
        setHasOptionsMenu(true);
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        vars = (Global) getActivity().getApplicationContext();

        pseudo = root.findViewById(R.id.profile_pnom);
        tel = root.findViewById(R.id.profile_tel);
        location = root.findViewById(R.id.profile_location);
        note = root.findViewById(R.id.noteGlobale);


        Intent intent = getActivity().getIntent();
        user = intent.getParcelableExtra("user");
        avatar = root.findViewById(R.id.avatar);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);

            }
        });
        if (user.getProfileImage() != null && !user.getProfileImage().equals("")) {
            Picasso.get().load(user.getProfileImage()).into(avatar);
        }
   /*     avatar.setImageURI(null);
        avatar.setImageURI(Uri.parse(user.getProfileImage()));*/
        Log.d("user", Uri.parse(user.getProfileImage()) + "");
        if (user.getTel().trim().length() == 0) {
            if (ActivityCompat.checkSelfPermission(root.getContext(),READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager tMgr = (TelephonyManager)   getContext().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();

                if(!mPhoneNumber.equals("")){

                    user.setTel(mPhoneNumber);
                    String uid = FirebaseAuth.getInstance().getUid();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                    DatabaseReference usersRef = ref.child(uid + "/account");
                    usersRef.setValue(user);
                }
            } else {
                requestPermission();
            }

        }
        Log.e("tel", user.toString());
       // Toast.makeText(root.getContext(), "hors"+user.getTel(), Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(root.getContext(),ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                Location lastKnownLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocationGPS != null) {
                    Log.d("positions", lastKnownLocationGPS.getLatitude()+" ---- "+lastKnownLocationGPS.getLongitude());
                    getAddressFromLocation(lastKnownLocationGPS.getLatitude(), lastKnownLocationGPS.getLongitude());
                } else {
                    Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                    Log.d("positions", loc.getLatitude()+" -- "+loc.getLongitude());

                    getAddressFromLocation(loc.getLatitude(), loc.getLongitude());
                }
            }
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, 100);
            }
        }


        profileViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                pseudo.setText(user.getPseudo());
                tel.setText(user.getTel());
                location.setText(user.getAdresse());
                note.setRating(vars.note/vars.parmoi);
//                avatar.setImageURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/gotoesig-50c46.appspot.com/o/images%2F53326439-e77d-41cc-81b6-ace6bf45612f?alt=media&token=f6e89453-36a6-4488-aeb7-a3e9b5331247"));
            }
        });
        return root;
    }


    private void getAddressFromLocation(double latitude, double longitude) {

        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());


        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address fetchedAddress = addresses.get(0);

                user.setAdresse(fetchedAddress.getAddressLine(0));
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                DatabaseReference usersRef = ref.child(uid + "/account");
                usersRef.setValue(user);
                location.setText(user.getAdresse());

            }else{
                user.setAdresse(latitude+", "+longitude);
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                DatabaseReference usersRef = ref.child(uid + "/account");
                usersRef.setValue(user);
                location.setText(user.getAdresse());

            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Could not get address..!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("permission", "Dedans"+Build.VERSION_CODES.M+"<>"+Build.VERSION.SDK_INT);

            requestPermissions(new String[]{READ_PHONE_STATE}, 100);
        }
        else{
            Log.d("permission", Build.VERSION_CODES.M+"<>"+Build.VERSION.SDK_INT);
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                TelephonyManager tMgr = (TelephonyManager)  getContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(getContext(), READ_SMS) !=
                        PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                        READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED  &&
                        ActivityCompat.checkSelfPermission(getContext(), READ_PHONE_STATE) !=      PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                String mPhoneNumber = tMgr.getLine1Number();

                user.setTel(mPhoneNumber);
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                DatabaseReference usersRef = ref.child(uid + "/account");
                usersRef.setValue(user);
                break;
        }
    }


    @Override
    public void onResume() {

        super.onResume();
        getActivity().setTitle("Profil");

    }

    // Call Back method  to get the Message from other Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2) {
            user = data.getParcelableExtra("user");
            ImageView avatar = root.findViewById(R.id.avatar);
            if (user.getProfileImage() != null && !user.getProfileImage().equals("")) {
                Picasso.get().load(user.getProfileImage()).into(avatar);
            }
   /*     avatar.setImageURI(null);
        avatar.setImageURI(Uri.parse(user.getProfileImage()));*/
            Log.d("user", "On Resume : " + Uri.parse(user.getProfileImage()) + "");
            if (user.getTel().equals("")) {

                TelephonyManager tMgr = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (getContext().checkSelfPermission(READ_SMS) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && getContext().checkSelfPermission(READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                }
                String mPhoneNumber = tMgr.getLine1Number();
                user.setTel(mPhoneNumber);
                String uid = FirebaseAuth.getInstance().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                DatabaseReference usersRef = ref.child(uid + "/account");
                usersRef.setValue(user);

            }
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
        else if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("Register Activity", "Une photo a été sélectionnée");

            pp = data.getData();

            avatar.setImageURI(null);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), pp);
                //      Picasso.get().load(getImageUri(getApplicationContext(), bitmap)).into(profile_picture_btn);

            } catch (IOException e) {
                e.printStackTrace();
            }
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap();

            avatar.setImageBitmap(bitmap);

            String filename = UUID.randomUUID().toString();
            final StorageReference riversRef = FirebaseStorage.getInstance().getReference("/images/" + filename);
            UploadTask uploadTask = riversRef.putFile(pp);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("image", downloadUri.toString());

                        String uid = FirebaseAuth.getInstance().getUid();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
                        DatabaseReference usersRef = ref.child(uid + "/account");
                        user.setProfileImage(downloadUri.toString());
                        usersRef.setValue(user);
                        Toast.makeText(getContext(), "Votre photo a été modifée avec succès.", Toast.LENGTH_SHORT).show();

                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(getContext(), "L'upload a échoué", Toast.LENGTH_SHORT).show();

                    }
                }
            });
//                profile_picture_btn.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profile_picture_btn.setBackgroundDrawable(bitmapDrawable)
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