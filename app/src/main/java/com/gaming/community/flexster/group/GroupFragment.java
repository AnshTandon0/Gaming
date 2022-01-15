package com.gaming.community.flexster.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.MainActivity;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterGroupPost;
import com.gaming.community.flexster.adapter.AdapterGroups;
import com.gaming.community.flexster.adapter.AdapterGroupsChatList;
import com.gaming.community.flexster.calling.RingingActivity;
import com.gaming.community.flexster.groupVoiceCall.RingingGroupVoiceActivity;
import com.gaming.community.flexster.model.ModelGroups;
import com.gaming.community.flexster.model.ModelPostGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupFragment extends AppCompatActivity {

    //Group
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroups;
    RecyclerView groups;

    RecyclerView post;
    AdapterGroupPost adapterPost;
    List<ModelPostGroup> modelPosts;

    AdapterGroupsChatList getAdapterGroups;
    List<ModelGroups> modelGroupsList;
    RecyclerView groups_chat;

    TabLayout tabLayout;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_fragment);

        //Groups
        groups = findViewById(R.id.groups);
        groups.setLayoutManager(new LinearLayoutManager(GroupFragment.this));
        modelGroups = new ArrayList<>();

        //Post
        post = findViewById(R.id.groups_post);
        post.setLayoutManager(new LinearLayoutManager(GroupFragment.this));
        modelPosts = new ArrayList<>();

        //Chat
        groups_chat = findViewById(R.id.groups_chat);
        groups_chat.setLayoutManager(new LinearLayoutManager(GroupFragment.this));
        modelGroupsList = new ArrayList<>();
        getChats();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                        for (DataSnapshot dataSnapshot1 : ds.child("Voice").getChildren()) {
                            if (Objects.requireNonNull(dataSnapshot1.child("type").getValue()).toString().equals("calling")) {

                                if (!Objects.requireNonNull(dataSnapshot1.child("from").getValue()).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    if (!dataSnapshot1.child("end").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        if (!dataSnapshot1.child("ans").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
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
        Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("to").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals("calling")) {
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

        //TabLayout
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    groups_chat.setVisibility(View.VISIBLE);
                    groups.setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    getChats();
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    groups_chat.setVisibility(View.GONE);
                    groups.setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    getAllPost();
                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    groups_chat.setVisibility(View.GONE);
                    groups.setVisibility(View.VISIBLE);
                    post.setVisibility(View.GONE);
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    getMyGroups();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.create).setOnClickListener(v -> startActivity(new Intent(GroupFragment.this, StepOneActivity.class)));

    }

    private void getChats() {
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroupsList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.child("status").getValue(String.class).equals("1"))
                    {

                        if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                            ModelGroups modelChatListGroups = ds.getValue(ModelGroups.class);
                            modelGroupsList.add(modelChatListGroups);
                        }
                        getAdapterGroups = new AdapterGroupsChatList(GroupFragment.this, modelGroupsList);
                        groups_chat.setAdapter(getAdapterGroups);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (getAdapterGroups.getItemCount() == 0) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            groups_chat.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            groups_chat.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelPosts.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                        for (DataSnapshot dataSnapshot1 : ds.child("Posts").getChildren()) {
                            ModelPostGroup modelPost = dataSnapshot1.getValue(ModelPostGroup.class);
                            modelPosts.add(modelPost);
                        }
                        adapterPost = new AdapterGroupPost(GroupFragment.this, modelPosts);
                        post.setAdapter(adapterPost);
                        adapterPost.notifyDataSetChanged();

                        sort_posts();

                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (adapterPost.getItemCount() == 0) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            post.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sort_posts() {
        List<ModelPostGroup> groupListCopy = new ArrayList<>();
        groupListCopy.addAll(modelPosts);

        modelPosts.clear();
        int size = groupListCopy.size();
        for (int i = 0; i < size; i++) {
            ModelPostGroup post = new ModelPostGroup();
            long a = 0;
            for (int j = 0; j < groupListCopy.size(); j++) {
                if (Long.parseLong(groupListCopy.get(j).getpTime()) > a) {
                    post = groupListCopy.get(j);
                    a = Long.parseLong(groupListCopy.get(j).getpTime());
                }
            }
            modelPosts.add(post);
            groupListCopy.remove(post);
        }
        adapterPost.notifyDataSetChanged();
    }

    private void getMyGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroups.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.child("status").getValue(String.class).equals("1"))
                    {
                        if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {
                            ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);
                            modelGroups.add(modelGroups1);
                        }
                        adapterGroups = new AdapterGroups(GroupFragment.this, modelGroups);
                        groups.setAdapter(adapterGroups);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (adapterGroups.getItemCount() == 0) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            groups.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            groups.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GroupFragment.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (tabLayout.getSelectedTabPosition() == 0) {
            groups_chat.setVisibility(View.VISIBLE);
            groups.setVisibility(View.GONE);
            post.setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getChats();
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            groups_chat.setVisibility(View.GONE);
            groups.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getAllPost();
        } else if (tabLayout.getSelectedTabPosition() == 2) {
            groups_chat.setVisibility(View.GONE);
            groups.setVisibility(View.VISIBLE);
            post.setVisibility(View.GONE);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            getMyGroups();
        }
    }

}