package com.example.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.ChatConversationActivity;
import com.example.chatapplication.R;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<String> userList;
    String username;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference reference;

    public UserAdapter(List<String> userList, String username, Context mContext) {
        this.userList = userList;
        this.username = username;
        this.mContext = mContext;

        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(com.example.chatapplication.R.layout.user_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String otherName = snapshot.child("userName").getValue().toString();
                String imageUrl = snapshot.child("image").getValue().toString();


                holder.txt_view_users.setText(otherName);

                if (imageUrl.equals("")){
                    holder.imageViewUsers.setImageResource(com.example.chatapplication.R.drawable.user);
                } else {
                    Picasso.get().load(imageUrl).into(holder.imageViewUsers);
                }

                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ChatConversationActivity.class);
                        intent.putExtra("userName", username);
                        intent.putExtra("imageUrl", imageUrl);

                        intent.putExtra("otherName", otherName);
                        mContext.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txt_view_users;
        CircleImageView imageViewUsers;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_view_users = itemView.findViewById(com.example.chatapplication.R.id.card_username);
            imageViewUsers = itemView.findViewById(com.example.chatapplication.R.id.circleImage_adapter_imageView);
            cardView = itemView.findViewById(R.id.user_cardview);
        }


    }
}