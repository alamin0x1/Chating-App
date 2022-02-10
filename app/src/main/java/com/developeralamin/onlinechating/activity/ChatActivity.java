package com.developeralamin.onlinechating.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.developeralamin.onlinechating.R;
import com.developeralamin.onlinechating.adapter.MessageAdapter;
import com.developeralamin.onlinechating.model.MessageData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage, ReceiverUid, ReceiverName, SenderUID;
    CircleImageView profile_image;
    TextView reciverName;

    public static String sImage;
    public static String rImage;
    FirebaseDatabase database;
    FirebaseAuth auth;

    EditText editTextMessage;
    CardView sendBtn;

    String senderRoom, receiverRooom;
    RecyclerView recyclerView;

    ArrayList<MessageData> messageDataArrayList;

    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        messageDataArrayList = new ArrayList<>();

        ReceiverName = getIntent().getStringExtra("name");
        ReceiverImage = getIntent().getStringExtra("ReceiveImage");
        ReceiverUid = getIntent().getStringExtra("uid");

        profile_image = findViewById(R.id.profile_image);
        reciverName = findViewById(R.id.reciverName);
        editTextMessage = findViewById(R.id.editTextMessage);
        sendBtn = findViewById(R.id.sendBtn);

        recyclerView = findViewById(R.id.recyclerViewChat);
//        messageAdapter = new MessageAdapter(this, messageDataArrayList);
//        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdapter = new MessageAdapter(ChatActivity.this, messageDataArrayList);
        recyclerView.setAdapter(messageAdapter);


        Glide.with(this).load(ReceiverImage).into(profile_image);
        reciverName.setText("" + ReceiverName);

        SenderUID = auth.getUid();
        senderRoom = SenderUID + ReceiverUid;
        receiverRooom = ReceiverUid + SenderUID;


        DatabaseReference reference = database.getReference().child("FastChat_User").child(auth.getUid());
        DatabaseReference chatReference = database.getReference().child("chats").child(senderRoom).child("messages");


        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messageDataArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    MessageData messageData = dataSnapshot.getValue(MessageData.class);
                    messageDataArrayList.add(messageData);
                }

                messageAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sImage = snapshot.child("imageUri").getValue().toString();
                rImage = ReceiverImage;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString();

                if (message.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Please enter Valid Message", Toast.LENGTH_SHORT).show();
                    return;
                }
                editTextMessage.setText("");
                Date date = new Date();

                MessageData messageData = new MessageData(message, SenderUID, date.getTime());

                database = FirebaseDatabase.getInstance();
                database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .push()
                        .setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        database.getReference().child("chats")
                                .child(receiverRooom)
                                .child("messages")
                                .push()
                                .setValue(messageData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });

            }
        });

    }
}