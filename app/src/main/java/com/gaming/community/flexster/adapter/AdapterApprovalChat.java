package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelVsFight;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterApprovalChat extends RecyclerView.Adapter<AdapterApprovalChat.ApprovalChatViewHolder> {

    Context context;
    ArrayList<ModelVsFight> modelVsFights = new ArrayList<>();
    private RequestQueue requestQueue;
    private boolean notify = false;

    public AdapterApprovalChat(Context context, ArrayList<ModelVsFight> modelVsFights){
        this.context=context;
        this.modelVsFights=modelVsFights;
    }

    @NonNull
    @Override
    public AdapterApprovalChat.ApprovalChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_approval_chat,parent,false);
        return new AdapterApprovalChat.ApprovalChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterApprovalChat.ApprovalChatViewHolder holder, @SuppressLint("RecyclerView") int position) {

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());

        ModelVsFight model=modelVsFights.get(position);

        int total= Integer.parseInt((model.getTotal_rounds()));
        int won= Integer.parseInt(model.getWon_round());

        int dra=total-won;

        if(dra==won)
        {
            //holder.dpiv1.setImageResource(R.drawable.ic_draw);
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_draw_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#D29A0B"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //draw
        }
        else if(dra<won)
        {
            //holder.dpiv1.setImageResource(R.drawable.ic_win);
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_win_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#569C3E"));
            holder.ll_trophy1.setVisibility(View.VISIBLE);
            holder.ll_trophy2.setVisibility(View.GONE);
            //won
        }
        else{
            //holder.dpiv1.setImageResource(R.drawable.ic_lose);
            holder.ll_main_bg.setBackground(context.getResources().getDrawable(R.drawable.gradient_lose_bg));
            holder.view_line.setBackgroundColor(Color.parseColor("#DA4727"));
            holder.ll_trophy1.setVisibility(View.GONE);
            holder.ll_trophy2.setVisibility(View.VISIBLE);
            //lost
        }

        holder.txt_total_round.setText(model.getTotal_rounds());
        holder.txt_won.setText(model.getWon_round());
        holder.txt_lost.setText(String.valueOf(dra));

        String category_1 = model.getCategory().substring(0,1);
        String category_2 = model.getCategory().substring(2,4);
        String category_3 = model.getCategory().substring(5,6);

        holder.txt_category.setText(category_1+"\n"+category_2+"\n"+category_3);

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

        //chekstatus
        if (model.getStatus().equals("approved")){
            holder.ll_unofficial.setVisibility(View.GONE);
            holder.ll_official.setVisibility(View.VISIBLE);
        }
        else {
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

        //*****AdminInfo2*****
        FirebaseDatabase.getInstance().getReference("Admin").child(model.getUser2_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.admin2.setVisibility(View.VISIBLE);
                        } else {
                            holder.admin2.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //PostCount2
        PostCount.getfightlevel(model.getUser2_id(), holder.txt_fight_count2);
        PostCount.getengagelevel(model.getUser2_id(), holder.txt_post_count2);

        //*****User1 Info*****
        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getCreatore_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.dpiv1);
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

        //*****User2 Info*****
        FirebaseDatabase.getInstance().getReference().child("Users").child(model.getUser2_id())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.dpiv2);
                        holder.user2.setText(snapshot.child("name").getValue().toString());

                        holder.user2.setOnClickListener(v -> {
                            if (!model.getUser2_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                Intent intent = new Intent(context, UserProfileActivity.class);
                                intent.putExtra("hisUID", model.getUser2_id());
                                context.startActivity(intent);
                            }else {
                                Snackbar.make(v,"It's you",Snackbar.LENGTH_LONG).show();
                            }
                        });

                        //Verify
                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                            holder.verified2.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.verified2.setVisibility(View.GONE);
                        }


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
        return modelVsFights.size();
    }

    public class ApprovalChatViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll_main_bg;
        LinearLayout ll_trophy1,ll_trophy2;
        ImageView dpiv1,dpiv2;
        TextView user1,user2;
        ImageView verified1,admin1,verified2,admin2;
        TextView txt_post_count1,txt_fight_count1,txt_post_count2,txt_fight_count2;
        TextView txt_category,txt_total_round,txt_won,txt_lost;
        View view_line;
        LinearLayout ll_official,ll_unofficial;
        TextView txt_game_name,txt_time;
        SocialTextView text;
        LinearLayout approval_Button,unapproval_Button;

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
            txt_post_count1 = itemView.findViewById(R.id.txt_post_count1);
            txt_fight_count1 = itemView.findViewById(R.id.txt_fight_count1);
            txt_post_count2 = itemView.findViewById(R.id.txt_post_count2);
            txt_fight_count2 = itemView.findViewById(R.id.txt_fight_count2);
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

        }
    }

    private void setApproved(int position){

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", modelVsFights.get(position).getpId());
        hashMap.put("game_name",modelVsFights.get(position).getGame_name());
        hashMap.put("category",modelVsFights.get(position).getCategory());
        hashMap.put("total_rounds",modelVsFights.get(position).getTotal_rounds());
        hashMap.put("won_round",modelVsFights.get(position).getWon_round());
        hashMap.put("creatore_won",modelVsFights.get(position).getCreatore_won());
        hashMap.put("user2_won", modelVsFights.get(position).getUser2_won());
        hashMap.put("user2_id",modelVsFights.get(position).getUser2_id());
        hashMap.put("creatore_id",modelVsFights.get(position).getCreatore_id());
        hashMap.put("Status", "approved");
        hashMap.put("content",modelVsFights.get(position).getContent());
        hashMap.put("chk_main_id",modelVsFights.get(position).getCreatore_id());

        FirebaseDatabase.getInstance().getReference().child("FightLog")
                .child(modelVsFights.get(position).getCreatore_id())
                .child(modelVsFights.get(position).getpId()).setValue(hashMap);


        HashMap<Object, String> hashMap2 = new HashMap<>();
        hashMap2.put("pId", modelVsFights.get(position).getpId());
        hashMap2.put("game_name",modelVsFights.get(position).getGame_name());
        hashMap2.put("category",modelVsFights.get(position).getCategory());
        hashMap2.put("total_rounds",modelVsFights.get(position).getTotal_rounds());
        hashMap2.put("won_round",modelVsFights.get(position).getUser2_won());
        hashMap2.put("creatore_won",modelVsFights.get(position).getUser2_won());
        hashMap2.put("user2_won", modelVsFights.get(position).getCreatore_won());
        hashMap2.put("user2_id",modelVsFights.get(position).getCreatore_id());
        hashMap2.put("creatore_id",modelVsFights.get(position).getUser2_id());
        hashMap2.put("Status", "approved");
        hashMap2.put("content",modelVsFights.get(position).getContent());
        hashMap2.put("chk_main_id",modelVsFights.get(position).getCreatore_id());

        FirebaseDatabase.getInstance().getReference().child("FightLog")
                .child(modelVsFights.get(position).getUser2_id())
                .child(modelVsFights.get(position).getpId()).setValue(hashMap2);


        int total= Integer.parseInt(modelVsFights.get(position).getTotal_rounds());
        int won= Integer.parseInt(modelVsFights.get(position).getWon_round());

        int dra=total-won;

        if(dra>won)
        {
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("value", "1");

            FirebaseDatabase.getInstance().getReference("WinCount")
                    .child(modelVsFights.get(position).getChk_main_id())
                    .child(modelVsFights.get(position).getpId())
                    .setValue(hashMap3);
        }

        Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("receiver", modelVsFights.get(position).getCreatore_id());
        hashMap1.put("msg", "Approved a fight result");
        hashMap1.put("isSeen", false);
        hashMap1.put("timestamp", ""+System.currentTimeMillis());
        hashMap1.put("type", "text");
        hashMap1.put("post_id", modelVsFights.get(position).getCreatore_id());
        hashMap1.put("win_post_id",modelVsFights.get(position).getpId());
        hashMap1.put("win_type","user_fight_log");
        FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap1);

        int won_rounds = Integer.parseInt(modelVsFights.get(position).getWon_round());

        for (int i = 0;i < won_rounds;i++){
            String timeStamp = String.valueOf(System.currentTimeMillis()+i);
            PostCount.increaseFightWin(timeStamp,modelVsFights.get(position).getCreatore_id());
        }

    }

    private void setReject(int position){

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", modelVsFights.get(position).getpId());
        hashMap.put("game_name",modelVsFights.get(position).getGame_name());
        hashMap.put("category",modelVsFights.get(position).getCategory());
        hashMap.put("total_rounds",modelVsFights.get(position).getTotal_rounds());
        hashMap.put("won_round",modelVsFights.get(position).getWon_round());
        hashMap.put("creatore_won",modelVsFights.get(position).getCreatore_won());
        hashMap.put("user2_won", modelVsFights.get(position).getUser2_won());
        hashMap.put("user2_id",modelVsFights.get(position).getUser2_id());
        hashMap.put("creatore_id",modelVsFights.get(position).getCreatore_id());
        hashMap.put("Status", "reject");
        hashMap.put("content",modelVsFights.get(position).getContent());
        hashMap.put("chk_main_id",modelVsFights.get(position).getCreatore_id());

        FirebaseDatabase.getInstance().getReference().child("FightLog")
                .child(modelVsFights.get(position).getCreatore_id())
                .child(modelVsFights.get(position).getpId()).setValue(hashMap);

        HashMap<Object, String> hashMap2 = new HashMap<>();
        hashMap2.put("pId", modelVsFights.get(position).getpId());
        hashMap2.put("game_name",modelVsFights.get(position).getGame_name());
        hashMap2.put("category",modelVsFights.get(position).getCategory());
        hashMap2.put("total_rounds",modelVsFights.get(position).getTotal_rounds());
        hashMap2.put("won_round",modelVsFights.get(position).getUser2_won());
        hashMap2.put("creatore_won",modelVsFights.get(position).getUser2_won());
        hashMap2.put("user2_won", modelVsFights.get(position).getCreatore_won());
        hashMap2.put("user2_id",modelVsFights.get(position).getCreatore_id());
        hashMap2.put("creatore_id",modelVsFights.get(position).getUser2_id());
        hashMap2.put("Status", "reject");
        hashMap2.put("content",modelVsFights.get(position).getContent());
        hashMap2.put("chk_main_id",modelVsFights.get(position).getCreatore_id());

        FirebaseDatabase.getInstance().getReference().child("FightLog")
                .child(modelVsFights.get(position).getUser2_id())
                .child(modelVsFights.get(position).getpId()).setValue(hashMap2);

        Toast.makeText(context, "Reject", Toast.LENGTH_SHORT).show();

        HashMap<String, Object> hashMap1 = new HashMap<>();
        hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap1.put("receiver", modelVsFights.get(position).getCreatore_id());
        hashMap1.put("msg", "Rejected a fight result");
        hashMap1.put("isSeen", false);
        hashMap1.put("timestamp", ""+System.currentTimeMillis());
        hashMap1.put("type", "text");
        hashMap1.put("post_id", modelVsFights.get(position).getCreatore_id());
        hashMap1.put("win_post_id",modelVsFights.get(position).getpId());
        hashMap1.put("win_type","user_fight_log");
        FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap1);

    }

}