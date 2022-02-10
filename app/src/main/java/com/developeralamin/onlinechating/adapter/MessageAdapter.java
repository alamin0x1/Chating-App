package com.developeralamin.onlinechating.adapter;

import static com.developeralamin.onlinechating.activity.ChatActivity.rImage;
import static com.developeralamin.onlinechating.activity.ChatActivity.sImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developeralamin.onlinechating.R;
import com.developeralamin.onlinechating.model.MessageData;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<MessageData> messageDataArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECIVE = 2;

    public MessageAdapter(Context context, ArrayList<MessageData> messageDataArrayList) {
        this.context = context;
        this.messageDataArrayList = messageDataArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout_item, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout_item, parent, false);
            return new ReciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MessageData messageData = messageDataArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;

            viewHolder.textMessage.setText(messageData.getMessage());
            Glide.with(context).load(sImage).into(((SenderViewHolder) holder).circleImageView);

        } else {
            ReciverViewHolder viewHolder = (ReciverViewHolder) holder;
            viewHolder.textMessage.setText(messageData.getMessage());

            Glide.with(context).load(rImage).into(((ReciverViewHolder) holder).circleImageView);

        }
    }

    @Override
    public int getItemCount() {
        return messageDataArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        MessageData messageData = messageDataArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messageData.getSenderId())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECIVE;
        }
    }

 class SenderViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        TextView textMessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.circleImageView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    class ReciverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView textMessage;

        public ReciverViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.circleImageView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
