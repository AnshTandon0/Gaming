package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.PostCount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.chat.ChatActivity;
import com.gaming.community.flexster.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCreateChat extends RecyclerView.Adapter<AdapterCreateChat.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    String privateMessage = "";

    public AdapterCreateChat(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.chat_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(userList.get(position).getName());

        holder.username.setText(userList.get(position).getUsername());

        if (userList.get(position).getPhoto().isEmpty()){
            //Picasso.get().load(R.drawable.avatar).into(holder.dp);
            holder.dp.setImageResource(R.drawable.avatar);
        }else {
            Picasso.get().load(userList.get(position).getPhoto()).into(holder.dp);
        }

        if (userList.get(position).getVerified().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {

            FirebaseDatabase.getInstance().getReference()
                    .child("Users")
                    .child(userList.get(position).getId())
                    .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    privateMessage = Objects.requireNonNull(snapshot.child("privateMessage").getValue()).toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            FirebaseDatabase.getInstance().getReference("Users").child(userList.get(position).getId())
                    .child("BlockedUsers")
                    .orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists())
                            {
                                Toast.makeText(context, "You are not allowed!", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                if (privateMessage.equals("private")){
                                    FirebaseDatabase.getInstance().getReference("Follow")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("Followers").child(userList.get(position).getId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists()){
                                                        Intent intent = new Intent(context, ChatActivity.class);
                                                        intent.putExtra("hisUID", userList.get(position).getId());
                                                        context.startActivity(intent);
                                                    }
                                                    else {
                                                        Toast.makeText(context, "You are not allowed!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                }
                                else {
                                    Intent intent = new Intent(context, ChatActivity.class);
                                    intent.putExtra("hisUID", userList.get(position).getId());
                                    context.startActivity(intent);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });

        //AdminInfo
        FirebaseDatabase.getInstance().getReference("Admin").child(userList.get(position).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.admin.setVisibility(View.VISIBLE);
                        } else {
                            holder.admin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //PostCount
        PostCount.getfightlevel(userList.get(position).getId(),holder.txt_fight_count);
        PostCount.getengagelevel(userList.get(position).getId(),holder.txt_post_count);

        //UserInfo
        FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Time
                if (Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("online")) holder.online.setVisibility(View.VISIBLE);

                //Typing
                if (Objects.requireNonNull(snapshot.child("typingTo").getValue()).toString().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                    holder.username.setText("Typing...");
                }else {
                    holder.username.setText(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                }

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

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView dp;
        final ImageView verified;
        final ImageView online;
        final TextView name;
        final TextView username;
        ImageView admin;
        TextView txt_post_count,txt_fight_count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.message);
            online = itemView.findViewById(R.id.imageView2);
            admin = itemView.findViewById(R.id.admin);
            txt_post_count = itemView.findViewById(R.id.txt_post_count);
            txt_fight_count = itemView.findViewById(R.id.txt_fight_count);
        }

    }
}
