package com.gaming.community.flexster.fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.adapter.ClubFightLogAdapter;
import com.gaming.community.flexster.adapter.FightListAdapter;
import com.gaming.community.flexster.group.GroupProfileActivity;
import com.gaming.community.flexster.model.ModelPostClubFight;
import com.gaming.community.flexster.model.ModelVsFight;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.MediaViewActivity;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterHigh;
import com.gaming.community.flexster.adapter.AdapterPost;
import com.gaming.community.flexster.adapter.AdapterReelView;
import com.gaming.community.flexster.menu.MenuActivity;
import com.gaming.community.flexster.model.ModelHigh;
import com.gaming.community.flexster.model.ModelPost;
import com.gaming.community.flexster.profile.EditProfileActivity;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.gaming.community.flexster.search.SearchActivity;
import com.gaming.community.flexster.who.FollowersActivity;
import com.gaming.community.flexster.who.FollowingActivity;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ProfileFragment extends Fragment {

    //Id
    VideoView videoView;
    ImageView cover,verify;
    CircleImageView dp;
    TextView name,username,location;
    SocialTextView bio,link;
    TextView followers,following,posts,topName;
    LinearLayout following_ly,followers_ly,link_layout,location_layout;
    View view;

    //Post
    AdapterPost adapterPost;
    List<ModelPost> modelPosts;
    RecyclerView post;

    //Story
    private AdapterHigh adapterHigh;
    private List<ModelHigh> modelHighs;
    RecyclerView storyView;

    private static final int TOTAL_ITEM_EACH_LOAD = 8;
    private int currentPage = 1;
    Button load;
    long initial,initial1;
    TextView nothing;
    ProgressBar progressBar;

    //Reel
    RecyclerView vsfight;
    AdapterReelView adapterReelView;
    ArrayList<ModelVsFight> modelfight;
    FightListAdapter fightadapter;

    ImageView admin;
    TextView txt_post_count,txt_fight_count;

    RecyclerView club_fight_log_list;
    ArrayList<ModelPostClubFight> modelPostClubFights;
    ClubFightLogAdapter clubFightLogAdapter;

    TextView txt_wins_count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        //Declaring
        videoView = view.findViewById(R.id.video);
        cover = view.findViewById(R.id.cover);
        dp = view.findViewById(R.id.dp);
        name = view.findViewById(R.id.name);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        location = view.findViewById(R.id.location);
        link = view.findViewById(R.id.link);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        posts = view.findViewById(R.id.posts);
        following_ly = view.findViewById(R.id.linearLayout5);
        followers_ly = view.findViewById(R.id.linearLayout4);
        verify = view.findViewById(R.id.verify);
        location_layout = view.findViewById(R.id.location_layout);
        link_layout = view.findViewById(R.id.link_layout);
        topName = view.findViewById(R.id.topName);
        admin = view.findViewById(R.id.admin);
        txt_post_count = view.findViewById(R.id.txt_post_count);
        txt_fight_count = view.findViewById(R.id.txt_fight_count);
        txt_wins_count = view.findViewById(R.id.txt_wins_count);

        //Post
        storyView = view.findViewById(R.id.story);
        LinearLayoutManager linearLayoutManager5 = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        storyView.setLayoutManager(linearLayoutManager5);
        modelHighs = new ArrayList<>();
        readStory();


        //OnStart
        view.findViewById(R.id.details).setVisibility(View.GONE);
        view.findViewById(R.id.bio).setVisibility(View.GONE);
        view.findViewById(R.id.name).setVisibility(View.GONE);
        view.findViewById(R.id.followers).setVisibility(View.GONE);
        view.findViewById(R.id.following).setVisibility(View.GONE);
        view.findViewById(R.id.posts).setVisibility(View.GONE);

         view.findViewById(R.id.edit).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
         });

        view.findViewById(R.id.menu).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MenuActivity.class);
            startActivity(intent);
        });


        //VideoView
        MediaController ctrl = new MediaController(getContext());
        ctrl.setVisibility(View.GONE);
        videoView.setMediaController(ctrl);
        setDimension();

        //PostCount
        PostCount.getfightlevel(FirebaseAuth.getInstance().getCurrentUser().getUid(),txt_fight_count);
        PostCount.getengagelevel(FirebaseAuth.getInstance().getCurrentUser().getUid(),txt_post_count);

        //AdminInfo
        FirebaseDatabase.getInstance().getReference("Admin").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            admin.setVisibility(View.VISIBLE);
                        } else {
                            admin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String mUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String mBio = Objects.requireNonNull(snapshot.child("bio").getValue()).toString();
                String mLocation = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                String mLink = Objects.requireNonNull(snapshot.child("link").getValue()).toString();
                String mVerify = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }

                name.setText(mName);
                username.setText(mUsername);
                location.setText(mLocation);
                bio.setLinkText(mBio);
                link.setLinkText(mLink);
                topName.setText(mUsername);

                if (mVerify.equals("yes")){
                    verify.setVisibility(View.VISIBLE);
                }else {
                    verify.setVisibility(View.GONE);
                }

                if (bio.getText().length()>0){
                    bio.setVisibility(View.VISIBLE);
                }else {
                    bio.setVisibility(View.GONE);
                }

                if (location.getText().length()>0){
                    location_layout.setVisibility(View.VISIBLE);
                }else{
                    location_layout.setVisibility(View.GONE);
                }

                if (link.getText().length()>0){
                    link_layout.setVisibility(View.VISIBLE);
                }else{
                    link_layout.setVisibility(View.GONE);
                }

                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("text", link.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    }
                });

                link.setOnLinkClickListener(new SocialTextView.OnLinkClickListener() {
                    @Override
                    public void onLinkClicked(int linkType, String matchedText) {
                        Toast.makeText(getActivity(), "Copied", Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("text", link.getText().toString());
                        clipboard.setPrimaryClip(clip);
                    }
                });

                bio.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }
                    else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });

                /*link.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });*/

                location.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + mLocation));
                    startActivity(i);
                });

                //OnDone
                view.findViewById(R.id.details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.followers).setVisibility(View.VISIBLE);
                view.findViewById(R.id.following).setVisibility(View.VISIBLE);
                view.findViewById(R.id.posts).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

        //Cover
        FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                    String uri = Objects.requireNonNull(snapshot.child("uri").getValue()).toString();

                    if (type.equals("image")){
                        Picasso.get().load(uri).placeholder(R.drawable.cover).into(cover);
                        videoView.setVisibility(View.GONE);
                        cover.setVisibility(View.VISIBLE);
                    }else if (type.equals("video")){
                        videoView.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.parse(uri));
                        videoView.start();
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                            mp.setVolume(0, 0);
                        });

                        videoView.setOnClickListener(v -> {
                            Intent i = new Intent(getContext(), MediaViewActivity.class);
                            i.putExtra("type", "video");
                            i.putExtra("uri", uri);
                            startActivity(i);
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.orderByChild("counter").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelPost post = snapshot.getValue(ModelPost.class);
                    if (Objects.requireNonNull(post).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        i++;
                    }
                }
                initial = i;
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference("WinCount")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            i++;
                        }
                        initial1 = i;
                        txt_wins_count.setText(""+i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        //Post
        post = view.findViewById(R.id.post);
        post.setLayoutManager(new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , true));
        modelPosts = new ArrayList<>();
        getAllPost();

        load = view.findViewById(R.id.load);
        load.setOnClickListener(v1 -> loadMoreData());
        nothing = view.findViewById(R.id.nothing);
        progressBar = view.findViewById(R.id.progressBar);

        getFollowers();
        getFollowing();

        followers_ly.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FollowersActivity.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        });

        following_ly.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FollowingActivity.class);
            intent.putExtra("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        });

        //Reels
        vsfight = view.findViewById(R.id.vsfight);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        vsfight.setLayoutManager(gridLayoutManager);
        modelfight = new ArrayList<>();
        getReel();

        //clubfightLogtab
        club_fight_log_list = view.findViewById(R.id.club_fight_log_list);
        club_fight_log_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL , true));
        modelPostClubFights = new ArrayList<>();
        getClubFightLog();

        //TabLayout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 1) {
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    vsfight.setVisibility(View.VISIBLE);
                    post.setVisibility(View.GONE);
                    load.setVisibility(View.GONE);
                    club_fight_log_list.setVisibility(View.GONE);
                } else if (tabLayout.getSelectedTabPosition() == 0) {
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    load.setVisibility(View.VISIBLE);
                    vsfight.setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                    club_fight_log_list.setVisibility(View.GONE);

                } else if (tabLayout.getSelectedTabPosition() == 2) {
                    view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                    load.setVisibility(View.GONE);
                    vsfight.setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    club_fight_log_list.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    private void getClubFightLog(){
        FirebaseDatabase.getInstance().getReference("PostUserClubFight").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        modelPostClubFights.clear();
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            ModelPostClubFight  md = ds.getValue(ModelPostClubFight.class);
                            modelPostClubFights.add(md);
                        }

                        view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                        clubFightLogAdapter = new ClubFightLogAdapter(getContext(),modelPostClubFights);
                        club_fight_log_list.setAdapter(clubFightLogAdapter);

                        if (clubFightLogAdapter.getItemCount() == 0){
                            club_fight_log_list.setVisibility(View.GONE);
                            nothing.setVisibility(View.VISIBLE);
                        }else {
                            //club_fight_log_list.setVisibility(View.VISIBLE);
                            nothing.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getReel() {

        FirebaseDatabase.getInstance().getReference("FightLog").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelfight.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    Log.e("calledmethidssds","one");
                    ModelVsFight modelfights = ds.getValue(ModelVsFight.class);
                    if(modelfights.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        modelfight.add(modelfights);
                    }
                    else if(!modelfights.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        if (modelfights.getStatus().equals("approved")){
                            modelfight.add(modelfights);
                        }
                    }

                }

                view.findViewById(R.id.progressBar).setVisibility(View.GONE);
                Collections.reverse(modelfight);
                fightadapter = new FightListAdapter(getContext(),modelfight,FirebaseAuth.getInstance().getCurrentUser().getUid());
                vsfight.setAdapter(fightadapter);
                fightadapter.notifyDataSetChanged();

                if (fightadapter.getItemCount() == 0){
                    vsfight.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                }else {
                    //vsfight.setVisibility(View.VISIBLE);
                    nothing.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void  getFollowers()
    {
        /*FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("High").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelHighs.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelHigh modelStory = ds.getValue(ModelHigh.class);
                    modelHighs.add(modelStory);
                }
                adapterHigh = new AdapterHigh(getContext(), modelHighs);
                storyView.setAdapter(adapterHigh);
                adapterHigh.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Followers");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void  getFollowing(){
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Following");
        reference1.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreData() {
        currentPage++;
        getAllPost();
    }

    private void readStory(){
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("High").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelHighs.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModelHigh modelStory = ds.getValue(ModelHigh.class);
                    modelHighs.add(modelStory);
                }
                adapterHigh = new AdapterHigh(getContext(), modelHighs);
                storyView.setAdapter(adapterHigh);
                adapterHigh.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAllPost() {
        FirebaseDatabase.getInstance().getReference("Posts").limitToFirst(currentPage*TOTAL_ITEM_EACH_LOAD)
                .orderByChild("id").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPosts.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            modelPosts.add(modelPost);

                        }

                        adapterPost = new AdapterPost(getActivity(), modelPosts);
                        post.setAdapter(adapterPost);
                        progressBar.setVisibility(View.GONE);

                        if (adapterPost.getItemCount() == 0){
                            progressBar.setVisibility(View.GONE);
                            post.setVisibility(View.GONE);
                            nothing.setVisibility(View.VISIBLE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                            nothing.setVisibility(View.GONE);
                            if(adapterPost.getItemCount() == initial){
                                load.setVisibility(View.GONE);
                                currentPage--;
                            }else {
                                load.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        //Firebase
        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String mUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String mBio = Objects.requireNonNull(snapshot.child("bio").getValue()).toString();
                String mLocation = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                String mLink = Objects.requireNonNull(snapshot.child("link").getValue()).toString();
                String mVerify = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }

                name.setText(mName);
                username.setText(mUsername);
                location.setText(mLocation);
                bio.setLinkText(mBio);
                link.setLinkText(mLink);
                topName.setText(mUsername);

                if (mVerify.equals("yes")){
                    verify.setVisibility(View.VISIBLE);
                }else {
                    verify.setVisibility(View.GONE);
                }

                if (bio.getText().length()>0){
                    bio.setVisibility(View.VISIBLE);
                }else {
                    bio.setVisibility(View.GONE);
                }

                if (location.getText().length()>0){
                    location_layout.setVisibility(View.VISIBLE);
                }else{
                    location_layout.setVisibility(View.GONE);
                }

                if (link.getText().length()>0){
                    link_layout.setVisibility(View.VISIBLE);
                }else{
                    link_layout.setVisibility(View.GONE);
                }

                bio.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });
                link.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });

                location.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + mLocation));
                    startActivity(i);
                });

                //OnDone
                view.findViewById(R.id.details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.followers).setVisibility(View.VISIBLE);
                view.findViewById(R.id.following).setVisibility(View.VISIBLE);
                view.findViewById(R.id.posts).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

        //Cover
        FirebaseDatabase.getInstance().getReference().child("Cover").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                    String uri = Objects.requireNonNull(snapshot.child("uri").getValue()).toString();

                    if (type.equals("image")){
                        Picasso.get().load(uri).placeholder(R.drawable.cover).into(cover);
                        videoView.setVisibility(View.GONE);
                        cover.setVisibility(View.VISIBLE);
                    }else if (type.equals("video")){
                        videoView.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.parse(uri));
                        videoView.start();
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                            mp.setVolume(0, 0);
                        });

                        videoView.setOnClickListener(v -> {
                            Intent i = new Intent(getContext(), MediaViewActivity.class);
                            i.putExtra("type", "video");
                            i.putExtra("uri", uri);
                            startActivity(i);
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //Firebase
        FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mDp = Objects.requireNonNull(snapshot.child("photo").getValue()).toString();
                String mName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String mUsername = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                String mBio = Objects.requireNonNull(snapshot.child("bio").getValue()).toString();
                String mLocation = Objects.requireNonNull(snapshot.child("location").getValue()).toString();
                String mLink = Objects.requireNonNull(snapshot.child("link").getValue()).toString();
                String mVerify = Objects.requireNonNull(snapshot.child("verified").getValue()).toString();

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }

                name.setText(mName);
                username.setText(mUsername);
                location.setText(mLocation);
                bio.setLinkText(mBio);
                link.setLinkText(mLink);
                topName.setText(mUsername);

                if (mVerify.equals("yes")){
                    verify.setVisibility(View.VISIBLE);
                }else {
                    verify.setVisibility(View.GONE);
                }

                if (bio.getText().length()>0){
                    bio.setVisibility(View.VISIBLE);
                }else {
                    bio.setVisibility(View.GONE);
                }

                if (location.getText().length()>0){
                    location_layout.setVisibility(View.VISIBLE);
                }else{
                    location_layout.setVisibility(View.GONE);
                }

                if (link.getText().length()>0){
                    link_layout.setVisibility(View.VISIBLE);
                }else{
                    link_layout.setVisibility(View.GONE);
                }


                bio.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });
                link.setOnLinkClickListener((i, s) -> {
                    if (i == 1){

                        Intent intent = new Intent(getContext(), SearchActivity.class);
                        intent.putExtra("hashtag", s);
                        startActivity(intent);

                    }else
                    if (i == 2){
                        String username = s.replaceFirst("@","");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                        Query query = ref.orderByChild("username").equalTo(username.trim());
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        String id = Objects.requireNonNull(ds.child("id").getValue()).toString();
                                        if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                            Snackbar.make(view.findViewById(R.id.scroll),"It's you", Snackbar.LENGTH_LONG).show();
                                        }else {
                                            Intent intent = new Intent(getContext(), UserProfileActivity.class);
                                            intent.putExtra("hisUID", id);
                                            startActivity(intent);
                                        }
                                    }
                                }else {
                                    Snackbar.make(view.findViewById(R.id.scroll),"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Snackbar.make(view.findViewById(R.id.scroll),error.getMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                    else if (i == 16){
                        if (!s.startsWith("https://") && !s.startsWith("http://")){
                            s = "http://" + s;
                        }
                        Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                        startActivity(openUrlIntent);
                    }else if (i == 4){
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                        startActivity(intent);
                    }else if (i == 8){
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, s);
                        intent.putExtra(Intent.EXTRA_SUBJECT, "");
                        startActivity(intent);

                    }
                });

                location.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + mLocation));
                    startActivity(i);
                });

                //OnDone
                view.findViewById(R.id.details).setVisibility(View.VISIBLE);
                view.findViewById(R.id.name).setVisibility(View.VISIBLE);
                view.findViewById(R.id.followers).setVisibility(View.VISIBLE);
                view.findViewById(R.id.following).setVisibility(View.VISIBLE);
                view.findViewById(R.id.posts).setVisibility(View.VISIBLE);
                view.findViewById(R.id.progressBar).setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

        //Cover
        FirebaseDatabase.getInstance().getReference().child("Cover").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String type = Objects.requireNonNull(snapshot.child("type").getValue()).toString();
                    String uri = Objects.requireNonNull(snapshot.child("uri").getValue()).toString();

                    if (type.equals("image")){
                        Picasso.get().load(uri).placeholder(R.drawable.cover).into(cover);
                        videoView.setVisibility(View.GONE);
                        cover.setVisibility(View.VISIBLE);
                    }else if (type.equals("video")){
                        videoView.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.parse(uri));
                        videoView.start();
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                            mp.setVolume(0, 0);
                        });

                        videoView.setOnClickListener(v -> {
                            Intent i = new Intent(getContext(), MediaViewActivity.class);
                            i.putExtra("type", "video");
                            i.putExtra("uri", uri);
                            startActivity(i);
                        });

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setDimension() {

        float videoProportion = getVideoProportion();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenProportion = (float) screenHeight / (float) screenWidth;
        android.view.ViewGroup.LayoutParams lp = videoView.getLayoutParams();

        if (videoProportion < screenProportion) {
            lp.height= screenHeight;
            lp.width = (int) ((float) screenHeight / videoProportion);
        } else {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * videoProportion);
        }
        videoView.setLayoutParams(lp);
    }

    private float getVideoProportion(){
        return 1.5f;
    }

}