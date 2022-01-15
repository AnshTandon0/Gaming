package com.gaming.community.flexster.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gaming.community.flexster.menu.MenuActivity;
import com.gaming.community.flexster.notifications.AnNotificationScreen;
import com.gaming.community.flexster.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.adapter.AdapterLive;
import com.gaming.community.flexster.adapter.AdapterPodcast;
import com.gaming.community.flexster.adapter.AdapterPost;
import com.gaming.community.flexster.adapter.AdapterStory;
import com.gaming.community.flexster.faceFilters.FaceFilters;
import com.gaming.community.flexster.group.GroupFragment;
import com.gaming.community.flexster.model.ModelLive;
import com.gaming.community.flexster.model.ModelPost;
import com.gaming.community.flexster.model.ModelStory;
import com.gaming.community.flexster.notifications.NotificationScreen;
import com.gaming.community.flexster.post.CreatePostActivity;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.search.TrendingActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    //Post
    AdapterPost adapterPost;
    List<ModelPost> modelPosts;
    RecyclerView post;

    //Live
    private AdapterLive live;
    private List<ModelLive> modelLives;
    RecyclerView liveView;

    //Pod
    private AdapterPodcast podcast;
    private List<ModelLive> modelLiveList;
    RecyclerView podView;

    //Story
    private AdapterStory adapterStory;
    private List<ModelStory> modelStories;
    RecyclerView storyView;

    //Follow
    List<String> followingList;

    //OtherId;
    ProgressBar progressBar;
    TextView nothing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Post
        post = v.findViewById(R.id.post);
        post.setLayoutManager(new LinearLayoutManager(getContext()));
        modelPosts = new ArrayList<>();

        //PostIntent
        v.findViewById(R.id.create_post).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), CreatePostActivity.class)));

        //Search
        //v.findViewById(R.id.search).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), TrendingActivity.class)));

        v.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TrendingActivity.class));
//                getActivity().finish();
            }
        });

        //User Search
        //v.findViewById(R.id.search_user).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        v.findViewById(R.id.search_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
                getActivity().finish();
            }
        });

        //Groups
       /* v.findViewById(R.id.add).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), GroupFragment.class)));*/

        //Camera
        /*v.findViewById(R.id.camera).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), FaceFilters.class)));*/

        //menu
        /*v.findViewById(R.id.menu).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), MenuActivity.class)));*/

        //Notification
        FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Count")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    v.findViewById(R.id.bell).setVisibility(View.GONE);
                    v.findViewById(R.id.count).setVisibility(View.VISIBLE);
                    TextView count =  v.findViewById(R.id.count);
                    count.setText(String.valueOf(snapshot.getChildrenCount()));
                }else {
                    v.findViewById(R.id.bell).setVisibility(View.VISIBLE);
                    v.findViewById(R.id.count).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        v.findViewById(R.id.bell).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), NotificationScreen.class)));
        v.findViewById(R.id.count).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), NotificationScreen.class)));

        //AnNotification
        FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("AnCount")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            v.findViewById(R.id.an_bell).setVisibility(View.GONE);
                            v.findViewById(R.id.an_count).setVisibility(View.VISIBLE);
                            TextView an_count =  v.findViewById(R.id.an_count);
                            an_count.setText(String.valueOf(snapshot.getChildrenCount()));
                        }else {
                            v.findViewById(R.id.an_bell).setVisibility(View.VISIBLE);
                            v.findViewById(R.id.an_count).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        v.findViewById(R.id.an_bell).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), AnNotificationScreen.class)));
        v.findViewById(R.id.an_count).setOnClickListener(v1 -> startActivity(new Intent(getActivity(), AnNotificationScreen.class)));

        //Live
        liveView = v.findViewById(R.id.live_list);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        liveView.setLayoutManager(linearLayoutManager2);
        modelLives = new ArrayList<>();
        checkFollowing();

         FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                 .addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 CircleImageView circleImageView = v.findViewById(R.id.circleImageView);
                 if (!Objects.requireNonNull(snapshot.child("photo").getValue()).toString().isEmpty()){
                     Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(circleImageView);
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });

        //Pod
        podView = v.findViewById(R.id.pod_list);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        podView.setLayoutManager(linearLayoutManager3);
        modelLiveList = new ArrayList<>();

        //Story
        storyView = v.findViewById(R.id.story_list);
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        storyView.setLayoutManager(linearLayoutManager5);
        modelStories = new ArrayList<>();

        //OtherId
        progressBar = v.findViewById(R.id.progressBar);
        nothing = v.findViewById(R.id.nothing);

        return v;
    }

    private void getAllPost() {

        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("counter")
               .addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               modelPosts.clear();
               for (DataSnapshot ds: snapshot.getChildren()){
                   ModelPost modelPost = ds.getValue(ModelPost.class);
                   for (String id : followingList){
                       if (Objects.requireNonNull(modelPost).getId().equals(id)) {
                           modelPosts.add(modelPost);
                       }
                   }

                   Collections.reverse(modelPosts);
                   adapterPost = new AdapterPost(getActivity(), modelPosts);
                   post.setAdapter(adapterPost);
                   progressBar.setVisibility(View.GONE);
                   adapterPost.notifyDataSetChanged();
                   if (adapterPost.getItemCount() == 0){
                       progressBar.setVisibility(View.GONE);
                       post.setVisibility(View.GONE);
                       nothing.setVisibility(View.VISIBLE);
                   }else {
                       progressBar.setVisibility(View.GONE);
                       post.setVisibility(View.VISIBLE);
                       nothing.setVisibility(View.GONE);
                   }

                   //adapterPost.notifyDataSetChanged();
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
         FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                getAllPost();
                readLive();
                readPod();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void readStory(){
        FirebaseDatabase.getInstance().getReference("Story").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timecurrent = System.currentTimeMillis();
                modelStories.clear();
                for (String id : followingList){
                    int countStory = 0;
                    ModelStory modelStory = null;
                    for (DataSnapshot snapshot1 : snapshot.child(id).getChildren()){
                        modelStory = snapshot1.getValue(ModelStory.class);
                        if (timecurrent > Objects.requireNonNull(modelStory).getTimestart() && timecurrent < modelStory.getTimeend()){
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        modelStories.add(modelStory);
                    }
                }
                adapterStory = new AdapterStory(getContext(), modelStories);
                storyView.setAdapter(adapterStory);
                adapterStory.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPod() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Podcast");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelLiveList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelLive modelLive = ds.getValue(ModelLive.class);
                    for (String id : followingList){
                        if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelLive).getUserid()) && Objects.requireNonNull(modelLive).getUserid().equals(id)){
                            modelLiveList.add(modelLive);
                        }
                    }
                    podcast = new AdapterPodcast(getActivity(), modelLiveList);
                    podView.setAdapter(podcast);
                    podcast.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void readLive(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Live");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelLives.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelLive modelLive = ds.getValue(ModelLive.class);
                    for (String id : followingList){
                        if (!Objects.requireNonNull(firebaseUser).getUid().equals(Objects.requireNonNull(modelLive).getUserid()) && Objects.requireNonNull(modelLive).getUserid().equals(id)){
                            modelLives.add(modelLive);
                        }
                    }
                    live = new AdapterLive(getActivity(), modelLives);
                    liveView.setAdapter(live);
                    live.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}