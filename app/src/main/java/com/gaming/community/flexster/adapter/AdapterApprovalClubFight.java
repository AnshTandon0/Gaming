package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelGroupVsFight;
import com.gaming.community.flexster.model.ModelVsFight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterApprovalClubFight extends RecyclerView.Adapter<AdapterApprovalClubFight.ApprovalChatViewHolder> {

    Context context;
    ArrayList<ModelGroupVsFight> modelGroupVsFightArrayList = new ArrayList<>();
    private RequestQueue requestQueue;
    private boolean notify = false;

    public AdapterApprovalClubFight(Context context, ArrayList<ModelGroupVsFight> modelGroupVsFightArrayList){
        this.context=context;
        this.modelGroupVsFightArrayList=modelGroupVsFightArrayList;
    }

    @NonNull
    @Override
    public AdapterApprovalClubFight.ApprovalChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_approval_chat,parent,false);
        return new AdapterApprovalClubFight.ApprovalChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApprovalClubFight.ApprovalChatViewHolder holder, @SuppressLint("RecyclerView") int position) {

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());

        ModelGroupVsFight model=modelGroupVsFightArrayList.get(position);

        if (model.getStatus().equals("approved")){
            holder.ll_unofficial.setVisibility(View.GONE);
            holder.ll_official.setVisibility(View.VISIBLE);
        }
        else {
            holder.ll_official.setVisibility(View.GONE);
            holder.ll_unofficial.setVisibility(View.VISIBLE);
        }

        int total= Integer.parseInt((model.getTotal_rounds()));
        int won= Integer.parseInt(model.getGroup1_won());

        int dra=total-won;

        if(dra==won)
        {
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_draw_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#D29A0B"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //draw
        }
        else if(dra<won)
        {
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_win_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#569C3E"));
            holder.ll_trophy1.setVisibility(View.VISIBLE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //won
        }
        else{
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_lose_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#DA4727"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.VISIBLE);
            //lost
        }

        holder.txt_total_round.setText(model.getTotal_rounds());
        holder.txt_won.setText(model.getGroup1_won());
        holder.txt_lost.setText(String.valueOf(dra));

        String category = model.getCategory().substring(0,4);
        String category1 = model.getCategory().substring(5,8);

        holder.txt_category.setText(category+"\n"+category1);

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

        holder.ll_g_engagement1.setVisibility(View.GONE);
        holder.ll_g_skill1.setVisibility(View.GONE);
        holder.ll_g_engagement2.setVisibility(View.GONE);
        holder.ll_g_skill2.setVisibility(View.GONE);
        holder.ll_c_engagement1.setVisibility(View.VISIBLE);
        holder.ll_c_engagement2.setVisibility(View.VISIBLE);

        //group1 info
        FirebaseDatabase.getInstance().getReference().child("Groups").child(model.getGroup1_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("gIcon").getValue().toString().isEmpty()){
                            Picasso.get().load(snapshot.child("gIcon").getValue().toString()).into(holder.dpiv1);
                        }
                        else {
                            holder.dpiv1.setImageResource(R.drawable.group);
                        }

                        holder.user1.setText(snapshot.child("gName").getValue().toString());

                        //setGroupLevel
                        PostCount.getgrouplevel(snapshot.child("groupId").getValue().toString(),holder.txt_club_count1);

                        //getClubVerification
                        if (snapshot.child("clubVerified").exists()){

                            if (snapshot.child("clubVerified").getValue().toString().equals("true")){
                                holder.verified1.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.verified1.setVisibility(View.GONE);
                            }

                        }
                        else {
                            holder.verified1.setVisibility(View.GONE);
                        }
                        //*********

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //group2 info
        FirebaseDatabase.getInstance().getReference().child("Groups").child(model.getGroup2_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("gIcon").getValue().toString().isEmpty()){
                            Picasso.get().load(snapshot.child("gIcon").getValue().toString()).into(holder.dpiv2);
                        }
                        else {
                            holder.dpiv2.setImageResource(R.drawable.group);
                        }

                        holder.user2.setText(snapshot.child("gName").getValue().toString());

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

        holder.approval_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getChk_main_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                }
                else {
                    setApproved(position);
                }
            }
        });

        holder.unapproval_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getChk_main_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Toast.makeText(context, "You are not authorized", Toast.LENGTH_SHORT).show();
                }
                else {
                    setReject(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelGroupVsFightArrayList.size();
    }

    public class ApprovalChatViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll_main_bg;
        LinearLayout ll_trophy1,ll_trophy2;
        ImageView dpiv1,dpiv2;
        TextView user1,user2;
        ImageView verified1,admin1,verified2,admin2;
        TextView txt_club_count1,txt_club_count2;
        TextView txt_category,txt_total_round,txt_won,txt_lost;
        View view_line;
        LinearLayout ll_official,ll_unofficial;
        TextView txt_game_name,txt_time;
        SocialTextView text;
        LinearLayout approval_Button,unapproval_Button;
        LinearLayout ll_g_engagement1,ll_g_skill1,ll_c_engagement1,ll_g_engagement2,ll_g_skill2,ll_c_engagement2;

        public ApprovalChatViewHolder(@NonNull View itemView) {
            super(itemView);

            ll_main_bg = itemView.findViewById(R.id.ll_main_bg);
            ll_trophy1 = itemView.findViewById(R.id.ll_trophy1);
            ll_trophy2 = itemView.findViewById(R.id.ll_trophy2);
            dpiv1 = itemView.findViewById(R.id.dpiv1);
            dpiv2 = itemView.findViewById(R.id.dpiv2);
            user1 = itemView.findViewById(R.id.user1);
            user2 = itemView.findViewById(R.id.user2);
            verified1 = itemView.findViewById(R.id.verified1);
            admin1 = itemView.findViewById(R.id.admin1);
            verified2 = itemView.findViewById(R.id.verified2);
            admin2 = itemView.findViewById(R.id.admin2);
            txt_category = itemView.findViewById(R.id.txt_category);
            txt_total_round = itemView.findViewById(R.id.txt_total_round);
            txt_won = itemView.findViewById(R.id.txt_won);
            txt_lost = itemView.findViewById(R.id.txt_lost);
            view_line = itemView.findViewById(R.id.view_line);
            ll_official = itemView.findViewById(R.id.ll_official);
            ll_unofficial = itemView.findViewById(R.id.ll_unofficial);
            txt_game_name = itemView.findViewById(R.id.txt_game_name);
            txt_time = itemView.findViewById(R.id.txt_time);
            text = itemView.findViewById(R.id.text);
            approval_Button = itemView.findViewById(R.id.approval_Button);
            unapproval_Button = itemView.findViewById(R.id.unapproval_Button);
            ll_g_engagement1 = itemView.findViewById(R.id.ll_g_engagement1);
            ll_g_skill1 = itemView.findViewById(R.id.ll_g_skill1);
            ll_c_engagement1 = itemView.findViewById(R.id.ll_c_engagement1);
            ll_g_engagement2 = itemView.findViewById(R.id.ll_g_engagement2);
            ll_g_skill2 = itemView.findViewById(R.id.ll_g_skill2);
            ll_c_engagement2 = itemView.findViewById(R.id.ll_c_engagement2);
            txt_club_count1 = itemView.findViewById(R.id.txt_club_count1);
            txt_club_count2 = itemView.findViewById(R.id.txt_club_count2);

        }
    }

    private void setApproved(int position){

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", modelGroupVsFightArrayList.get(position).getpId());
        hashMap.put("game_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap.put("category",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap.put("total_rounds",modelGroupVsFightArrayList.get(position).getTotal_rounds());
        hashMap.put("group1_won",modelGroupVsFightArrayList.get(position).getGroup1_won());
        hashMap.put("group1_id",modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap.put("group1_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap.put("group2_won", modelGroupVsFightArrayList.get(position).getGroup2_won());
        hashMap.put("group2_id",modelGroupVsFightArrayList.get(position).getGroup2_id());
        hashMap.put("group2_name",modelGroupVsFightArrayList.get(position).getGroup2_name());
        hashMap.put("creatore_id",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap.put("photo", "");
        hashMap.put("content",modelGroupVsFightArrayList.get(position).getContent());
        hashMap.put("Status", "approved");
        hashMap.put("chk_main_id", modelGroupVsFightArrayList.get(position).getChk_main_id());

        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                .child(modelGroupVsFightArrayList.get(position).getGroup1_id())
                .child(modelGroupVsFightArrayList.get(position).getpId()).setValue(hashMap);

        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("pId", modelGroupVsFightArrayList.get(position).getpId());
        hashMap1.put("game_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap1.put("category",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap1.put("total_rounds",modelGroupVsFightArrayList.get(position).getTotal_rounds());
        hashMap1.put("group1_won",modelGroupVsFightArrayList.get(position).getGroup2_won());
        hashMap1.put("group1_id",modelGroupVsFightArrayList.get(position).getGroup2_id());
        hashMap1.put("group1_name",modelGroupVsFightArrayList.get(position).getGroup2_name());
        hashMap1.put("group2_won",modelGroupVsFightArrayList.get(position).getGroup1_won());
        hashMap1.put("group2_id",modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap1.put("group2_name",modelGroupVsFightArrayList.get(position).getGroup1_name());
        hashMap1.put("creatore_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("photo", "");
        hashMap1.put("content",modelGroupVsFightArrayList.get(position).getContent());
        hashMap1.put("Status", "approved");
        hashMap1.put("chk_main_id", modelGroupVsFightArrayList.get(position).getChk_main_id());

        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                .child(modelGroupVsFightArrayList.get(position).getGroup2_id())
                .child(modelGroupVsFightArrayList.get(position).getpId()).setValue(hashMap1);

        Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();

        //sendClubMassage
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap2.put("receiver", modelGroupVsFightArrayList.get(position).getCategory());
        hashMap2.put("msg", "Approved a fight result");
        hashMap2.put("isSeen", false);
        hashMap2.put("timestamp", ""+System.currentTimeMillis());
        hashMap2.put("type", "text");
        hashMap2.put("post_id", modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap2.put("win_post_id",modelGroupVsFightArrayList.get(position).getpId());
        hashMap2.put("win_type","");
        FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap2);

    }

    private void setReject(int position){

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", modelGroupVsFightArrayList.get(position).getpId());
        hashMap.put("game_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap.put("category",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap.put("total_rounds",modelGroupVsFightArrayList.get(position).getTotal_rounds());
        hashMap.put("group1_won",modelGroupVsFightArrayList.get(position).getGroup1_won());
        hashMap.put("group1_id",modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap.put("group1_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap.put("group2_won", modelGroupVsFightArrayList.get(position).getGroup2_won());
        hashMap.put("group2_id",modelGroupVsFightArrayList.get(position).getGroup2_id());
        hashMap.put("group2_name",modelGroupVsFightArrayList.get(position).getGroup2_name());
        hashMap.put("creatore_id",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap.put("photo", "");
        hashMap.put("content",modelGroupVsFightArrayList.get(position).getContent());
        hashMap.put("Status", "reject");
        hashMap.put("chk_main_id", modelGroupVsFightArrayList.get(position).getChk_main_id());

        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                .child(modelGroupVsFightArrayList.get(position).getGroup1_id())
                .child(modelGroupVsFightArrayList.get(position).getpId()).setValue(hashMap);

        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("pId", modelGroupVsFightArrayList.get(position).getpId());
        hashMap1.put("game_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap1.put("category",modelGroupVsFightArrayList.get(position).getCategory());
        hashMap1.put("total_rounds",modelGroupVsFightArrayList.get(position).getTotal_rounds());
        hashMap1.put("group1_won",modelGroupVsFightArrayList.get(position).getGroup2_won());
        hashMap1.put("group1_id",modelGroupVsFightArrayList.get(position).getGroup2_id());
        hashMap1.put("group1_name",modelGroupVsFightArrayList.get(position).getGroup2_name());
        hashMap1.put("group2_won",modelGroupVsFightArrayList.get(position).getGroup1_won());
        hashMap1.put("group2_id",modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap1.put("group2_name",modelGroupVsFightArrayList.get(position).getGame_name());
        hashMap1.put("creatore_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("photo", "");
        hashMap1.put("content",modelGroupVsFightArrayList.get(position).getContent());
        hashMap1.put("Status", "reject");
        hashMap1.put("chk_main_id", modelGroupVsFightArrayList.get(position).getChk_main_id());

        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                .child(modelGroupVsFightArrayList.get(position).getGroup2_id())
                .child(modelGroupVsFightArrayList.get(position).getpId()).setValue(hashMap1);

        Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();

        //sendClubMassage
        HashMap<String, Object> hashMap2 = new HashMap<>();
        hashMap2.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap2.put("receiver", modelGroupVsFightArrayList.get(position).getCategory());
        hashMap2.put("msg", "Approved a fight result");
        hashMap2.put("isSeen", false);
        hashMap2.put("timestamp", ""+System.currentTimeMillis());
        hashMap2.put("type", "text");
        hashMap2.put("post_id", modelGroupVsFightArrayList.get(position).getGroup1_id());
        hashMap2.put("win_post_id",modelGroupVsFightArrayList.get(position).getpId());
        hashMap2.put("win_type","");
        FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap2);

    }

}