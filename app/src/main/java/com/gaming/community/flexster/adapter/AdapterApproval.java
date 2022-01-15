package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dropbox.core.v2.teamlog.SfInviteGroupDetails;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.group.GroupChatActivity;
import com.gaming.community.flexster.group.GroupProfileActivity;
import com.gaming.community.flexster.model.ModelPostClubFight;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterApproval extends RecyclerView.Adapter<AdapterApproval.ApprovalViewHolder> {

    Context context;
    ArrayList<ModelPostClubFight> modelPostClubFights = new ArrayList<>();

    public AdapterApproval(Context context, ArrayList<ModelPostClubFight> modelPostClubFights){
        this.context=context;
        this.modelPostClubFights=modelPostClubFights;
    }

    @NonNull
    @Override
    public AdapterApproval.ApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_approval,parent,false);
        return new AdapterApproval.ApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApproval.ApprovalViewHolder holder, @SuppressLint("RecyclerView") int position) {

        ModelPostClubFight model = modelPostClubFights.get(position);

        int total= Integer.parseInt((model.getTotal_rounds()));
        int won= Integer.parseInt(model.getUser_won());

        int dra=total-won;

        if(dra==won)
        {
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_club_scrim_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#1476CF"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //draw
        }
        else if(dra<won)
        {
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_club_scrim_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#1476CF"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //won
        }
        else{
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_club_scrim_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#1476CF"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //lost
        }

        holder.txt_total_round.setText(model.getTotal_rounds());
        holder.txt_won.setText(model.getUser_won());

        //getScrimType
        if (model.getScrim_type().equals("Mixed mode")){
            holder.txt_mode.setText("Mixed");
        }
        else if (model.getScrim_type().equals("Solo mode")){
            holder.txt_mode.setText("Solo");
        }
        else if (model.getScrim_type().equals("Duo mode"))
        {
            holder.txt_mode.setText("Duo");
        }
        else if (model.getScrim_type().equals("Trio mode"))
        {
            holder.txt_mode.setText("Trio");
        }
        else if (model.getScrim_type().equals("Squad mode"))
        {
            holder.txt_mode.setText("Squad");
        }
        else if (model.getScrim_type().equals("Penta mode"))
        {
            holder.txt_mode.setText("Penta");
        }

        /*String category = model.getCategory().substring(0,4);
        String category1 = model.getCategory().substring(5,11);*/

        holder.txt_category.setText("Club"+"\n"+"Scrim");

        holder.txt_game_name.setText(model.getGame_name());

        //Time
        long lastTime = Long.parseLong(model.getpId());
        holder.txt_time.setText("Fought "+ GetTimeAgo.getTimeAgo(lastTime));

        if (model.getContent().equals("")){
            holder.text.setVisibility(View.GONE);
        }
        else {
            holder.text.setText(model.getContent());
        }

        //getapproval
        if (model.getStatus().equals("approved")){
            holder.ll_unofficial.setVisibility(View.GONE);
            holder.ll_official.setVisibility(View.VISIBLE);
        }
        else{
            holder.ll_official.setVisibility(View.GONE);
            holder.ll_unofficial.setVisibility(View.VISIBLE);
        }

        //*****AdminInfo1*****
        FirebaseDatabase.getInstance().getReference("Admin").child(model.getCreatore_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.admin1.setVisibility(View.VISIBLE);
                        } else {
                            holder.admin1.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //PostCount1
        PostCount.getfightlevel(model.getCreatore_id(), holder.txt_fight_count1);
        PostCount.getengagelevel(model.getCreatore_id(), holder.txt_post_count1);

        //userInfo
        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getCreatore_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.dpiv1);

                        holder.dpiv1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!model.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    Intent intent = new Intent(context, UserProfileActivity.class);
                                    intent.putExtra("hisUID", model.getCreatore_id());
                                    context.startActivity(intent);
                                }else {
                                    Snackbar.make(v,"It's you",Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });

                        holder.user1.setText(snapshot.child("name").getValue().toString());

                        holder.user1.setOnClickListener(v -> {
                            if (!model.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, UserProfileActivity.class);
                                intent.putExtra("hisUID", model.getCreatore_id());
                                context.startActivity(intent);
                            }else {
                                Snackbar.make(v,"It's you",Snackbar.LENGTH_LONG).show();
                            }
                        });

                        //Verify
                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                            holder.verified1.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.verified1.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //groupInfo
        FirebaseDatabase.getInstance().getReference().child("Groups").child(model.getGroup_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("gIcon").getValue().toString().isEmpty()){
                            Picasso.get().load(snapshot.child("gIcon").getValue().toString()).into(holder.dpiv2);
                        }
                        else {
                            holder.dpiv2.setImageResource(R.drawable.group);
                        }

                        holder.dpiv2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, GroupProfileActivity.class);
                                intent.putExtra("group", model.getGroup_id());
                                intent.putExtra("type", "");
                                context.startActivity(intent);
                            }
                        });

                        holder.user2.setText(snapshot.child("gName").getValue().toString());

                        holder.user2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, GroupProfileActivity.class);
                                intent.putExtra("group", model.getGroup_id());
                                intent.putExtra("type", "");
                                context.startActivity(intent);
                            }
                        });

                        //setGroupLevel
                        PostCount.getgrouplevel(snapshot.child("groupId").getValue().toString(),holder.txt_club_count2);

                        //getClubVerification
                        if (snapshot.child("clubVerified").exists()){

                            if (snapshot.child("clubVerified").getValue().toString().equals("true")){
                                holder.verified2.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.verified2.setVisibility(View.GONE);
                            }

                        }
                        else {
                            holder.verified2.setVisibility(View.GONE);
                        }
                        //*********

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //approvedButton
        holder.approval_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("Groups")
                .child(model.getGroup_id()).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            String scrimster = ""+snapshot.child("scrimster").getValue();
                            //holder.username.setText(mUsername + " - " +hisRole);
                            if (hisRole.equals("owner")){
                                setApproved(position);
                            }
                            else if (scrimster.equals("yes")){
                                setApproved(position);
                            }
                            else {
                                Toast.makeText(context, "You are not allowed.", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        //rejectButton
        holder.unapproval_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase.getInstance().getReference("Groups")
                .child(model.getGroup_id()).child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            if (hisRole.equals("owner")){
                                setReject(position);
                            }
                            else if (hisRole.equals("co-owner")){
                                setReject(position);
                            }
                            else if (hisRole.equals("mod")){
                                setReject(position);
                            }
                            else if (hisRole.equals("vip guest")){
                                Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                            }
                            else if (hisRole.equals("guest")){
                                Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                            }
                            else if (hisRole.equals("member")){
                                Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                            }
                            else if (hisRole.equals("senior")){
                                Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return modelPostClubFights.size();
    }

    public class ApprovalViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll_trophy1,ll_trophy2,ll_main_bg;
        ImageView dpiv1,dpiv2;
        TextView user1,user2;
        ImageView verified1,admin1,verified2;
        TextView txt_category,txt_total_round,txt_won,txt_mode,txt_post_count1,txt_fight_count1,txt_club_count2;
        View view_line;
        TextView txt_game_name,txt_time,text;
        LinearLayout approval_Button,unapproval_Button;
        LinearLayout ll_official,ll_unofficial;

        public ApprovalViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_main_bg = itemView.findViewById(R.id.ll_main_bg);
            ll_trophy1 = itemView.findViewById(R.id.ll_trophy1);
            ll_trophy2 = itemView.findViewById(R.id.ll_trophy2);
            dpiv1 = itemView.findViewById(R.id.dpiv1);
            dpiv2 = itemView.findViewById(R.id.dpiv2);
            user1 = itemView.findViewById(R.id.user1);
            user2 = itemView.findViewById(R.id.user2);
            verified1 = itemView.findViewById(R.id.verified1);
            verified2 = itemView.findViewById(R.id.verified2);
            admin1 = itemView.findViewById(R.id.admin1);
            txt_post_count1 = itemView.findViewById(R.id.txt_post_count1);
            txt_fight_count1 = itemView.findViewById(R.id.txt_fight_count1);
            txt_club_count2 = itemView.findViewById(R.id.txt_club_count2);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_total_round = itemView.findViewById(R.id.txt_total_round);
            txt_won =  itemView.findViewById(R.id.txt_won);
            txt_mode = itemView.findViewById(R.id.txt_mode);
            view_line = itemView.findViewById(R.id.view_line);
            txt_game_name = itemView.findViewById(R.id.txt_game_name);
            txt_time = itemView.findViewById(R.id.txt_time);
            text = itemView.findViewById(R.id.text);
            approval_Button = itemView.findViewById(R.id.approval_Button);
            unapproval_Button = itemView.findViewById(R.id.unapproval_Button);
            ll_official = itemView.findViewById(R.id.ll_official);
            ll_unofficial = itemView.findViewById(R.id.ll_unofficial);

        }
    }


    private void setApproved(int position){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pId", modelPostClubFights.get(position).getpId());
        hashMap.put("game_name",modelPostClubFights.get(position).getGame_name());
        hashMap.put("category","Club Screen");
        hashMap.put("scrim_type",modelPostClubFights.get(position).getScrim_type());
        hashMap.put("total_rounds",modelPostClubFights.get(position).getTotal_rounds());
        hashMap.put("user_won",modelPostClubFights.get(position).getUser_won());
        hashMap.put("group_id",modelPostClubFights.get(position).getGroup_id());
        hashMap.put("group_won", modelPostClubFights.get(position).getGroup_won());
        hashMap.put("creatore_id", modelPostClubFights.get(position).getCreatore_id());
        hashMap.put("content",modelPostClubFights.get(position).getContent());
        hashMap.put("status", "approved");

        FirebaseDatabase.getInstance().getReference("PostClubFight")
                .child(modelPostClubFights.get(position).getGroup_id())
                .child(modelPostClubFights.get(position).getpId())
                .setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("PostUserClubFight")
                .child(modelPostClubFights.get(position).getCreatore_id())
                .child(modelPostClubFights.get(position).getpId())
                .setValue(hashMap);


        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("value", "1");

        FirebaseDatabase.getInstance().getReference("WinCount")
                .child(modelPostClubFights.get(position).getCreatore_id())
                .child(modelPostClubFights.get(position).getpId())
                .setValue(hashMap2);

        Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();

        //sendClubMassage
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("msg", "");
        hashMap1.put("type", "text");
        hashMap1.put("timestamp", timeStamp);
        hashMap1.put("replayId","");
        hashMap1.put("replayMsg","");
        hashMap1.put("replayUserId","");
        hashMap1.put("creater_win_id",modelPostClubFights.get(position).getGroup_id());
        hashMap1.put("win_log_msg","Approved a fight result");
        hashMap1.put("win_post_id", modelPostClubFights.get(position).getpId());
        hashMap1.put("win_type","club_fight_log");

        FirebaseDatabase.getInstance().getReference("Groups")
                .child(modelPostClubFights.get(position).getGroup_id())
                .child("Message")
                .child(timeStamp)
                .setValue(hashMap1);
        //**********************************

        int won_rounds = Integer.parseInt(modelPostClubFights.get(position).getUser_won());

        for (int i = 0;i < won_rounds;i++){
            String timeStam = String.valueOf(System.currentTimeMillis()+i);
            PostCount.increaseFightWin(timeStam,modelPostClubFights.get(position).getCreatore_id());
        }

    }


    private void setReject(int position){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pId", modelPostClubFights.get(position).getpId());
        hashMap.put("game_name",modelPostClubFights.get(position).getGame_name());
        hashMap.put("category","Club Screen");
        hashMap.put("scrim_type",modelPostClubFights.get(position).getScrim_type());
        hashMap.put("total_rounds",modelPostClubFights.get(position).getTotal_rounds());
        hashMap.put("user_won",modelPostClubFights.get(position).getUser_won());
        hashMap.put("group_id",modelPostClubFights.get(position).getGroup_id());
        hashMap.put("group_won", modelPostClubFights.get(position).getGroup_won());
        hashMap.put("creatore_id", modelPostClubFights.get(position).getCreatore_id());
        hashMap.put("content",modelPostClubFights.get(position).getContent());
        hashMap.put("status", "reject");

        FirebaseDatabase.getInstance().getReference("PostClubFight")
                .child(modelPostClubFights.get(position).getGroup_id())
                .child(modelPostClubFights.get(position).getpId())
                .setValue(hashMap);

        FirebaseDatabase.getInstance().getReference("PostUserClubFight")
                .child(modelPostClubFights.get(position).getCreatore_id())
                .child(modelPostClubFights.get(position).getpId())
                .setValue(hashMap);

        Toast.makeText(context, "Reject", Toast.LENGTH_SHORT).show();

        //sendClubMassage
        String timeStamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("msg", "");
        hashMap1.put("type", "text");
        hashMap1.put("timestamp", timeStamp);
        hashMap1.put("replayId","");
        hashMap1.put("replayMsg","");
        hashMap1.put("replayUserId","");
        hashMap1.put("creater_win_id",modelPostClubFights.get(position).getGroup_id());
        hashMap1.put("win_log_msg","Rejected a fight result");
        hashMap1.put("win_post_id", modelPostClubFights.get(position).getpId());
        hashMap1.put("win_type","club_fight_log");

        FirebaseDatabase.getInstance().getReference("Groups")
                .child(modelPostClubFights.get(position).getGroup_id())
                .child("Message")
                .child(timeStamp)
                .setValue(hashMap1);
        //**********************************

    }

}