package com.gaming.community.flexster.adapter;

import android.content.Context;
import android.net.Uri;
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
import com.gaming.community.flexster.PostCount;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.gaming.community.flexster.send.SendToUserActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class AdapterSendUsers extends RecyclerView.Adapter<AdapterSendUsers.MyHolder>{

    final Context context;
    final List<ModelUser> userList;

    public AdapterSendUsers(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    private RequestQueue requestQueue;
    private boolean notify = false;

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
            //Picasso.get().load(R.drawable.avatar).into(holder.dp);
            holder.dp.setImageResource(R.drawable.avatar);
        }else {
            Picasso.get().load(userList.get(position).getPhoto()).into(holder.dp);
        }

        if (userList.get(position).getVerified().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);

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

        holder.itemView.setOnClickListener(v -> {
            Snackbar.make(v, "Please wait sending...", Snackbar.LENGTH_LONG).show();
            switch (SendToUserActivity.getType()) {
                case "image": {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_photo/" + "" + System.currentTimeMillis());
                    storageReference.putFile(Uri.parse(SendToUserActivity.getUri())).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("receiver", userList.get(position).getId());
                            hashMap.put("msg", downloadUri.toString());
                            hashMap.put("isSeen", false);
                            hashMap.put("timestamp", "" + System.currentTimeMillis());
                            hashMap.put("type", "image");
                            hashMap.put("post_id","");
                            hashMap.put("win_post_id","");
                            hashMap.put("win_type","");
                            FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                            Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent a image");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                    break;
                }
                case "video": {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_video/" + "" + System.currentTimeMillis());
                    storageReference.putFile(Uri.parse(SendToUserActivity.getUri())).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("receiver", userList.get(position).getId());
                            hashMap.put("msg", downloadUri.toString());
                            hashMap.put("isSeen", false);
                            hashMap.put("timestamp", "" + System.currentTimeMillis());
                            hashMap.put("type", "video");
                            hashMap.put("post_id","");
                            hashMap.put("win_post_id","");
                            hashMap.put("win_type","");
                            FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                            Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent a video");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                    break;
                }
                case "reel": {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", SendToUserActivity.getUri());
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", "" + System.currentTimeMillis());
                    hashMap.put("type", "reel");
                    hashMap.put("post_id","");
                    hashMap.put("win_post_id","");
                    hashMap.put("win_type","");
                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();
                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                //sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent a reel");
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    break;
                }
                case "post": {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", "Shared a post");
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", "" + System.currentTimeMillis());
                    hashMap.put("type", "text");
                    hashMap.put("post_id",SendToUserActivity.getPost_id());
                    hashMap.put("win_post_id",SendToUserActivity.getWin_post_id());
                    hashMap.put("win_type",SendToUserActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();
                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                //sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent a post");
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    break;
                }
                case "meet": {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", SendToUserActivity.getUri());
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", SendToUserActivity.getUri());
                    hashMap.put("type", "meet");
                    hashMap.put("post_id","");
                    hashMap.put("win_post_id","");
                    hashMap.put("win_type","");
                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);
                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();
                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                //sendNotification(userList.get(position).getId(), Objects.requireNonNull(user).getName(), "sent meeting id");
                            }
                            notify = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    break;
                }
                case "group_fight_log":{

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", "Shared a fight result");
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", "" + System.currentTimeMillis());
                    hashMap.put("type", "text");
                    hashMap.put("post_id",SendToUserActivity.getPost_id());
                    hashMap.put("win_post_id",SendToUserActivity.getWin_post_id());
                    hashMap.put("win_type",SendToUserActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }
                case "user_fight_log":{

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", "Shared a fight result");
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", "" + System.currentTimeMillis());
                    hashMap.put("type", "text");
                    hashMap.put("post_id",SendToUserActivity.getPost_id());
                    hashMap.put("win_post_id",SendToUserActivity.getWin_post_id());
                    hashMap.put("win_type",SendToUserActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }
                case "club_fight_log":{

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("receiver", userList.get(position).getId());
                    hashMap.put("msg", "Shared a fight result");
                    hashMap.put("isSeen", false);
                    hashMap.put("timestamp", "" + System.currentTimeMillis());
                    hashMap.put("type", "text");
                    hashMap.put("post_id",SendToUserActivity.getPost_id());
                    hashMap.put("win_post_id",SendToUserActivity.getWin_post_id());
                    hashMap.put("win_type",SendToUserActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }
                //**************
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
        final TextView name;
        final TextView username;
        ImageView admin;
        TextView txt_post_count,txt_fight_count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            admin = itemView.findViewById(R.id.admin);
            txt_post_count = itemView.findViewById(R.id.txt_post_count);
            txt_fight_count = itemView.findViewById(R.id.txt_fight_count);
        }

    }

    /*private void sendNotification(final String hisId, final String name,final String message){
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
    }*/

}
