package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.chat.FightLogPostCommentActivity;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.model.ModelVsFight;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.gaming.community.flexster.post.CommentActivity;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.gaming.community.flexster.send.SendToGroupActivity;
import com.gaming.community.flexster.send.SendToUserActivity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class FightListAdapter extends RecyclerView.Adapter<FightListAdapter.MyHolder>
{

    final Context context;
    final ArrayList<ModelVsFight> modelPosts;
    private RequestQueue requestQueue;
    private boolean notify = false;
    String selected_user="";

    public FightListAdapter(Context context, ArrayList<ModelVsFight> modelPosts,String u_id) {
        this.context = context;
        this.modelPosts = modelPosts;
        this.selected_user=u_id;
        Log.e("adaptercalledd","calleddadapter");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.design_fight_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position)
    {

        requestQueue = Volley.newRequestQueue(holder.itemView.getContext());

        Log.e("onbindview","bindcalled");

        ModelVsFight model=modelPosts.get(position);

        /*if(!model.getPhoto().equals("-"))
        {
            Glide.with(context).load(model.getPhoto()).thumbnail(0.1f).into(holder.mediaView);
        }*/

        //*****set posted image*****
        //Picasso.get().load(model.getPhoto()).into(holder.mediaView);

        //*****set profile photo*****
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

        //holder.wontv.setText(model.getWon_round() +" Out of "+model.getTotal_rounds());

        String category_1 = model.getCategory().substring(0,1);
        String category_2 = model.getCategory().substring(2,4);
        String category_3 = model.getCategory().substring(5,6);

        holder.txt_category.setText(category_1+"\n"+category_2+"\n"+category_3);

        holder.txt_game_name.setText(model.getGame_name());

        //Time
        long lastTime = Long.parseLong(model.getpId());
        holder.txt_time.setText("Fought "+GetTimeAgo.getTimeAgo(lastTime));

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

        //*****CheckLikes*****
        FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.likeLayout.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);
                    holder.noLikes.setText(String.valueOf(snapshot.getChildrenCount()));
                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        //CheckNew
                        FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){

                                    String react = snapshot.child("type").getValue().toString();
                                    if (react.equals("like")){
                                        holder.like_img.setImageResource(R.drawable.ic_thumb);
                                        holder.like_text.setText("GG");
                                    }
                                    if (react.equals("love")){
                                        holder.like_img.setImageResource(R.drawable.ic_love);
                                        holder.like_text.setText("On Fire!");
                                    }
                                    if (react.equals("laugh")){
                                        holder.like_img.setImageResource(R.drawable.ic_laugh);
                                        holder.like_text.setText("LOL");
                                    }
                                    if (react.equals("wow")){
                                        holder.like_img.setImageResource(R.drawable.ic_wow);
                                        holder.like_text.setText("Whoa!");
                                    }
                                    if (react.equals("sad")){
                                        holder.like_img.setImageResource(R.drawable.ic_sad);
                                        holder.like_text.setText("Oh No!");
                                    }
                                    if (react.equals("angry")){
                                        holder.like_img.setImageResource(R.drawable.ic_angry);
                                        holder.like_text.setText("WTH");
                                    }

                                }else {
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()){
                                                holder.like_img.setImageResource(R.drawable.ic_thumb);
                                                holder.like_text.setText("GG");
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
                    }else if (!snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        holder.like_img.setImageResource(R.drawable.ic_like);
                        holder.like_text.setText("React");
                    }
                    //QuickShow
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("like").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0) {
                                holder.thumb.setVisibility(View.VISIBLE);
                            }else {
                                holder.thumb.setVisibility(View.GONE);
                               /* FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            holder.thumb.setVisibility(View.VISIBLE);
                                        }else {
                                            holder.thumb.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("love").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                holder.love.setVisibility(View.VISIBLE);
                            }else {
                                holder.love.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("wow").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                holder.wow.setVisibility(View.VISIBLE);
                            }else {
                                holder.wow.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("angry").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                holder.angry.setVisibility(View.VISIBLE);
                            }else {
                                holder.angry.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("laugh").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                holder.laugh.setVisibility(View.VISIBLE);
                            }else {
                                holder.laugh.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).orderByChild("type").equalTo("sad").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                holder.sad.setVisibility(View.VISIBLE);
                            }else {
                                holder.sad.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    holder.likeLayout.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                    holder.like_img.setImageResource(R.drawable.ic_like);
                    holder.like_text.setText("React");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //*****Like*****
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(new int[]{
                        R.drawable.ic_thumb,
                        R.drawable.ic_love,
                        R.drawable.ic_laugh,
                        R.drawable.ic_wow,
                        R.drawable.ic_sad,
                        R.drawable.ic_angry
                })
                .withPopupAlpha(1)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (position1) -> {

            if (position1 == 0) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "like");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }else if (position1 == 1) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "love");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }
            else if (position1 == 2) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "laugh");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }      else if (position1 == 3) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "wow");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }
            else if (position1 == 4) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "sad");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }
            else if (position1 == 5) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(model.getCreatore_id(), "Reacted on post", model.getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify){
                                        //sendNotification(model.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "angry");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }

            return true;
        });

        //*****Like Functions*****
        holder.likeButtonTwo.setOnTouchListener(popup);

        FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        holder.likeButtonTwo.setVisibility(View.GONE);
                        holder.likeButton.setVisibility(View.VISIBLE);
                    }else {
                        holder.likeButton.setVisibility(View.GONE);
                        holder.likeButtonTwo.setVisibility(View.VISIBLE);
                    }
                }else {
                    holder.likeButton.setVisibility(View.GONE);
                    holder.likeButtonTwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likeButton.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Likes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(model.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(model.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                snapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }));

        //*****CheckComments*****
        FirebaseDatabase.getInstance().getReference("FightLog")
                .child(model.getCreatore_id())
                .child(model.getpId())
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    holder.layout.setVisibility(View.VISIBLE);
                    holder.commentLayout.setVisibility(View.VISIBLE);
                    holder.noComments.setText(String.valueOf(snapshot.getChildrenCount()));
                }else {
                    holder.commentLayout.setVisibility(View.GONE);
                    holder.noComments.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //*****Commment Functions*****
        holder.comment.setOnClickListener(v -> {

            Intent intent = new Intent(context, FightLogPostCommentActivity.class);
            //intent.putExtra("check_intent","Fight");
            intent.putExtra("user_id",selected_user);
            intent.putExtra("postID", model.getpId());
            context.startActivity(intent);

        });

        //Share
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu sharePop = new PopupMenu(wrapper, holder.share);
        //sharePop.getMenu().add(Menu.NONE,0,0, "App");
        sharePop.getMenu().add(Menu.NONE,0,0, "Chat");
        sharePop.getMenu().add(Menu.NONE,1,1, "Club");

        sharePop.setOnMenuItemClickListener(item -> {
            /*if (item.getItemId() == 0){

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                intent.putExtra(Intent.EXTRA_TEXT, holder.user1.getText().toString() +" vs "+ holder.user2.getText().toString() + " " + model.getContent());
                context.startActivity(Intent.createChooser(intent, "Share Via"));

            }*/
            if (item.getItemId() == 0){
                Intent intent = new Intent( holder.itemView.getContext(), SendToUserActivity.class);
                intent.putExtra("type", "user_fight_log");
                intent.putExtra("uri", model.getpId());
                intent.putExtra("post_id",model.getCreatore_id());
                intent.putExtra("win_post_id",model.getpId());
                intent.putExtra("win_type","user_fight_log");
                holder.itemView.getContext().startActivity(intent);
            }
            if (item.getItemId() == 1){
                Intent intent = new Intent( holder.itemView.getContext(), SendToGroupActivity.class);
                intent.putExtra("type", "user_fight_log");
                intent.putExtra("uri", model.getpId());
                intent.putExtra("creater_win_id",model.getCreatore_id());
                intent.putExtra("win_post_id",model.getpId());
                intent.putExtra("win_type","user_fight_log");
                holder.itemView.getContext().startActivity(intent);
            }
            return false;
        });

        holder.share.setOnClickListener(v -> sharePop.show());

    }


    @Override
    public int getItemCount() {
        Log.e("modelsizesidfs","bindcalled");

        return modelPosts.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView dpiv1,dpiv2;
        TextView user2,user1,game_nametv,wontv;
        LinearLayout likeButton;
        LinearLayout likeButtonTwo;
        ImageView thumb;
        ImageView love;
        ImageView laugh;
        ImageView wow;
        ImageView angry;
        ImageView sad;
        LinearLayout likeLayout;
        RelativeLayout line;
        ImageView like_img;
        TextView like_text;
        TextView noLikes;
        LinearLayout commentLayout;
        TextView noComments;
        LinearLayout comment;
        LinearLayout layout;
        LinearLayout share;
        SocialTextView text;
        ImageView verified1,admin1,verified2,admin2;
        TextView txt_post_count1,txt_fight_count1,txt_post_count2,txt_fight_count2;
        TextView txt_category,txt_total_round,txt_won,txt_lost,txt_game_name,txt_time;
        LinearLayout ll_main_bg;
        View view_line;
        LinearLayout ll_trophy1,ll_trophy2;

        LinearLayout ll_official,ll_unofficial;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //mediaView=itemView.findViewById(R.id.mediaView);
            dpiv1=itemView.findViewById(R.id.dpiv1);
            user2=itemView.findViewById(R.id.user2);
            user1=itemView.findViewById(R.id.user1);
            //wontv=itemView.findViewById(R.id.wontv);
            likeButton = itemView.findViewById(R.id.likeButton);
            likeButtonTwo = itemView.findViewById(R.id.likeButtonTwo);
            thumb = itemView.findViewById(R.id.thumb);
            love = itemView.findViewById(R.id.love);
            laugh = itemView.findViewById(R.id.laugh);
            wow = itemView.findViewById(R.id.wow);
            angry = itemView.findViewById(R.id.angry);
            sad = itemView.findViewById(R.id.sad);
            likeLayout = itemView.findViewById(R.id.likeLayout);
            line = itemView.findViewById(R.id.line);
            like_img = itemView.findViewById(R.id.like_img);
            like_text = itemView.findViewById(R.id.like_text);
            noLikes = itemView.findViewById(R.id.noLikes);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            noComments = itemView.findViewById(R.id.noComments);
            comment = itemView.findViewById(R.id.comment);
            layout = itemView.findViewById(R.id.layout);
            share = itemView.findViewById(R.id.share);
            text = itemView.findViewById(R.id.text);

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
            txt_game_name = itemView.findViewById(R.id.txt_game_name);
            txt_time = itemView.findViewById(R.id.txt_time);
            ll_main_bg = itemView.findViewById(R.id.ll_main_bg);
            view_line = itemView.findViewById(R.id.view_line);
            dpiv2  = itemView.findViewById(R.id.dpiv2);
            ll_trophy1 = itemView.findViewById(R.id.ll_trophy1);
            ll_trophy2 = itemView.findViewById(R.id.ll_trophy2);
            ll_official = itemView.findViewById(R.id.ll_official);
            ll_unofficial = itemView.findViewById(R.id.ll_unofficial);

        }
    }

    private void addToHisNotification(String hisUid, String message, String post){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", post);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Count").child(timestamp).setValue(true);
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
