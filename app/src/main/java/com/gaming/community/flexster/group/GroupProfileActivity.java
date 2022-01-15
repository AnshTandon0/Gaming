package com.gaming.community.flexster.group;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.adapter.AdapterApproval;
import com.gaming.community.flexster.adapter.ClubFightLogAdapter;
import com.gaming.community.flexster.adapter.GroupFightListAdapter;
import com.gaming.community.flexster.adapter.GroupUserListAdapter;
import com.gaming.community.flexster.model.ModelGroupVsFight;
import com.gaming.community.flexster.model.ModelPostClubFight;
import com.gaming.community.flexster.model.ModelVsFight;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.MainActivity;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterGroupPost;
import com.gaming.community.flexster.calling.RingingActivity;
import com.gaming.community.flexster.groupVoiceCall.RingingGroupVoiceActivity;
import com.gaming.community.flexster.model.ModelPostGroup;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

@SuppressWarnings("ALL")
public class GroupProfileActivity extends AppCompatActivity {

    String groupId,myGroupRole,scrimster;
    LinearLayout link_layout;

    RecyclerView post;
    AdapterGroupPost adapterPost;
    List<ModelPostGroup> modelPosts;

    //memberTab
    RecyclerView member_list;
    List<ModelUser> userList;
    GroupUserListAdapter adapterUsers;
    List<String> list;
    String memberGroupRole="";

    //Bottom
    BottomSheetDialog more_options;
    LinearLayout members,add,announcement,mEdit,mLeave,delete,addPost,report,requestJoin,group_verification,group_verification_remove,official_members,ll_privacy;
    //Switch sw_clubs;
    String club_private="";

    private RequestQueue requestQueue;
    private boolean notify = false;

    NightMode sharedPref;
    boolean sendRequest = false;

    RecyclerView fight_log_list;
    GroupFightListAdapter groupFightListAdapter;
    ArrayList<ModelGroupVsFight> modelGroupVsFightArrayList;

    List<ModelUser> updatedlist=new ArrayList<>();
    List<String> rolesare=new ArrayList<>();

    RecyclerView club_fight_log_list;
    ArrayList<ModelPostClubFight> modelPostClubFights;
    ClubFightLogAdapter clubFightLogAdapter;

    TextView  txt_club_count;
    ImageView verify;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        txt_club_count = findViewById(R.id.txt_club_count);
        verify = findViewById(R.id.verify);

        requestQueue = Volley.newRequestQueue(GroupProfileActivity.this);

        groupId = getIntent().getStringExtra("group");
        String type = getIntent().getStringExtra("type");

        //setGroupLevel
        PostCount.getgrouplevel(groupId,txt_club_count);

        //getClubVerification
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (snapshot.child("clubVerified").exists()){

                                if (snapshot.child("clubVerified").getValue().toString().equals("true")){
                                    verify.setVisibility(View.VISIBLE);
                                }
                                else {
                                    verify.setVisibility(View.GONE);
                                }

                            }
                            else {
                                verify.setVisibility(View.GONE);
                            }

                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //***************

        //*****getclubPrivacy*****
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("status").getValue(String.class).equals("0"))
                {
                    findViewById(R.id.menu).setVisibility(View.GONE);
                    findViewById(R.id.lldelete).setVisibility(View.VISIBLE);
                    findViewById(R.id.scrollbar).setClickable(false);
                }

                club_private=Objects.requireNonNull(snapshot.child("clubPrivacy").getValue()).toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Post
        post = findViewById(R.id.post);
        post.setLayoutManager(new LinearLayoutManager(GroupProfileActivity.this , LinearLayoutManager.VERTICAL , true));
        modelPosts = new ArrayList<>();
        getAllPost();

        //memberTab
        //member_list = findViewById(R.id.member_list);
        //member_list.setLayoutManager(new LinearLayoutManager(GroupProfileActivity.this,LinearLayoutManager.VERTICAL , false));
        //userList = new ArrayList<>();
        //getMembers();

        //groupfightlogTab
        fight_log_list = findViewById(R.id.fight_log_list);
        fight_log_list.setLayoutManager(new LinearLayoutManager(GroupProfileActivity.this,LinearLayoutManager.VERTICAL , true));
        modelGroupVsFightArrayList = new ArrayList<>();
        getGroupFightLog();

        //clubfightLogtab
        club_fight_log_list = findViewById(R.id.club_fight_log_list);
        club_fight_log_list.setLayoutManager(new LinearLayoutManager(GroupProfileActivity.this,LinearLayoutManager.VERTICAL , true));
        modelPostClubFights = new ArrayList<>();
        getClubFightLog();


        //Back
        findViewById(R.id.back).setOnClickListener(v -> {
            if (type.equals("create")){
                Intent intent = new Intent(GroupProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else {
                onBackPressed();
            }
        });

        //PostIntent
        findViewById(R.id.create_post).setOnClickListener(v -> {
            Intent intent = new Intent(GroupProfileActivity.this, CreateGroupPostActivity.class);
            intent.putExtra("group", groupId);
            startActivity(intent);
            finish();
        });

        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CircleImageView circleImageView = findViewById(R.id.circleImageView);
                if (!Objects.requireNonNull(snapshot.child("photo").getValue()).toString().isEmpty()){
                    Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        findViewById(R.id.more).setOnClickListener(v -> more_options.show());
        findViewById(R.id.menu).setOnClickListener(v -> more_options.show());

        //Id
        TextView topName = findViewById(R.id.topName);
        TextView name = findViewById(R.id.name);
        TextView bio = findViewById(R.id.bio);
        TextView username = findViewById(R.id.username);
        TextView link = findViewById(R.id.link);
        TextView created = findViewById(R.id.location);
        CircleImageView dp = findViewById(R.id.dp);
        ImageView cover = findViewById(R.id.cover);
        TextView members = findViewById(R.id.members);
        TextView posts = findViewById(R.id.posts);
        link_layout = findViewById(R.id.link_layout);

        //Buttons
        Button edit = findViewById(R.id.edit);
        Button request = findViewById(R.id.request);
        Button cancel = findViewById(R.id.cancel);
        Button club_talk = findViewById(R.id.club_talk);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){
                        for (DataSnapshot dataSnapshot1 : ds.child("Voice").getChildren()){
                            if (Objects.requireNonNull(dataSnapshot1.child("type").getValue()).toString().equals("calling")){

                                if (!Objects.requireNonNull(dataSnapshot1.child("from").getValue()).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                    if (!dataSnapshot1.child("end").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        if (!dataSnapshot1.child("ans").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Intent intent = new Intent(getApplicationContext(), RingingGroupVoiceActivity.class);
                                            intent.putExtra("room", Objects.requireNonNull(dataSnapshot1.child("room").getValue()).toString());
                                            intent.putExtra("group", Objects.requireNonNull(ds.child("groupId").getValue()).toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Call
        Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("to").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()){
                        if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals("calling")){
                            Intent intent = new Intent(getApplicationContext(), RingingActivity.class);
                            intent.putExtra("room", Objects.requireNonNull(ds.child("room").getValue()).toString());
                            intent.putExtra("from", Objects.requireNonNull(ds.child("from").getValue()).toString());
                            intent.putExtra("call", Objects.requireNonNull(ds.child("call").getValue()).toString());
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //GroupInfo
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                topName.setText(Objects.requireNonNull(snapshot.child("gUsername").getValue()).toString());

                name.setText(Objects.requireNonNull(snapshot.child("gName").getValue()).toString());

                bio.setText(Objects.requireNonNull(snapshot.child("gBio").getValue()).toString());

                username.setText(Objects.requireNonNull(snapshot.child("gUsername").getValue()).toString());

                link.setText(Objects.requireNonNull(snapshot.child("gLink").getValue()).toString());

                long lastTime = Long.parseLong(Objects.requireNonNull(snapshot.child("timestamp").getValue()).toString());

                //Visibility
                if (bio.getText().length()>0){
                    bio.setVisibility(View.VISIBLE);
                }

                if (link.getText().length()>0){
                    link_layout.setVisibility(View.VISIBLE);
                }else{
                    link_layout.setVisibility(View.GONE);
                }

                FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(snapshot.child("createdBy").getValue()).toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        created.setText("Created by " + Objects.requireNonNull(snapshot.child("name").getValue()).toString() + " - " + GetTimeAgo.getTimeAgo(lastTime));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (!Objects.requireNonNull(snapshot.child("gIcon").getValue()).toString().isEmpty()) {
                    Picasso.get().load(Objects.requireNonNull(snapshot.child("gIcon").getValue()).toString()).into(dp);
                }

                //Cover
                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Cover").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()){
                           if (!Objects.requireNonNull(snapshot.child("cover").getValue()).toString().isEmpty()){
                               Picasso.get().load(Objects.requireNonNull(snapshot.child("cover").getValue()).toString()).into(cover);
                           }
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Participants
                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Participants").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists()){
                           members.setText(String.valueOf(snapshot.getChildrenCount()));
                       }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //Posts
                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            posts.setText(String.valueOf(snapshot.getChildrenCount()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                findViewById(R.id.progressBar).setVisibility(View.GONE);

                //Buttons
                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                        .child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    myGroupRole = ""+snapshot.child("role").getValue();
                                    scrimster = ""+snapshot.child("scrimster").getValue();
                                }else {
                                    myGroupRole = "visitor";
                                    scrimster = "no";
                                }
                                checkUserType();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                //EditProfile
                edit.setOnClickListener(v -> {
                    Intent intent = new Intent(GroupProfileActivity.this, EditGroupActivity.class);
                    intent.putExtra("group", groupId);
                    startActivity(intent);
                    finish();
                });

                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GroupProfileActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboard = (ClipboardManager) GroupProfileActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("text", link.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    }
                });

                link.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GroupProfileActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboard = (ClipboardManager) GroupProfileActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("text", link.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    }
                });

                //Leave
                /*leave.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if (snapshot1.exists()){
                                    snapshot1.getRef().removeValue();
                                    checkUserType();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        }));*/

                club_talk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupProfileActivity.this,GroupChatActivity.class);
                        intent.putExtra("group",groupId);
                        intent.putExtra("type","create");
                        startActivity(intent);
                    }
                });

                //join
                findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap1.put("role","guest");
                        hashMap1.put("scrimster", "no");
                        hashMap1.put("timestamp", groupId);
                        FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupId)
                                .child("Participants")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                .setValue(hashMap1);

                        String stamp = "" + System.currentTimeMillis();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("msg", "");
                        hashMap.put("type", "text");
                        hashMap.put("timestamp", stamp);
                        hashMap.put("replayId", "");
                        hashMap.put("replayMsg", "");
                        hashMap.put("replayUserId", "");
                        hashMap.put("creater_win_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("win_log_msg", "Joined the club");
                        hashMap.put("win_post_id", "");
                        hashMap.put("win_type", "");

                        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(stamp)
                                .setValue(hashMap);

                    }
                });

                //Request
                request.setOnClickListener(v -> {
                     sendRequest = true;
                    if (sendRequest){
                        HashMap<String, String> hashMap1 = new HashMap<>();
                        hashMap1.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        FirebaseDatabase.getInstance().getReference().child("Groups")
                                .child(groupId)
                                .child("Request")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(hashMap1);
                          sendRequest = false;
                        checkUserType();
                        Snackbar.make(v, "Request sent", Snackbar.LENGTH_LONG).show();
                    }
                });

                //Cancel
                cancel.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Request").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            snapshot.getRef().removeValue();
                            checkUserType();
                            Snackbar.make(v, "Request sent", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 1) {

                    findViewById(R.id.create_post).setVisibility(View.GONE);
                    //findViewById(R.id.nothing).setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    club_fight_log_list.setVisibility(View.GONE);
                    fight_log_list.setVisibility(View.VISIBLE);

                } else if (tabLayout.getSelectedTabPosition() == 0) {

                    findViewById(R.id.create_post).setVisibility(View.VISIBLE);
                    //findViewById(R.id.nothing).setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                    club_fight_log_list.setVisibility(View.GONE);
                    fight_log_list.setVisibility(View.GONE);

                }else if (tabLayout.getSelectedTabPosition() == 2){

                    findViewById(R.id.create_post).setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    club_fight_log_list.setVisibility(View.VISIBLE);
                    //findViewById(R.id.nothing).setVisibility(View.GONE);
                    fight_log_list.setVisibility(View.GONE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void getClubFightLog(){
        FirebaseDatabase.getInstance().getReference("PostClubFight").child(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        modelPostClubFights.clear();
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            ModelPostClubFight  md = ds.getValue(ModelPostClubFight.class);
                            if (md.getStatus().equals("approved")){
                                modelPostClubFights.add(md);
                            }
                        }

                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        clubFightLogAdapter = new ClubFightLogAdapter(GroupProfileActivity.this,modelPostClubFights);
                        club_fight_log_list.setAdapter(clubFightLogAdapter);

                        if (clubFightLogAdapter.getItemCount() == 0){
                            club_fight_log_list.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            //fight_log_list.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getGroupFightLog() {

        FirebaseDatabase.getInstance().getReference("GroupFightLog").child(groupId)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelGroupVsFightArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    ModelGroupVsFight modelGroupVsFight = ds.getValue(ModelGroupVsFight.class);
                    if(modelGroupVsFight.getGroup1_id().equals(groupId))
                    {
                        modelGroupVsFightArrayList.add(modelGroupVsFight);
                    }

                }

                groupFightListAdapter = new GroupFightListAdapter(GroupProfileActivity.this, modelGroupVsFightArrayList);
                fight_log_list.setHasFixedSize(true);
                fight_log_list.setAdapter(groupFightListAdapter);

                if (groupFightListAdapter.getItemCount() == 0){
                    fight_log_list.setVisibility(View.GONE);
                    findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                }else {
                    //fight_log_list.setVisibility(View.VISIBLE);
                    findViewById(R.id.nothing).setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setAdapters() {
        adapterUsers = new GroupUserListAdapter(GroupProfileActivity.this, updatedlist,groupId,memberGroupRole);
        member_list.setAdapter(adapterUsers);
        if (adapterUsers.getItemCount() == 0){
            //member_list.setVisibility(View.GONE);
            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
        }else {
            //member_list.setVisibility(View.VISIBLE);
            findViewById(R.id.nothing).setVisibility(View.GONE);
        }
    }

    private void checkUserType() {
        //Admin & Creator
        switch (myGroupRole) {
            case "co-owner":
                findViewById(R.id.edit).setVisibility(View.GONE);
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "owner":
                findViewById(R.id.edit).setVisibility(View.VISIBLE);
                //findViewById(R.id.leave).setVisibility(View.GONE);
                findViewById(R.id.club_talk).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "guest":
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "vip guest":
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "member":
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "senior":
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "mod":
                //findViewById(R.id.leave).setVisibility(View.VISIBLE);
                findViewById(R.id.club_talk).setVisibility(View.VISIBLE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                break;
            case "visitor":
                //findViewById(R.id.leave).setVisibility(View.GONE);
                findViewById(R.id.club_talk).setVisibility(View.GONE);
                findViewById(R.id.edit).setVisibility(View.GONE);
                findViewById(R.id.request).setVisibility(View.GONE);
                findViewById(R.id.cancel).setVisibility(View.GONE);
                findViewById(R.id.join).setVisibility(View.GONE);
                checkRequest();
                break;
        }
        options();
    }

    private void checkRequest() {

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                club_private=Objects.requireNonNull(snapshot.child("clubPrivacy").getValue()).toString();

                if (club_private.equals("public")){
                    findViewById(R.id.cancel).setVisibility(View.GONE);
                    findViewById(R.id.edit).setVisibility(View.GONE);
                    findViewById(R.id.request).setVisibility(View.GONE);
                    findViewById(R.id.join).setVisibility(View.VISIBLE);
                    //findViewById(R.id.leave).setVisibility(View.GONE);
                    findViewById(R.id.club_talk).setVisibility(View.GONE);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Request").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                if (snapshot.hasChild(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
                                    findViewById(R.id.cancel).setVisibility(View.VISIBLE);
                                    findViewById(R.id.edit).setVisibility(View.GONE);
                                    findViewById(R.id.request).setVisibility(View.GONE);
                                    findViewById(R.id.join).setVisibility(View.GONE);
                                    //findViewById(R.id.leave).setVisibility(View.GONE);
                                    findViewById(R.id.club_talk).setVisibility(View.GONE);
                                }else {
                                    findViewById(R.id.request).setVisibility(View.VISIBLE);
                                    findViewById(R.id.edit).setVisibility(View.GONE);
                                    findViewById(R.id.cancel).setVisibility(View.GONE);
                                    findViewById(R.id.join).setVisibility(View.GONE);
                                    //findViewById(R.id.leave).setVisibility(View.GONE);
                                    findViewById(R.id.club_talk).setVisibility(View.GONE);
                                }
                            }else {
                                findViewById(R.id.request).setVisibility(View.VISIBLE);
                                findViewById(R.id.edit).setVisibility(View.GONE);
                                findViewById(R.id.cancel).setVisibility(View.GONE);
                                findViewById(R.id.join).setVisibility(View.GONE);
                                //findViewById(R.id.leave).setVisibility(View.GONE);
                                findViewById(R.id.club_talk).setVisibility(View.GONE);
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

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Posts")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelPosts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelPostGroup modelPost = ds.getValue(ModelPostGroup.class);
                    modelPosts.add(modelPost);

                    adapterPost = new AdapterGroupPost(GroupProfileActivity.this, modelPosts);
                    post.setAdapter(adapterPost);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);

                    if (adapterPost.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        post.setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void options() {
        if (more_options == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.group_more, null);

            members = view.findViewById(R.id.members);
            add = view.findViewById(R.id.add);
            announcement = view.findViewById(R.id.announcement);
            mEdit = view.findViewById(R.id.edit);
            mLeave = view.findViewById(R.id.leave);
            delete = view.findViewById(R.id.delete);
            addPost = view.findViewById(R.id.addPost);
            report = view.findViewById(R.id.report);
            requestJoin = view.findViewById(R.id.requestJoin);
            group_verification = view.findViewById(R.id.group_verification);
            group_verification_remove = view.findViewById(R.id.group_verification_remove);
            official_members = view.findViewById(R.id.official_members);
            ll_privacy = view.findViewById(R.id.ll_privacy);
            ///sw_clubs = view.findViewById(R.id.sw_clubs);

            /*if (club_private.equals("public")){
                sw_clubs.setChecked(false);
            }
            else {
                sw_clubs.setChecked(true);
            }

            sw_clubs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (club_private.equals("public")){
                        club_private = "private";
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("clubPrivacy",club_private);
                        FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupId)
                                .updateChildren(hashMap);
                        Toast.makeText(GroupProfileActivity.this, "Private", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        club_private = "public";
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("clubPrivacy",club_private);
                        FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupId)
                                .updateChildren(hashMap);
                        Toast.makeText(GroupProfileActivity.this, "Public", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/

            //*****AdminInfo1*****
            FirebaseDatabase.getInstance().getReference("Admin").child(FirebaseAuth.getInstance().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                //getClubVerification
                                FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                                        .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){

                                            if (snapshot.child("clubVerified").exists()){

                                                if (snapshot.child("clubVerified").getValue().toString().equals("true")){
                                                    group_verification.setVisibility(View.GONE);
                                                    group_verification_remove.setVisibility(View.VISIBLE);
                                                }
                                                else {
                                                    group_verification.setVisibility(View.VISIBLE);
                                                    group_verification_remove.setVisibility(View.GONE);
                                                }

                                            }
                                            else {
                                                group_verification.setVisibility(View.VISIBLE);
                                            }

                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //***************

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            group_verification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("clubVerified", "true");
                    FirebaseDatabase.getInstance().getReference("Groups").child(groupId).updateChildren(hashMap);

                    Toast.makeText(GroupProfileActivity.this, "Club Verified", Toast.LENGTH_SHORT).show();
                    group_verification.setVisibility(View.GONE);
                    group_verification_remove.setVisibility(View.VISIBLE);

                }
            });

            group_verification_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("clubVerified", "false");
                    FirebaseDatabase.getInstance().getReference("Groups").child(groupId).updateChildren(hashMap);

                    Toast.makeText(GroupProfileActivity.this, "Club Verification Removed", Toast.LENGTH_SHORT).show();
                    group_verification.setVisibility(View.VISIBLE);
                    group_verification_remove.setVisibility(View.GONE);

                }
            });

            official_members.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    more_options.cancel();
                    Intent intent = new Intent(GroupProfileActivity.this, GroupMembersActivity.class);
                    intent.putExtra("group", groupId);
                    intent.putExtra("official","official");
                    startActivity(intent);
                    finish();
                }
            });

            //Admin & Creator
            switch (myGroupRole) {
                case "co-owner":
                    delete.setVisibility(View.GONE);
                    mLeave.setVisibility(View.VISIBLE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                case "owner":
                    mLeave.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
                case "guest":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
                case "vip guest":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
                case "member":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
                case "senior":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    requestJoin.setVisibility(View.GONE);
                    break;
                case "mod":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    add.setVisibility(View.VISIBLE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
                case "visitor":
                    delete.setVisibility(View.GONE);
                    announcement.setVisibility(View.GONE);
                    mEdit.setVisibility(View.GONE);
                    mLeave.setVisibility(View.GONE);
                    add.setVisibility(View.GONE);
                    addPost.setVisibility(View.GONE);
                    requestJoin.setVisibility(View.GONE);
                    ll_privacy.setVisibility(View.GONE);
                    break;
            }

            if (scrimster.equals("yes")){
                announcement.setVisibility(View.VISIBLE);
            }
            else {
                announcement.setVisibility(View.GONE);
            }

            requestJoin.setOnClickListener(v -> {
                more_options.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, JoinRequestActivity.class);
                intent.putExtra("group", groupId);
                startActivity(intent);
            });

            announcement.setOnClickListener(v -> {
                more_options.cancel();
                findViewById(R.id.extra).setVisibility(View.VISIBLE);
                textView = findViewById(R.id.textView);
                textView.setText("Scrim");
            });

            findViewById(R.id.imageView4).setOnClickListener(v -> {
                more_options.cancel();
                findViewById(R.id.extra).setVisibility(View.GONE);
            });

            EditText email = findViewById(R.id.email);
            findViewById(R.id.login).setOnClickListener(v -> {
                if (email.getText().toString().isEmpty()){
                    Snackbar.make(v, "Enter a message", Snackbar.LENGTH_SHORT).show();
                }else {
                    notify = true;
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ModelUser user = snapshot.getValue(ModelUser.class);
                            if (notify){
                                FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Participants")
                                        .addListenerForSingleValueEvent (new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()){

                                            FirebaseDatabase.getInstance().getReference("Groups")
                                                    .child(groupId)
                                                    .child("Participants")
                                                    .child(ds.child("id").getValue(String.class))
                                                    .child("GroupNotification")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()){

                                                                if (snapshot.child("notification").getValue().toString().equals("on")){
                                                                    sendNotification("Scrim!",ds.getKey(), Objects.requireNonNull(user).getName(), email.getText().toString());
                                                                    addToHisNotification(ds.getKey(), email.getText().toString());
                                                                }
                                                                else {
                                                                    addToHisNotification(ds.getKey(), email.getText().toString());
                                                                }

                                                            }
                                                            else {
                                                                sendNotification("Scrim!",ds.getKey(), Objects.requireNonNull(user).getName(), email.getText().toString());
                                                                addToHisNotification(ds.getKey(), email.getText().toString());
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                            findViewById(R.id.extra).setVisibility(View.GONE);
                                        }

                                        String timestamp = ""+System.currentTimeMillis();
                                        HashMap<Object, String> hashMap = new HashMap<>();
                                        hashMap.put("pId", "");
                                        hashMap.put("timestamp", timestamp);
                                        hashMap.put("pUid", groupId);
                                        hashMap.put("notification", email.getText().toString());
                                        hashMap.put("sUid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                                        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("AnnouncementScrim").child(timestamp).setValue(hashMap);

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

                    String stamp = ""+System.currentTimeMillis();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("msg", "");
                    hashMap.put("type", "text");
                    hashMap.put("timestamp", stamp);
                    hashMap.put("replayId","");
                    hashMap.put("replayMsg","");
                    hashMap.put("replayUserId","");
                    hashMap.put("creater_win_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("win_log_msg",email.getText().toString());
                    hashMap.put("win_post_id","");
                    hashMap.put("win_type","");

                    FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(stamp)
                            .setValue(hashMap);

                }
            });

            members.setOnClickListener(v -> {
                more_options.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, GroupMembersActivity.class);
                intent.putExtra("group", groupId);
                intent.putExtra("official","unofficial");
                startActivity(intent);
                finish();
            });

            add.setOnClickListener(v -> {
                more_options.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, AddGroupActivity.class);
                intent.putExtra("group", groupId);
                startActivity(intent);
                finish();
            });

            mEdit.setOnClickListener(v -> {
                more_options.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, EditGroupActivity.class);
                intent.putExtra("group", groupId);
                startActivity(intent);
                finish();
            });

            mLeave.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Groups")
                    .child(groupId)
                    .child("Participants")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot1) {
                            if (snapshot1.exists()){
                                more_options.cancel();
                                String stamp = "" + System.currentTimeMillis();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("msg", "");
                                hashMap.put("type", "text");
                                hashMap.put("timestamp", stamp);
                                hashMap.put("replayId", "");
                                hashMap.put("replayMsg", "");
                                hashMap.put("replayUserId", "");
                                hashMap.put("creater_win_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap.put("win_log_msg", "Left the club");
                                hashMap.put("win_post_id", "");
                                hashMap.put("win_type", "");

                                FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(stamp)
                                        .setValue(hashMap);
                                snapshot1.getRef().removeValue();
                                checkUserType();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    }));

            mLeave.setOnClickListener(v -> {
                more_options.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfileActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to leave this club ?");
                builder.setPositiveButton("Delete", (dialog, which) -> FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                if (snapshot1.exists()){

                                    String stamp = "" + System.currentTimeMillis();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("msg", "");
                                    hashMap.put("type", "text");
                                    hashMap.put("timestamp", stamp);
                                    hashMap.put("replayId", "");
                                    hashMap.put("replayMsg", "");
                                    hashMap.put("replayUserId", "");
                                    hashMap.put("creater_win_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    hashMap.put("win_log_msg", "Left the club");
                                    hashMap.put("win_post_id", "");
                                    hashMap.put("win_type", "");

                                    FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(stamp)
                                            .setValue(hashMap);

                                    snapshot1.getRef().removeValue();
                                    checkUserType();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        })).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });

            delete.setOnClickListener(v -> {
                more_options.cancel();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupProfileActivity.this);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this club ?");
                builder.setPositiveButton("Delete", (dialog, which) -> {

                    removeclub();

                }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            });

            addPost.setOnClickListener(v -> {
                more_options.cancel();
                Intent intent = new Intent(GroupProfileActivity.this, CreateGroupPostActivity.class);
                intent.putExtra("group", groupId);
                startActivity(intent);
                finish();
            });

            report.setOnClickListener(v -> {
                more_options.cancel();
                FirebaseDatabase.getInstance().getReference().child("GroupsReport").child(groupId).child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(true);
                Snackbar.make(v, "Reported", Snackbar.LENGTH_LONG).show();
            });

            more_options = new BottomSheetDialog(this);
            more_options.setContentView(view);
        }
    }


    private void removeclub()
    {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("status","0");
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("status").setValue("0")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(GroupProfileActivity.this, "Club deleted!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GroupProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupProfileActivity.this, "Failed Group deactivated", Toast.LENGTH_SHORT).show();
            }
        });

/*            FirebaseDatabase.getInstance().getReference().child("PostUserClubFight").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot)
                {
                    for(DataSnapshot snp1:snapshot.getChildren())
                    {
                        String id=snp1.getKey();
                        FirebaseDatabase.getInstance().getReference()
                                .child("PostUserClubFight").child(id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot)
                            {
                               for(DataSnapshot snp2:snapshot.getChildren())
                               {
                                    String groupid=snp2.child("group_id").getValue(String.class);
                                    Log.e("groupidissss",groupid);
                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error)
                {

                }
            });*/

    }

    private void addToHisNotification(String hisUid, String message){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", "");
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("AnNotifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("AnCount").child(timestamp).setValue(true);
    }

    private void sendNotification(final String title,final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + ": " + message, title, hisId, "profile", R.drawable.ic_push_notification);
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
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}