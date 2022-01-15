package com.gaming.community.flexster.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.StickersPost;
import com.gaming.community.flexster.adapter.AdapterComment;
import com.gaming.community.flexster.model.ModelComment;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class FightLogPostCommentActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView comment_rv;
    String post_id = "";
    String user_id = "";
    List<ModelComment> commentsarray;
    AdapterComment adapterComment;
    LinearProgressIndicator progressBar;
    ImageView comment_send, add;
    LinearLayout main_ll;
    EditText editText_cmt;

    LinearLayout ll_main_bg;
    LinearLayout ll_trophy1,ll_trophy2;
    ImageView dpiv1,dpiv2;
    TextView user1,user2;
    ImageView verified1,admin1,verified2,admin2;
    TextView txt_post_count1,txt_fight_count1,txt_post_count2,txt_fight_count2;
    TextView txt_category;
    View view_line;
    TextView txt_total_round,txt_won,txt_lost;
    LinearLayout ll_official,ll_unofficial;
    TextView txt_game_name,txt_time;
    SocialTextView text;
    LinearLayout likeLayout;
    ImageView thumb,love,laugh,wow,sad,angry;
    TextView noLikes;
    TextView noComments;
    LinearLayout viewsLayout;
    TextView noViews;
    RelativeLayout line;
    LinearLayout likeButton,likeButtonTwo;
    ImageView like_img;
    TextView like_text;
    LinearLayout comment,share;
    ImageView back;

    private RequestQueue requestQueue;

    private boolean notify = false;

    //Bottom
    BottomSheetDialog comment_more;
    LinearLayout image, video, gif, camera;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int VIDEO_PICK_CODE = 1003;
    private static final int PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_log_post_comment);
        init();
        getcommentdatas();

        commentMore();
        comment_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendcommentcodes();
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_more.show();

            }
        });

    }

    private void sendcommentcodes() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        String mComment = editText_cmt.getText().toString();
        if (mComment.isEmpty()) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Snackbar.make(main_ll, "Type something", Snackbar.LENGTH_LONG).show();
        } else {
            String timeStamp = "" + System.currentTimeMillis();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("comment", mComment);
            hashMap.put("timestamp", timeStamp);
            hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("type", "text");
            hashMap.put("pId", post_id);
            FirebaseDatabase.getInstance().getReference("FightLog")
                    .child(user_id)
                    .child(post_id)
                    .child("Comments")
                    .child(timeStamp)
                    .setValue(hashMap);

            //increaseComment
            PostCount.increaseComment(user_id, timeStamp);

            progressBar.setVisibility(View.GONE);
            Snackbar.make(main_ll, "Opinion posted", Snackbar.LENGTH_LONG).show();
            editText_cmt.setText("");
            addToHisNotification(user_id, "Posted an opinion");
            notify = true;
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ModelUser user = snapshot.getValue(ModelUser.class);
                    if (notify) {
                        //sendNotification(user_id, Objects.requireNonNull(user).getName(), "Commented on your post");
                    }
                    notify = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }
    }

    private void getcommentdatas() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        comment_rv.setLayoutManager(layoutManager);
        comment_rv.setHasFixedSize(true);
        commentsarray = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("FightLog").child(user_id).child(post_id)
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        commentsarray.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                ModelComment modelComments = ds.getValue(ModelComment.class);
                                commentsarray.add(modelComments);
                                adapterComment = new AdapterComment(getApplicationContext(), commentsarray);
                                comment_rv.setAdapter(adapterComment);
                                adapterComment.notifyDataSetChanged();
                            }
                        }
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        Log.e("commentarraysizeissss", String.valueOf(commentsarray.size()));

    }

    public void init() {
        requestQueue = Volley.newRequestQueue(FightLogPostCommentActivity.this);
        add = findViewById(R.id.add);

        main_ll = findViewById(R.id.main_linear);
        editText_cmt = findViewById(R.id.editText_cmt);
        comment_send = findViewById(R.id.comment_send);

        progressBar = findViewById(R.id.progressBar);
        comment_rv = findViewById(R.id.comment_rv);

        back = findViewById(R.id.back);

        //fightlogpost
        ll_main_bg = findViewById(R.id.ll_main_bg);
        ll_trophy1 = findViewById(R.id.ll_trophy1);
        ll_trophy2 = findViewById(R.id.ll_trophy2);
        dpiv1 = findViewById(R.id.dpiv1);
        dpiv2 = findViewById(R.id.dpiv2);
        user1 = findViewById(R.id.user1);
        user2 = findViewById(R.id.user2);
        verified1 = findViewById(R.id.verified1);
        admin1 = findViewById(R.id.admin1);
        verified2 = findViewById(R.id.verified2);
        admin2 = findViewById(R.id.admin2);
        txt_post_count1 = findViewById(R.id.txt_post_count1);
        txt_fight_count1 = findViewById(R.id.txt_fight_count1);
        txt_post_count2 = findViewById(R.id.txt_post_count2);
        txt_fight_count2 = findViewById(R.id.txt_fight_count2);
        txt_category = findViewById(R.id.txt_category);
        view_line = findViewById(R.id.view_line);
        txt_total_round = findViewById(R.id.txt_total_round);
        txt_won = findViewById(R.id.txt_won);
        txt_lost = findViewById(R.id.txt_lost);
        ll_official = findViewById(R.id.ll_official);
        ll_unofficial = findViewById(R.id.ll_unofficial);
        txt_game_name = findViewById(R.id.txt_game_name);
        txt_time = findViewById(R.id.txt_time);
        text = findViewById(R.id.text);
        likeLayout = findViewById(R.id.likeLayout);
        thumb = findViewById(R.id.thumb);
        love = findViewById(R.id.love);
        laugh = findViewById(R.id.laugh);
        wow = findViewById(R.id.wow);
        sad = findViewById(R.id.sad);
        angry= findViewById(R.id.angry);
        noLikes = findViewById(R.id.noLikes);
        noComments = findViewById(R.id.noComments);
        viewsLayout = findViewById(R.id.viewsLayout);
        noViews = findViewById(R.id.noViews);
        line = findViewById(R.id.line);
        likeButton = findViewById(R.id.likeButton);
        like_img = findViewById(R.id.like_img);
        like_text = findViewById(R.id.like_text);
        likeButtonTwo = findViewById(R.id.likeButtonTwo);
        comment = findViewById(R.id.comment);
        share = findViewById(R.id.share);

        if (getIntent().hasExtra("gif"))
        {
            post_id = getIntent().getStringExtra("postID");
            user_id = getIntent().getStringExtra("user_id");

            String timeStamp = ""+System.currentTimeMillis();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("cId", timeStamp);
            hashMap.put("comment", getIntent().getStringExtra("gif"));
            hashMap.put("timestamp",  timeStamp);
            hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("type", "gif");
            hashMap.put("pId", post_id);
            FirebaseDatabase.getInstance().getReference("FightLog")
                    .child(user_id)
                    .child(post_id)
                    .child("Comments")
                    .child(timeStamp)
                    .setValue(hashMap);
            //increaseComment
            PostCount.increaseComment(user_id,timeStamp);

            findViewById(R.id.progressBar).setVisibility(View.GONE);
            Snackbar.make(main_ll, "Sticker posted", Snackbar.LENGTH_LONG).show();
        }
        else
        {
            user_id = getIntent().getStringExtra("user_id");
            post_id = getIntent().getStringExtra("postID");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        FirebaseDatabase.getInstance().getReference("FightLog").child(user_id)
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    if (ds.getKey().equals(post_id)){

                        ModelVsFight modelfights = ds.getValue(ModelVsFight.class);

                        int total= Integer.parseInt((modelfights.getTotal_rounds()));
                        int won= Integer.parseInt(modelfights.getWon_round());

                        int dra=total-won;

                        if(dra==won)
                        {
                            //holder.dpiv1.setImageResource(R.drawable.ic_draw);
                            ll_main_bg.setBackground(getResources().getDrawable(R.drawable.gradient_draw_bg));
                            view_line.setBackgroundColor(Color.parseColor("#D29A0B"));
                            ll_trophy1.setVisibility(View.GONE);
                            ll_trophy2.setVisibility(View.GONE);
                            //draw
                        }
                        else if(dra<won)
                        {
                            //holder.dpiv1.setImageResource(R.drawable.ic_win);
                            ll_main_bg.setBackground(getResources().getDrawable(R.drawable.gradient_win_bg));
                            view_line.setBackgroundColor(Color.parseColor("#569C3E"));
                            ll_trophy1.setVisibility(View.VISIBLE);
                            ll_trophy2.setVisibility(View.GONE);
                            //won
                        }
                        else{
                            //holder.dpiv1.setImageResource(R.drawable.ic_lose);
                            ll_main_bg.setBackground(getResources().getDrawable(R.drawable.gradient_lose_bg));
                            view_line.setBackgroundColor(Color.parseColor("#DA4727"));
                            ll_trophy1.setVisibility(View.GONE);
                            ll_trophy2.setVisibility(View.VISIBLE);
                            //lost
                        }

                        txt_total_round.setText(modelfights.getTotal_rounds());
                        txt_won.setText(modelfights.getWon_round());
                        txt_lost.setText(String.valueOf(dra));

                        String category_1 = modelfights.getCategory().substring(0,1);
                        String category_2 = modelfights.getCategory().substring(2,4);
                        String category_3 = modelfights.getCategory().substring(5,6);

                        txt_category.setText(category_1+"\n"+category_2+"\n"+category_3);

                        txt_game_name.setText(modelfights.getGame_name());

                        //Time
                        long lastTime = Long.parseLong(modelfights.getpId());
                        txt_time.setText("Fought "+ GetTimeAgo.getTimeAgo(lastTime));

                        if (modelfights.getContent().equals("")){
                            text.setVisibility(View.GONE);
                        }
                        else {
                            text.setText(modelfights.getContent());
                        }

                        //chekstatus
                        if (modelfights.getStatus().equals("approved")){
                            ll_unofficial.setVisibility(View.GONE);
                            ll_official.setVisibility(View.VISIBLE);
                        }
                        else {
                            ll_official.setVisibility(View.GONE);
                            ll_unofficial.setVisibility(View.VISIBLE);
                        }

                        //*****AdminInfo1*****
                        FirebaseDatabase.getInstance().getReference("Admin").child(modelfights.getCreatore_id())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            admin1.setVisibility(View.VISIBLE);
                                        } else {
                                            admin1.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        //PostCount1
                        PostCount.getfightlevel(modelfights.getCreatore_id(), txt_fight_count1);
                        PostCount.getengagelevel(modelfights.getCreatore_id(), txt_post_count1);

                        //*****AdminInfo2*****
                        FirebaseDatabase.getInstance().getReference("Admin").child(modelfights.getUser2_id())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            admin2.setVisibility(View.VISIBLE);
                                        } else {
                                            admin2.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        //PostCount2
                        PostCount.getfightlevel(modelfights.getUser2_id(), txt_fight_count2);
                        PostCount.getengagelevel(modelfights.getUser2_id(), txt_post_count2);

                        //*****User1 Info*****
                        FirebaseDatabase.getInstance().getReference().child("Users").child(modelfights.getCreatore_id())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(dpiv1);
                                        user1.setText(snapshot.child("name").getValue().toString());

                                        user1.setOnClickListener(v -> {
                                            if (!modelfights.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                Intent intent = new Intent(FightLogPostCommentActivity.this, UserProfileActivity.class);
                                                intent.putExtra("hisUID", modelfights.getCreatore_id());
                                                startActivity(intent);
                                            }else {
                                                Snackbar.make(v,"It's you",Snackbar.LENGTH_LONG).show();
                                            }
                                        });

                                        //Verify
                                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                                            verified1.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            verified1.setVisibility(View.GONE);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        //*****User2 Info*****
                        FirebaseDatabase.getInstance().getReference().child("Users").child(modelfights.getUser2_id())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(dpiv2);
                                        user2.setText(snapshot.child("name").getValue().toString());

                                        user2.setOnClickListener(v -> {
                                            if (!modelfights.getUser2_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                Intent intent = new Intent(FightLogPostCommentActivity.this, UserProfileActivity.class);
                                                intent.putExtra("hisUID", modelfights.getUser2_id());
                                                startActivity(intent);
                                            }else {
                                                Snackbar.make(v,"It's you",Snackbar.LENGTH_LONG).show();
                                            }
                                        });

                                        //Verify
                                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                                            verified2.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            verified2.setVisibility(View.GONE);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        //*****CheckLikes*****
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId())
                                .addValueEventListener(new ValueEventListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    likeLayout.setVisibility(View.VISIBLE);
                                    line.setVisibility(View.VISIBLE);
                                    noLikes.setText(String.valueOf(snapshot.getChildrenCount()));
                                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        //CheckNew
                                        FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId())
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){

                                                    String react = snapshot.child("type").getValue().toString();
                                                    if (react.equals("like")){
                                                        like_img.setImageResource(R.drawable.ic_thumb);
                                                        like_text.setText("GG");
                                                    }
                                                    if (react.equals("love")){
                                                        like_img.setImageResource(R.drawable.ic_love);
                                                        like_text.setText("On Fire!");
                                                    }
                                                    if (react.equals("laugh")){
                                                        like_img.setImageResource(R.drawable.ic_laugh);
                                                        like_text.setText("LOL");
                                                    }
                                                    if (react.equals("wow")){
                                                        like_img.setImageResource(R.drawable.ic_wow);
                                                        like_text.setText("Whoa!");
                                                    }
                                                    if (react.equals("sad")){
                                                        like_img.setImageResource(R.drawable.ic_sad);
                                                        like_text.setText("Oh No!");
                                                    }
                                                    if (react.equals("angry")){
                                                        like_img.setImageResource(R.drawable.ic_angry);
                                                        like_text.setText("WTH");
                                                    }

                                                }else {
                                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId())
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){
                                                                like_img.setImageResource(R.drawable.ic_thumb);
                                                                like_text.setText("GG");
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
                                        like_img.setImageResource(R.drawable.ic_like);
                                        like_text.setText("React");
                                    }
                                    //QuickShow
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("like")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0) {
                                                thumb.setVisibility(View.VISIBLE);
                                            }else {
                                                thumb.setVisibility(View.GONE);
                                                /*FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()){
                                                            thumb.setVisibility(View.VISIBLE);
                                                        }else {
                                                            thumb.setVisibility(View.GONE);
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
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("love")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0){
                                                love.setVisibility(View.VISIBLE);
                                            }else {
                                                love.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("wow")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0){
                                                wow.setVisibility(View.VISIBLE);
                                            }else {
                                                wow.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("angry")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0){
                                                angry.setVisibility(View.VISIBLE);
                                            }else {
                                                angry.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("laugh")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0){
                                                laugh.setVisibility(View.VISIBLE);
                                            }else {
                                                laugh.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).orderByChild("type").equalTo("sad")
                                            .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount()>0){
                                                sad.setVisibility(View.VISIBLE);
                                            }else {
                                                sad.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }else {
                                    likeLayout.setVisibility(View.GONE);
                                    line.setVisibility(View.GONE);
                                    like_img.setImageResource(R.drawable.ic_like);
                                    like_text.setText("React");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //*****Like*****
                        ReactionsConfig config = new ReactionsConfigBuilder(FightLogPostCommentActivity.this)
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

                        ReactionPopup popup = new ReactionPopup(FightLogPostCommentActivity.this, config, (position1) -> {

                            if (position1 == 0) {
                                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "like");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "love");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "laugh");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "wow");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "sad");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            addToHisNotification1(modelfights.getCreatore_id(), "Reacted on post", modelfights.getpId());
                                            notify = true;
                                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                                    if (notify){
                                                        //sendNotification(modelfights.getCreatore_id(), Objects.requireNonNull(user).getName(), "liked on your post");
                                                    }
                                                    notify = false;
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("type", "angry");
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
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
                        likeButtonTwo.setOnTouchListener(popup);

                        FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        likeButtonTwo.setVisibility(View.GONE);
                                        likeButton.setVisibility(View.VISIBLE);
                                    }else {
                                        likeButton.setVisibility(View.GONE);
                                        likeButtonTwo.setVisibility(View.VISIBLE);
                                    }
                                }else {
                                    likeButton.setVisibility(View.GONE);
                                    likeButtonTwo.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        likeButton.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Likes")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child(modelfights.getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelfights.getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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

                        //*****Commment Functions*****
                        comment.setOnClickListener(v -> {

                            //Toast.makeText(FightLogPostCommentActivity.this, "", Toast.LENGTH_SHORT).show();

                        });

                        //Share
                        Context wrapper = new ContextThemeWrapper(FightLogPostCommentActivity.this, R.style.popupMenuStyle);
                        PopupMenu sharePop = new PopupMenu(wrapper, share);
                        //sharePop.getMenu().add(Menu.NONE,0,0, "App");
                        sharePop.getMenu().add(Menu.NONE,0,0, "Chat");
                        sharePop.getMenu().add(Menu.NONE,1,1, "Club");

                        sharePop.setOnMenuItemClickListener(item -> {
                            /*if (item.getItemId() == 0){

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/*");
                                intent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
                                intent.putExtra(Intent.EXTRA_TEXT, user1.getText().toString() +" vs "+ user2.getText().toString() + " " + modelfights.getContent());
                                startActivity(Intent.createChooser(intent, "Share Via"));

                            }*/
                            if (item.getItemId() == 0){
                                Intent intent = new Intent( FightLogPostCommentActivity.this, SendToUserActivity.class);
                                intent.putExtra("type", "user_fight_log");
                                intent.putExtra("uri", modelfights.getpId());
                                intent.putExtra("post_id",modelfights.getCreatore_id());
                                intent.putExtra("win_post_id",modelfights.getpId());
                                intent.putExtra("win_type","user_fight_log");
                                startActivity(intent);
                            }
                            if (item.getItemId() == 1){
                                Intent intent = new Intent( FightLogPostCommentActivity.this, SendToGroupActivity.class);
                                intent.putExtra("type", "user_fight_log");
                                intent.putExtra("uri", modelfights.getpId());
                                intent.putExtra("creater_win_id",modelfights.getCreatore_id());
                                intent.putExtra("win_post_id",modelfights.getpId());
                                intent.putExtra("win_type","user_fight_log");
                                startActivity(intent);
                            }
                            return false;
                        });
                        share.setOnClickListener(v -> sharePop.show());

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void commentMore() {
        if (comment_more == null) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.comment_more, null);
            image = view.findViewById(R.id.image);
            image.setOnClickListener(this);
            video = view.findViewById(R.id.video);
            video.setOnClickListener(this);
            video.setVisibility(View.GONE);
            gif = view.findViewById(R.id.gif);
            gif.setOnClickListener(this);
            camera = view.findViewById(R.id.camera);
            camera.setOnClickListener(this);
            camera.setVisibility(View.GONE);
            comment_more = new BottomSheetDialog(this);
            comment_more.setContentView(view);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick (View v){
        switch (v.getId()) {
            case R.id.image:
                comment_more.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    } else {
                        pickImage();
                    }
                } else {
                    pickImage();
                }

                break;
            case R.id.gif:

                comment_more.cancel();
                Intent s = new Intent(FightLogPostCommentActivity.this, StickersPost.class);
                s.putExtra("activity", "fightpost");
                s.putExtra("postID", post_id);
                s.putExtra("user_id",user_id);
                startActivity(s);

                break;

            case R.id.camera:

                //Cam
                if (ContextCompat.checkSelfPermission(FightLogPostCommentActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FightLogPostCommentActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 99);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 130);
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            Uri img_uri = Objects.requireNonNull(data).getData();
            uploadImage(img_uri);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Snackbar.make(main_ll, "Please wait, sending...", Snackbar.LENGTH_LONG).show();
        }
        if (resultCode == RESULT_OK && requestCode == 130 && data != null)
        {
            Uri img_uri = Objects.requireNonNull(data).getData();
            if(img_uri!=null)
            {
                uploadImage(img_uri);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                Snackbar.make(main_ll, "Please wait, sending...", Snackbar.LENGTH_LONG).show();
            }
            else
            {
                Log.e("uriisnulll","null uri");
                Snackbar.make(main_ll, "Failed to capture image", Snackbar.LENGTH_LONG).show();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(Uri dp_uri) {
        String timeStamp = ""+System.currentTimeMillis();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("comment_photo/" + timeStamp);
        storageReference.putFile(dp_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("cId",  timeStamp);
                hashMap.put("comment", downloadUri.toString());
                hashMap.put("timestamp",  timeStamp);
                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("type", "image");
                hashMap.put("pId", post_id);
                FirebaseDatabase.getInstance().getReference("FightLog")
                        .child(user_id)
                        .child(post_id)
                        .child("Comments")
                        .child(timeStamp)
                        .setValue(hashMap);
                //increaseComment
                PostCount.increaseComment(FirebaseAuth.getInstance().getCurrentUser().getUid(),timeStamp);

                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Snackbar.make(main_ll, "Photo posted", Snackbar.LENGTH_LONG).show();
                addToHisNotification(user_id, "Posted an opinion");
                notify = true;
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUser user = snapshot.getValue(ModelUser.class);
                        if (notify){
                            //sendNotification(user_id, Objects.requireNonNull(user).getName(), "Commented on your post");
                        }
                        notify = false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

    }

    private void addToHisNotification1(String hisUid, String message, String post){
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


    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, VIDEO_PICK_CODE);
    }

    private void addToHisNotification(String hisUid, String message) {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", post_id);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "personalfightlog");
        hashMap.put("pUid", user_id);
        hashMap.put("notification", message);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Count").child(timestamp).setValue(true);
    }

    /*private void sendNotification(final String hisId, final String name, final String message) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, "New Message", hisId, "profile", R.drawable.logo);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse%s", response.toString()), error -> Timber.d("onResponse%s", error.toString())) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAALM19fr0:APA91bFu8hZyEWAUhRieVN8SHIkt5wV_Kb5f4aar4-Zang3fmD3BbdbqzxP4wNFOGx_C2Mc0fxNtYg4JCvVBDQUU3C7b-pkX4DGfMA5v93FIYtFKQ96Opb0ATQHR5lKoNVitdV9L8oNo");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }*/

}