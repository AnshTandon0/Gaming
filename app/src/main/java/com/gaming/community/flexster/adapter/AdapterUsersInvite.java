package com.gaming.community.flexster.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.gaming.community.flexster.watchParty.InviteActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class AdapterUsersInvite extends RecyclerView.Adapter<AdapterUsersInvite.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    private RequestQueue requestQueue;
    private boolean notify = false;

    public AdapterUsersInvite(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());

        holder.name.setText(userList.get(position).getName());

        holder.username.setText(userList.get(position).getUsername());

        if (userList.get(position).getPhoto().isEmpty()){
            Picasso.get().load(R.drawable.avatar).into(holder.dp);
        }else {
            Picasso.get().load(userList.get(position).getPhoto()).into(holder.dp);
        }

        if (userList.get(position).getVerified().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("receiver", userList.get(position).getId());
            hashMap.put("msg", "Watch party invitation " + InviteActivity.getId());
            hashMap.put("isSeen", false);
            hashMap.put("timestamp", InviteActivity.getId());
            hashMap.put("type", "party");
            FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
            notify = true;
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify){
                        sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent meeting id");
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            Snackbar.make(v, "Invitation sent", Snackbar.LENGTH_LONG).show();
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView dp;
        final ImageView verified;
        final TextView name;
        final TextView username;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
        }

    }

    private void sendNotification(final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, "New Message", hisId, "profile", R.drawable.logo);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse%s", response.toString()), error -> Timber.d("onResponse%s", error.toString())){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAALM19fr0:APA91bFu8hZyEWAUhRieVN8SHIkt5wV_Kb5f4aar4-Zang3fmD3BbdbqzxP4wNFOGx_C2Mc0fxNtYg4JCvVBDQUU3C7b-pkX4DGfMA5v93FIYtFKQ96Opb0ATQHR5lKoNVitdV9L8oNo");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
