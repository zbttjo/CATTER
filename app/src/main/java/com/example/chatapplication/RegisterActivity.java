package com.example.chatapplication;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    Uri imageUri;

    CircleImageView imageViewCircle;
    EditText edt_email, edt_password, edt_username;
    Button btn_signIn;
    boolean imageControl = false;

    String folderName = "image/*";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imageViewCircle = findViewById(R.id.circleImageView);

        // textfield
        edt_email = findViewById(R.id.edt_register_email);
        edt_password = findViewById(R.id.edt_register_password);
        edt_username = findViewById(R.id.edt_register_username);

        // button
        btn_signIn = findViewById(R.id.btn_register_register);


        imageViewCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_image();
            }
        });

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edt_email.getText().toString();
                String password = edt_password.getText().toString();
                String usernane = edt_username.getText().toString();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(usernane)) {

                } else {
                    signin_user(email, password, usernane);
                }

            }
        });

    }

    public void signin_user(final String email, String password, String username) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("userName").setValue(username);

                    upload_image_to_storage(username);

                } else {
                    Toast.makeText(RegisterActivity.this, "Error creating the user", Toast.LENGTH_LONG).show();
                }
            }
        });
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
                            reference.child("Users").child(auth.getUid()).child("image").setValue(filePath)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.i("FIRESTORAGE", "Success writing to database");
                                    Toast.makeText(RegisterActivity.this, "Writing url to database is success", Toast.LENGTH_SHORT).show();
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("FIRESTORAGE", "Error writing to database");
                                    Toast.makeText(RegisterActivity.this, "Failed url to database is success", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });


        } else {
            reference.child("Users").child(auth.getUid()).child("image").setValue("null");
        }

        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void choose_image(){
        Intent intent = new Intent();
        intent.setType(folderName);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageViewCircle);
            imageControl = true;
        }
    }
}