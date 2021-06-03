package com.example.chatapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.R;
import com.example.chatapplication.model.Messages;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    List<Messages> list_messages;
    String userName;
    boolean status;
    int send;
    int receive;

    public MessageAdapter(List<Messages> list_messages, String userName) {
        this.list_messages = list_messages;
        this.userName = userName;

        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_received,parent,false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.txtView.setText(list_messages.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list_messages.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            if (status){
                txtView = itemView.findViewById(R.id.textViewSend);

            } else {
                txtView = itemView.findViewById(R.id.textViewReceive);
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (list_messages.get(position).getFrom().equals(userName)){
            status = true;
            return send;
        }

        else {
            status = false;
            return receive;
        }
    }
}