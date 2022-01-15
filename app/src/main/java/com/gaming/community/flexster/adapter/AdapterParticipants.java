package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.chat.ChatActivity;
import com.gaming.community.flexster.profile.UserProfileActivity;
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
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class AdapterParticipants extends RecyclerView.Adapter<AdapterParticipants.HolderParticipantsAdd>{

    private final Context context;
    private final List<ModelUser> userList;
    private final String groupId;
    private final String myGroupRole;


    private RequestQueue requestQueue;
    private boolean notify = false;

    String privateClubs = "";

    public AdapterParticipants(Context context, List<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantsAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        return new HolderParticipantsAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantsAdd holder, int position) {

        requestQueue = Volley.newRequestQueue(context);

        ModelUser modelUser = userList.get(position);
        String mName = modelUser.getName();
        String mUsername = modelUser.getUsername();
        String dp = modelUser.getPhoto();
        String uid = modelUser.getId();

        holder.name.setText(mName);

        if (userList.get(position).getVerified().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);

        try {
            if (!dp.equals("")){
                Picasso.get().load(dp).placeholder(R.drawable.avatar).into(holder.circleImageView);
            }
            else {
                holder.circleImageView.setImageResource(R.drawable.avatar);
            }
        }catch (Exception e){
            //Picasso.get().load(R.drawable.avatar).into(holder.circleImageView);
            holder.circleImageView.setImageResource(R.drawable.avatar);
        }
        //holder.username.setText(mUsername);
        checkAlreadyExists(modelUser, holder,mUsername);
        holder.ll_g_engagement.setVisibility(View.GONE);
        holder.ll_g_skill.setVisibility(View.GONE);
        holder.ll_c_engagement.setVisibility(View.VISIBLE);

        //getGroupUserLevel
        PostCount.getgroupuserlevel(groupId,uid,holder.txt_clan_post_count);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
                ref.child(groupId).child("Participants").child(userList.get(position).getId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    Toast.makeText(context, "already added!!", Toast.LENGTH_SHORT).show();
                                }
                                else {

                                    FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position).getId())
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            privateClubs = Objects.requireNonNull(snapshot.child("privateClubs").getValue()).toString();

                                            if (privateClubs.equals("private")){
                                                FirebaseDatabase.getInstance().getReference("Follow")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child("Followers").child(userList.get(position).getId())
                                                        .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        if(snapshot.exists()){

                                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                                            ref.child(userList.get(position).getId())
                                                                    .child("BlockedUsers")
                                                                    .orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .addValueEventListener(new ValueEventListener() {
                                                                        @SuppressLint("SetTextI18n")
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                            if (snapshot.exists()){
                                                                                for (DataSnapshot ds: snapshot.getChildren()){
                                                                                    if (ds.exists()){
                                                                                        Toast.makeText(context, "You are not allowed!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                    else {

                                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                        builder.setTitle("Add Player")
                                                                                                .setMessage("Add this player in the club?")
                                                                                                .setPositiveButton("Add", (dialog, which) ->
                                                                                                        addParticipants(modelUser, holder)).setNegativeButton("Cancel", (dialog, which) ->
                                                                                                dialog.dismiss()).show();

                                                                                    }
                                                                                }
                                                                            }
                                                                            else {

                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                                builder.setTitle("Add Player")
                                                                                        .setMessage("Add this player in the club?")
                                                                                        .setPositiveButton("Add", (dialog, which) ->
                                                                                                addParticipants(modelUser, holder)).setNegativeButton("Cancel", (dialog, which) ->
                                                                                        dialog.dismiss()).show();

                                                                            }

                                                                        }

                                                                        @Override
                                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                                        }
                                                                    });

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
                                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                                ref.child(userList.get(position).getId())
                                                        .child("BlockedUsers")
                                                        .orderByChild("id").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @SuppressLint("SetTextI18n")
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                if (snapshot.exists()){
                                                                    for (DataSnapshot ds: snapshot.getChildren()){
                                                                        if (ds.exists()){
                                                                            Toast.makeText(context, "You are not allowed!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        else {

                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                            builder.setTitle("Add Player")
                                                                                    .setMessage("Add this player in the club?")
                                                                                    .setPositiveButton("Add", (dialog, which) ->
                                                                                            addParticipants(modelUser, holder)).setNegativeButton("Cancel", (dialog, which) ->
                                                                                    dialog.dismiss()).show();

                                                                        }
                                                                    }
                                                                }
                                                                else {

                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                                                    builder.setTitle("Add Player")
                                                                            .setMessage("Add this player in the club?")
                                                                            .setPositiveButton("Add", (dialog, which) ->
                                                                                    addParticipants(modelUser, holder)).setNegativeButton("Cancel", (dialog, which) ->
                                                                            dialog.dismiss()).show();

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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        });

    }

    /*private void makevip(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "vip");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Vip made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    /*private void makecaptain(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "captain");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "captain made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    /*private void maketester(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "tester");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "tester made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    /*private void makemember(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "member");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "member made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    /*private void makeguest(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "guest");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "guest made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

    private void addParticipants(ModelUser modelUser, HolderParticipantsAdd holder) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", modelUser.getId());
        hashMap.put("role", "guest");
        hashMap.put("scrimster", "no");
        hashMap.put("timestamp", ""+timestamp);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Player added", Toast.LENGTH_SHORT).show());

        String stamp = "" + System.currentTimeMillis();
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", modelUser.getId());
        hashMap1.put("msg", "");
        hashMap1.put("type", "text");
        hashMap1.put("timestamp", stamp);
        hashMap1.put("replayId", "");
        hashMap1.put("replayMsg", "");
        hashMap1.put("replayUserId", "");
        hashMap1.put("creater_win_id", modelUser.getId());
        hashMap1.put("win_log_msg", "Joined the club");
        hashMap1.put("win_post_id", "");
        hashMap1.put("win_type", "");

        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(stamp)
                .setValue(hashMap1);

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Let's go!",modelUser.getId(), Objects.requireNonNull(user).getName(), "added you to club");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /*private void makeco_leader(ModelUser modelUser, HolderParticipantsAdd holder) {
        Log.e("Makingadmin","co-leader");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "co-leader");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Admin made", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification(modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you admin");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }*/

    /*private void removeParticipants(ModelUser modelUser, HolderParticipantsAdd holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Plear removed from the club", Toast.LENGTH_SHORT).show());
    }*/

    /*private void removeAdmin(ModelUser modelUser, HolderParticipantsAdd holder) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "guest");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Admin removed", Toast.LENGTH_SHORT).show());
    }*/

    private void checkAlreadyExists(ModelUser modelUser, HolderParticipantsAdd holder, String mUsername) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            //holder.username.setText(mUsername + " - " +hisRole);

                            holder.username.setText(mUsername + " - In Club");

                            /*if (hisRole.equals("leader")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_leader);
                            }
                            else if (hisRole.equals("co-leader")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_co_leader);
                            }
                            else if (hisRole.equals("vip")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_vip);
                            }
                            else if (hisRole.equals("guest")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_guest);
                            }
                            else if (hisRole.equals("member")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_member);
                            }
                            else if (hisRole.equals("tester")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_tester);
                            }
                            else if (hisRole.equals("captain")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_captain);
                            }*/

                        }
                        else {
                            holder.username.setText(mUsername);
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

    static class HolderParticipantsAdd extends RecyclerView.ViewHolder{

        private final CircleImageView circleImageView;
        private final TextView name;
        private final TextView username;
        private final ImageView verified;
        private final ImageView img_role;
        LinearLayout ll_g_engagement,ll_g_skill,ll_c_engagement;
        TextView txt_clan_post_count;

        public HolderParticipantsAdd(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.dp);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            verified  = itemView.findViewById(R.id.verified);
            img_role = itemView.findViewById(R.id.img_role);
            ll_g_engagement = itemView.findViewById(R.id.ll_g_engagement);
            ll_g_skill = itemView.findViewById(R.id.ll_g_skill);
            ll_c_engagement = itemView.findViewById(R.id.ll_c_engagement);
            txt_clan_post_count = itemView.findViewById(R.id.txt_clan_post_count);

        }
    }


    private void sendNotification(final String title,final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, title, hisId, "profile", R.drawable.ic_push_notification);
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
