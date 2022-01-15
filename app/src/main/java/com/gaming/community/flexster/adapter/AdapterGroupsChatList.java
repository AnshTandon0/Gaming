package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.group.GroupChatActivity;
import com.gaming.community.flexster.model.ModelGroups;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGroupsChatList extends RecyclerView.Adapter<AdapterGroupsChatList.MyHolder>{

    final Context context;
    final List<ModelGroups> userList;

    public AdapterGroupsChatList(Context context, List<ModelGroups> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.group_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        ModelGroups modelChatListGroups = userList.get(position);

        loadLastMsg(holder, modelChatListGroups);

        holder.name.setText(userList.get(position).getgName());

        if (userList.get(position).getgIcon().isEmpty()){
            //Picasso.get().load(R.drawable.group).into(holder.dp);
            holder.dp.setImageResource(R.drawable.group);
        }else {
            Picasso.get().load(userList.get(position).getgIcon()).into(holder.dp);
        }

        holder.img_notification_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notification", "off");

                FirebaseDatabase.getInstance().getReference("Groups")
                        .child(userList.get(position).getGroupId())
                        .child("Participants")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("GroupNotification")
                        .setValue(hashMap);

                Toast.makeText(context, "Notification Off", Toast.LENGTH_SHORT).show();
                holder.img_notification_on.setVisibility(View.GONE);
                holder.img_notification_off.setVisibility(View.VISIBLE);

            }
        });

        holder.img_notification_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("notification", "on");

                FirebaseDatabase.getInstance().getReference("Groups")
                        .child(userList.get(position).getGroupId())
                        .child("Participants")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("GroupNotification")
                        .setValue(hashMap);

                Toast.makeText(context, "Notification On", Toast.LENGTH_SHORT).show();
                holder.img_notification_off.setVisibility(View.GONE);
                holder.img_notification_on.setVisibility(View.VISIBLE);

            }
        });

        //check notification
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(userList.get(position).getGroupId())
                .child("Participants")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("GroupNotification")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    if (snapshot.child("notification").getValue().toString().equals("on")){
                        holder.img_notification_off.setVisibility(View.GONE);
                        holder.img_notification_on.setVisibility(View.VISIBLE);
                    }
                    else {
                        holder.img_notification_off.setVisibility(View.VISIBLE);
                        holder.img_notification_on.setVisibility(View.GONE);
                    }

                }
                else {
                    holder.img_notification_off.setVisibility(View.GONE);
                    holder.img_notification_on.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //setGroupLevel
        PostCount.getgrouplevel(userList.get(position).getGroupId(),holder.txt_club_count);

        //getClubVerification
        FirebaseDatabase.getInstance().getReference().child("Groups").child(userList.get(position).getGroupId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (snapshot.child("clubVerified").exists()){

                                if (snapshot.child("clubVerified").getValue().toString().equals("true")){
                                    holder.verified.setVisibility(View.VISIBLE);
                                }
                                else {
                                    holder.verified.setVisibility(View.GONE);
                                }

                            }
                            else {
                                holder.verified.setVisibility(View.GONE);
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //***************

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GroupChatActivity.class);
            intent.putExtra("group", userList.get(position).getGroupId());
            intent.putExtra("type", "create");
            context.startActivity(intent);
        });

        //Private
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(userList.get(position).getGroupId())
                .child("Privacy")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String privacy = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                    if (privacy.equals("private")){
                        FirebaseDatabase.getInstance().getReference().child("Groups")
                                .child(userList.get(position).getGroupId())
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){
                                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                    params.height = 0;
                                    holder.itemView.setLayoutParams(params);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadLastMsg(MyHolder holder, ModelGroups modelChatListGroups) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(modelChatListGroups.getGroupId()).child("Message").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String winMessage = ""+ds.child("win_log_msg").getValue();
                            String message = ""+ds.child("msg").getValue();
                            String sender = ""+ds.child("sender").getValue();
                            String type = ""+ds.child("type").getValue();

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.orderByChild("id").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ds: snapshot.getChildren()){
                                                String name = ""+ds.child("name").getValue();
                                                switch (type) {
                                                    case "image":
                                                        holder.username.setText(name + ": " + "Sent a photo");
                                                        break;
                                                    case "video":
                                                        holder.username.setText(name + ": " + "Sent a video");
                                                        break;
                                                    case "post":
                                                        holder.username.setText(name + ": " + "Sent a post");
                                                        break;
                                                    case "gif":
                                                        holder.username.setText(name + ": " + "Sent a sticker");
                                                        break;
                                                    case "cust_gif":
                                                        holder.username.setText(name + ": " + "Sent a custom emoji");
                                                        break;
                                                    case "audio":
                                                        holder.username.setText(name + ": " + "Sent a audio");
                                                    case "doc":
                                                        holder.username.setText(name + ": " + "Sent a document");
                                                        break;
                                                    case "location":
                                                        holder.username.setText("Sent a location");
                                                        break;
                                                    case "party":
                                                        holder.username.setText("Sent a party invitation");
                                                        break;
                                                    case "reel":
                                                        holder.username.setText("Sent a reel");
                                                        break;
                                                    case "story":
                                                    case "high":
                                                        holder.username.setText("Sent a story");
                                                        break;
                                                    default:
                                                        if (message.equals("")){
                                                            holder.username.setText(name + ": " + winMessage);
                                                        }
                                                        else {
                                                            holder.username.setText(name + ": " + message);
                                                        }
                                                        break;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
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
        final TextView name;
        final TextView username;
        TextView txt_club_count;
        ImageView  verified,img_notification_on,img_notification_off;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            txt_club_count = itemView.findViewById(R.id.txt_club_count);
            verified = itemView.findViewById(R.id.verified);
            img_notification_on = itemView.findViewById(R.id.img_notification_on);
            img_notification_off = itemView.findViewById(R.id.img_notification_off);
        }

    }
}
