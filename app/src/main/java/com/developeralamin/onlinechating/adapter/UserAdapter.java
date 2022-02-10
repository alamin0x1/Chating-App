package com.developeralamin.onlinechating.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.developeralamin.onlinechating.R;
import com.developeralamin.onlinechating.activity.ChatActivity;
import com.developeralamin.onlinechating.activity.HomeActivity;
import com.developeralamin.onlinechating.model.UserData;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private HomeActivity context;
    private ArrayList<UserData> userDataArrayList;

    public UserAdapter(HomeActivity context, ArrayList<UserData> userDataArrayList) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        UserData userData = userDataArrayList.get(position);

        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userData.getUid())) {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.user_name.setText(userData.getName());
        holder.user_status.setText(userData.getStatus());

        Glide.with(context).load(userData.getImageUri()).into(holder.profile_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", userData.getName());
                intent.putExtra("ReceiveImage", userData.getImageUri());
                intent.putExtra("uid", userData.getUid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView user_name, user_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image);
            user_name = itemView.findViewById(R.id.user_name);
            user_status = itemView.findViewById(R.id.user_status);


        }
    }
}
