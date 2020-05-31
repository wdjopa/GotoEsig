package fr.tchatat.gotoesig.ui.profile.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.tchatat.gotoesig.R;
import fr.tchatat.gotoesig.models.User;

public class UpdateProfileActivity extends AppCompatActivity {

    private Uri pp = null;
    private ImageView profile_picture_btn;
    private User user = new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle("Modification du profil");


        profile_picture_btn = findViewById(R.id.ppUpdate);
        profile_picture_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Log.d("Page d'inscription", "Selection de photo");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 0);

            }
        });

        ((Button) findViewById(R.id.updateBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadProfilePicture();
            }
        });

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {


            String uid = FirebaseAuth.getInstance().getUid();
            ValueEventListener userListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);

                    ((EditText) findViewById(R.id.etNomUpdate)).setText(user.getNom());
                    ((EditText) findViewById(R.id.etPrenomUpdate)).setText(user.getPrenom());
                    ((EditText) findViewById(R.id.etPseudoUpdate)).setText(user.getPseudo());
                    ((EditText) findViewById(R.id.etAdresseUpdate)).setText(user.getAdresse());
                    ((EditText) findViewById(R.id.etEmailUpdate)).setText(user.getEmail());
                    ((EditText) findViewById(R.id.etTelUpdate)).setText(user.getTel());
                    if (!user.getProfileImage().equals("")) {
                        // Picasso.get().load(user.getProfileImage()).into(profile_picture_btn);
                    }

                //    Log.d("userStart", new Gson().toJson(user));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                  //  Log.w("Update", "loadUser:onCancelled", databaseError.toException());
                    // ...
                }
            };
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
            DatabaseReference usersRef = ref.child(uid + "/account");
            usersRef.addValueEventListener(userListener);

        }
    }

    private void saveOther(Uri p) {
        //Sauvegarde de l'utilisateur dans Firebase
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users");
        DatabaseReference usersRef = ref.child(uid + "/account");

        user.setNom(((EditText) findViewById(R.id.etNomUpdate)).getText().toString());
        user.setPrenom(((EditText) findViewById(R.id.etPrenomUpdate)).getText().toString());
        user.setPseudo(((EditText) findViewById(R.id.etPseudoUpdate)).getText().toString());
        user.setAdresse(((EditText) findViewById(R.id.etAdresseUpdate)).getText().toString());
        user.setTel(((EditText) findViewById(R.id.etTelUpdate)).getText().toString());
        if (p != null)
            user.setProfileImage(p.toString());
        usersRef.setValue(user);
        Intent intent=new Intent();
        intent.putExtra("user", user);
        setResult(2,intent);
        finish();
    }


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void uploadProfilePicture() {
        if (pp == null) {
            saveOther(null);
            return;
        }
//        Uri file = Uri.fromFile(new File(pp));
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
                 //   Log.d("image", downloadUri.toString());
                    saveOther(downloadUri);
                } else {
                    // Handle failures
                    // ...
                    Toast.makeText(UpdateProfileActivity.this, "L'upload a échoué", Toast.LENGTH_SHORT).show();

                }
            }
        });
        /*
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                saveOther(riversRef.getDownloadUrl());
            }
        });*/
        /*
        StorageReference ref = FirebaseStorage.getInstance().getReference("/images/"+filename);

        ref.putFile(pp)
            .addOnSuccessListener(){
            Log.d("Page inscription", "Image uploadée avec succès à l'adersse ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("Page inscription", "File Location : $it")

                saveUserToFirebase(it.toString())
            }
        }
            .addOnFailureListener{
            Log.d("Page inscription", "Erreur lors de l'upload de l'image")
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
           // Log.d("Register Activity", "Une photo a été sélectionnée");

            pp = data.getData();

            profile_picture_btn.setImageURI(null);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pp);
                //      Picasso.get().load(getImageUri(getApplicationContext(), bitmap)).into(profile_picture_btn);

            } catch (IOException e) {
                e.printStackTrace();
            }
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap();

            profile_picture_btn.setImageBitmap(bitmap);
//                profile_picture_btn.alpha = 0f

//            val bitmapDrawable = BitmapDrawable(bitmap)
//            profile_picture_btn.setBackgroundDrawable(bitmapDrawable)
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
