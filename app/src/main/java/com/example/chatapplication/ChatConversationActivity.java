package com.example.chatapplication;

import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.adapter.MessageAdapter;
import com.example.chatapplication.model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatConversationActivity extends AppCompatActivity {



    CircleImageView other_user_image;
    ImageView image_view_back;
    TextView txt_name;
    EditText edt_usermessage;
    ImageButton btn_send;
    RecyclerView rv_chat;


    String userName, otherName, user_image_url;

    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<Messages> list_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_conversation);


        other_user_image = findViewById(R.id.circleImage_chat_picture);
        image_view_back = findViewById(R.id.btn_chat_message_back);
        txt_name = findViewById(R.id.txt_chat_name);
        btn_send = findViewById(R.id.btn_chat_message_send);
        edt_usermessage = findViewById(R.id.edt_chat_message_usermessage);
        rv_chat = findViewById(R.id.rv_chat_message_recycle_view);


        rv_chat.setLayoutManager(new LinearLayoutManager(this));
        list_message = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        userName = getIntent().getStringExtra("userName");
        user_image_url = getIntent().getStringExtra("imageUrl");
        otherName = getIntent().getStringExtra("otherName");


        txt_name.setText(otherName);
        // load the image to this circle view id     circleImage_chat_picture

        if (!user_image_url.equals("")){
            Picasso.get().load(user_image_url).into(other_user_image);
        } else {
            Toast.makeText(ChatConversationActivity.this, "NO IMAGE URL", Toast.LENGTH_LONG).show();
        }


        image_view_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // jump back to home
                Intent i = new Intent(ChatConversationActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edt_usermessage.getText().toString();

                if (!TextUtils.isEmpty(message)) {
                    send_message(message);
                    edt_usermessage.setText("");
                }
            }
        });

        getMessage();
    }

    // Sending the message to the other user by referencing it by key
    private void send_message(String message) {

        String key = reference.child("Messages").child(userName).child(otherName).push().getKey();

        System.out.println(key);


        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("message", message);
        messageMap.put("from", userName);
        reference.child("Messages").child(userName).child(otherName).child(key)
                .setValue(messageMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override


            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(userName).child(key).setValue(messageMap);
                }
            }
        });
    }




    public void getMessage() {
        reference.child("Messages").child(userName).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages messageClass = snapshot.getValue(Messages.class);
                list_message.add(messageClass);
                adapter.notifyDataSetChanged();
                rv_chat.scrollToPosition(list_message.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        adapter = new MessageAdapter(list_message,userName);
        rv_chat.setAdapter(adapter);
    }


}