package com.example.merobook.adpters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.merobook.R;
import com.example.merobook.activities.ChatActivity;
import com.example.merobook.databinding.RowConversationBinding;
import com.example.merobook.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

        Context context;
        ArrayList<User> users;

public UsersAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        }

@NonNull
@Override
public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation, parent, false);

        return new UsersViewHolder(view);
        }

@Override
public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = users.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getUid();

        FirebaseDatabase.getInstance().getReference()
        .child("chats")
        .child(senderRoom)
        .addValueEventListener(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
        if(snapshot.exists()) {
        String lastMsg = snapshot.child("lastMsg").getValue(String.class);
        long time = snapshot.child("lastMsgTime").getValue(Long.class);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        holder.binding.msgTime.setText(dateFormat.format(new Date(time)));
        holder.binding.lastMsg.setText(lastMsg);
        }
        else {
        holder.binding.lastMsg.setText("Tap to chat");
        }
        }

@Override
public void onCancelled(@NonNull DatabaseError error) {

        }
        });


        holder.binding.username.setText(user.getName());
        holder.binding.usertype.setText(user.getUserType());


/*
        holder.binding.addfirend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        User user = users.get(position);
                        String senderId = getCurrentUserId();
                        String receiverId = user.getUid();
                        sendFriendRequest(senderId, receiverId);
                        if ( receiverId.equals(true)){
                                holder.binding.addfirend.setVisibility(View.GONE);
                        }else {
                                holder.binding.addfirend.setVisibility(View.VISIBLE);
                        }
                }
        });

 */



        Glide.with(context).load(user.getProfileImage())
        .placeholder(R.drawable.profile)
        .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("name", user.getName());
        intent.putExtra("image", user.getProfileImage());
        intent.putExtra("uid", user.getUid());
        intent.putExtra("token", user.getToken());
        intent.putExtra("email",user.getEmail());
        intent.putExtra("userType",user.getUserType());
        context.startActivity(intent);
        }
        });
        }

        private String getCurrentUserId() {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                                return user.getUid();
                        } else {
                                return null;
                        }
                }

        private void sendFriendRequest(String senderId, String receiverId) {

                DatabaseReference senderRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(senderId);
                senderRef.child(receiverId).setValue(true);

                Toast.makeText(context, "Request sent!!", Toast.LENGTH_SHORT).show();


                DatabaseReference receiverRef = FirebaseDatabase.getInstance().getReference("FriendRequests").child(receiverId);
                receiverRef.child(senderId).setValue(false);
        }

        private void moveItemToTop(int fromPostion) {
                if (fromPostion > 0) {
                        // Move item to top of list
                        notifyItemMoved(fromPostion, 0);
                }
        }

        @Override
public int getItemCount() {
        return users.size();
        }

public class UsersViewHolder extends RecyclerView.ViewHolder {

    RowConversationBinding binding;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        binding = RowConversationBinding.bind(itemView);
    }
}

}
