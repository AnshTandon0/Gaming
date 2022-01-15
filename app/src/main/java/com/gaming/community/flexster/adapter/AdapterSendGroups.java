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
import com.gaming.community.flexster.model.ModelGroups;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.gaming.community.flexster.send.SendToGroupActivity;
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
public class AdapterSendGroups extends RecyclerView.Adapter<AdapterSendGroups.MyHolder>{

    final Context context;
    final List<ModelGroups> userList;

    public AdapterSendGroups(Context context, List<ModelGroups> userList) {
        this.context = context;
        this.userList = userList;
    }

    private RequestQueue requestQueue;
    private boolean notify = false;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.group_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());

        holder.name.setText(userList.get(position).getgName());

        holder.username.setText(userList.get(position).getgUsername());

        if (userList.get(position).getgIcon().isEmpty()){
            //Picasso.get().load(R.drawable.group).into(holder.dp);
            holder.dp.setImageResource(R.drawable.group);
        }else {
            Picasso.get().load(userList.get(position).getgIcon()).into(holder.dp);
        }

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

        //setGroupLevel
        PostCount.getgrouplevel(userList.get(position).getGroupId(),holder.txt_club_count);

        holder.itemView.setOnClickListener(v -> {
            Snackbar.make(v, "Please wait sending...", Snackbar.LENGTH_LONG).show();
            switch (SendToGroupActivity.getType()) {
                case "image": {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("chat_photo/" + "" + System.currentTimeMillis());
                    storageReference.putFile(Uri.parse(SendToGroupActivity.getUri())).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {

                            String stamp = "" + System.currentTimeMillis();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("msg", downloadUri.toString());
                            hashMap.put("type", "image");
                            hashMap.put("timestamp", stamp);
                            hashMap.put("replayId","");
                            hashMap.put("replayMsg","");
                            hashMap.put("replayUserId","");
                            hashMap.put("creater_win_id","");
                            hashMap.put("win_log_msg","");
                            hashMap.put("win_post_id","");
                            hashMap.put("win_type","");

                            FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                                    .setValue(hashMap);

                            Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Participants").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    //sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "sent a image");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
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
                    storageReference.putFile(Uri.parse(SendToGroupActivity.getUri())).addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        Uri downloadUri = uriTask.getResult();
                        if (uriTask.isSuccessful()) {

                            String stamp = "" + System.currentTimeMillis();
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                            hashMap.put("msg", downloadUri.toString());
                            hashMap.put("type", "video");
                            hashMap.put("timestamp", stamp);
                            hashMap.put("replayId","");
                            hashMap.put("replayMsg","");
                            hashMap.put("replayUserId","");
                            hashMap.put("creater_win_id","");
                            hashMap.put("win_log_msg","");
                            hashMap.put("win_post_id","");
                            hashMap.put("win_type","");

                            FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                                    .setValue(hashMap);


                            Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Participants").addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    //sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "sent a video");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
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

                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", SendToGroupActivity.getUri());
                    hashMap.put("type", "reel");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id","");
                    hashMap.put("win_log_msg","");
                    hashMap.put("win_post_id","");
                    hashMap.put("win_type","");

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Participants").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            //sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "sent a reel");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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

                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", "");
                    hashMap.put("type", "text");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",SendToGroupActivity.getCreater_win_id());
                    hashMap.put("win_log_msg","Shared a post");
                    hashMap.put("win_post_id",SendToGroupActivity.getWin_post_id());
                    hashMap.put("win_type",SendToGroupActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Participants").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            //sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "sent a post");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", SendToGroupActivity.getUri());
                    hashMap.put("type", "meet");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("win_log_msg",SendToGroupActivity.getUri());
                    hashMap.put("win_post_id","");
                    hashMap.put("win_type","meet");

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify) {
                                FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Participants").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            //sendNotification(ds.getKey(), Objects.requireNonNull(user).getName(), "sent meeting id");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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

                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", "");
                    hashMap.put("type", "text");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",SendToGroupActivity.getCreater_win_id());
                    hashMap.put("win_log_msg","Shared a fight result");
                    hashMap.put("win_post_id",SendToGroupActivity.getWin_post_id());
                    hashMap.put("win_type",SendToGroupActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }
                case "user_fight_log":{

                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", "");
                    hashMap.put("type", "text");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",SendToGroupActivity.getCreater_win_id());
                    hashMap.put("win_log_msg","Shared a fight result");
                    hashMap.put("win_post_id",SendToGroupActivity.getWin_post_id());
                    hashMap.put("win_type",SendToGroupActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }
                case "club_fight_log":{

                    String stamp = "" + System.currentTimeMillis();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", "");
                    hashMap.put("type", "text");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",SendToGroupActivity.getCreater_win_id());
                    hashMap.put("win_log_msg","Shared a fight result");
                    hashMap.put("win_post_id",SendToGroupActivity.getWin_post_id());
                    hashMap.put("win_type",SendToGroupActivity.getWin_type());

                    FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Message").child(stamp)
                            .setValue(hashMap);

                    Snackbar.make(v, "Sent", Snackbar.LENGTH_LONG).show();

                    break;
                }

                //***************
            }

        });

        //Private
        FirebaseDatabase.getInstance().getReference("Groups").child(userList.get(position).getGroupId()).child("Privacy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String privacy = snapshot.child("type").getValue().toString();
                    if (privacy.equals("private")){
                        FirebaseDatabase.getInstance().getReference().child("Groups").child(userList.get(position).getGroupId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
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


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView dp;
        final TextView name;
        final TextView username;
        ImageView verified;
        TextView txt_club_count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            verified = itemView.findViewById(R.id.verified);
            txt_club_count = itemView.findViewById(R.id.txt_club_count);
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
