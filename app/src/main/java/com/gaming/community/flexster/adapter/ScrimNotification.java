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

import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelNotification;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ScrimNotification extends RecyclerView.Adapter<ScrimNotification.Holder>  {

    private final Context context;
    private final ArrayList<ModelNotification> notifications;

    public ScrimNotification(Context context, ArrayList<ModelNotification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ScrimNotification.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_list, parent, false);
        return new ScrimNotification.Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ScrimNotification.Holder holder, int position) {

        ModelNotification modelNotification = notifications.get(position);
        String notification = modelNotification.getNotification();
        String timestamp = modelNotification.getTimestamp();
        String senderUid = modelNotification.getsUid();
        String postId = modelNotification.getpId();

        String lastSeenTime = GetTimeAgo.getTimeAgo(Long.parseLong(timestamp));
        holder.username.setText(notification+ " - "+ lastSeenTime);

        FirebaseDatabase.getInstance().getReference().child("Users").child(senderUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (Objects.requireNonNull(snapshot.child("verified").getValue()).toString().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);
                holder.name.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                if (!Objects.requireNonNull(snapshot.child("photo").getValue()).toString().isEmpty())  Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(holder.circleImageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(v -> {

            if (senderUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                Toast.makeText(context, "It's You", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("hisUID", senderUid);
                context.startActivity(intent);
            }

        });

    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class Holder extends RecyclerView.ViewHolder{

        final CircleImageView circleImageView;
        final TextView username;
        final TextView name;
        final ImageView verified;

        public Holder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.dp);
            username = itemView.findViewById(R.id.username);
            name = itemView.findViewById(R.id.name);
            verified = itemView.findViewById(R.id.verified);
        }
    }

}