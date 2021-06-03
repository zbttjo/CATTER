package com.example.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {



    CircleImageView new_profile_image;
    EditText edt_newName;
    Button btn_update;
    Uri imageUri;
    boolean imageControl = false;



    FirebaseDatabase db;
    DatabaseReference ref;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = FirebaseDatabase.getInstance();
        ref = db.getReference();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        new_profile_image = findViewById(R.id.circleImageView_Profile);
        edt_newName = findViewById(R.id.edt_profile_newName);
        btn_update = findViewById(R.id.btn_profile_update);


        get_user_info();

        new_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_image();
            }
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_profile();
            }
        });
    }


    public void choose_image(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(new_profile_image);
            imageControl = true;
        } else {
            imageControl = false;
        }
    }


    public void update_profile(){
        String username = edt_newName.getText().toString();
        ref.child("Users").child(firebaseUser.getUid()).child("userName").setValue(username);
        upload_image_to_storage(username);
    }

    private void upload_image_to_storage(String username) {
        if (imageControl){
            UUID randomID = UUID.randomUUID();
            String imageName = "images/"+randomID+".jpg";
            storageReference.child(imageName).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                    myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filePath = uri.toString();
                            ref.child("Users").child(auth.getUid()).child("image").setValue(filePath)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.i("FIRESTORAGE_PROFILE", "Success writing to database");
                                            Toast.makeText(ProfileActivity.this, "Writing url to database is success", Toast.LENGTH_SHORT).show();
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("FIRESTORAGE_PROFILE", "Error writing to database");
                                    Toast.makeText(ProfileActivity.this, "Failed url to database is success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });


        } else {
            ref.child("Users").child(auth.getUid()).child("image").setValue("null");
        }

        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }


    public void get_user_info(){
        ref.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("userName").getValue().toString();
                image = snapshot.child("image").getValue().toString();

                edt_newName.setText(name);

                if (image.equals("null")){
                    new_profile_image.setImageResource(R.drawable.user);
                } else {
                    Picasso.get().load(image).into(new_profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}