package com.gaming.community.flexster.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.MainActivity;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.model.SortByrankModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterGroups;
import com.gaming.community.flexster.adapter.AdapterPost;
import com.gaming.community.flexster.adapter.AdapterProduct;
import com.gaming.community.flexster.adapter.AdapterUsers;
import com.gaming.community.flexster.calling.RingingActivity;
import com.gaming.community.flexster.groupVoiceCall.RingingGroupVoiceActivity;
import com.gaming.community.flexster.model.ModelGroups;
import com.gaming.community.flexster.model.ModelPost;
import com.gaming.community.flexster.model.ModelProduct;
import com.gaming.community.flexster.model.ModelUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    //User
    AdapterUsers adapterUsers;
    List<ModelUser> userList;
    RecyclerView users_rv;

    //Group
    AdapterGroups adapterGroups;
    List<ModelGroups> modelGroups;
    RecyclerView groups;

    //Market
    RecyclerView productList;
    AdapterProduct adapterProduct;
    List<ModelProduct> modelProducts;

    //Post
    AdapterPost adapterPost;
    List<ModelPost> modelPosts;
    RecyclerView post;

    //Other
    private static final int TOTAL_ITEM_EACH_LOAD = 8;
    private int currentPage = 1;
    Button more;
    long initial;
    String type = "user";

    ArrayList<SortByrankModel> sortmodel=new ArrayList<>() ;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Back
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());
        more = findViewById(R.id.more);

        //User
        users_rv = findViewById(R.id.users);
        users_rv.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        userList = new ArrayList<>();
        //getAllUsers();

        //Post
        post = findViewById(R.id.post);
        post.setLayoutManager(new LinearLayoutManager(SearchActivity.this , LinearLayoutManager.VERTICAL , true));
        modelPosts = new ArrayList<>();

        //Groups
        groups = findViewById(R.id.groups);
        groups.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        modelGroups = new ArrayList<>();


        //Market
        productList = findViewById(R.id.products);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        productList.setLayoutManager(gridLayoutManager);
        modelProducts = new ArrayList<>();
        getAllProducts();

        //EdiText
        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                switch (type) {
                    case "user":
                        filterUser(editText.getText().toString());
                        break;
                    case "post":
                        filterPost(editText.getText().toString());
                        break;
                    case "group":
                        filterGroup(editText.getText().toString());
                        break;
                    case "product":
                        filterProduct(editText.getText().toString());
                        break;
                }
                return true;
            }
            return false;
        });


        //TabLayout
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tabLayout.getSelectedTabPosition() == 0) {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    users_rv.setVisibility(View.GONE);
                    groups.setVisibility(View.GONE);
                    productList.setVisibility(View.VISIBLE);
                    post.setVisibility(View.GONE);
                    more.setVisibility(View.GONE);
                    type = "product";
                    getAllProducts();
                }
                else if (tabLayout.getSelectedTabPosition() == 1) {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

                    users_rv.setVisibility(View.GONE);
                    groups.setVisibility(View.VISIBLE);
                    productList.setVisibility(View.GONE);
                    post.setVisibility(View.GONE);
                    more.setVisibility(View.GONE);
                    type = "group";
                    getAllGroup();

                }
                else if (tabLayout.getSelectedTabPosition() == 2) {
                    topRank();
                    //getAllUsers();
                    type = "user";
                    users_rv.setVisibility(View.VISIBLE);
                    post.setVisibility(View.GONE);
                    groups.setVisibility(View.GONE);
                    productList.setVisibility(View.GONE);
                    more.setVisibility(View.GONE);
                }
                else if (tabLayout.getSelectedTabPosition() == 3) {
                    loadusers();
                    //getAllUsers();
                    type = "user";
                    users_rv.setVisibility(View.VISIBLE);
                    post.setVisibility(View.GONE);
                    groups.setVisibility(View.GONE);
                    productList.setVisibility(View.GONE);
                    more.setVisibility(View.GONE);
                }
                /*else if (tabLayout.getSelectedTabPosition() == 1) {
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    users_rv.setVisibility(View.GONE);
                    groups.setVisibility(View.GONE);
                    post.setVisibility(View.VISIBLE);
                    productList.setVisibility(View.GONE);
                    type = "post";
                    more.setVisibility(View.VISIBLE);
                    getAllPost();
                }*/
                /*else if (tabLayout.getSelectedTabPosition() == 4) {
                    startActivity(new Intent(SearchActivity.this, LocationActivity.class));
                }*/
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    i++;
                }
                initial = i;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.more).setOnClickListener(v -> loadMoreData());

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
                            Intent intent = new Intent(SearchActivity.this, RingingActivity.class);
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

        //Tag
        if (getIntent().hasExtra("hashtag")){
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            users_rv.setVisibility(View.GONE);
            groups.setVisibility(View.GONE);
            post.setVisibility(View.VISIBLE);
            productList.setVisibility(View.GONE);
            type = "post";
            Objects.requireNonNull(tabLayout.getTabAt(1)).select();
            more.setVisibility(View.GONE);
            filterPost(getIntent().getStringExtra("hashtag"));
            editText.setText(getIntent().getStringExtra("hashtag"));
        }

    }

    private void loadusers() {
        sortmodel.clear();

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int dssize=(int) dataSnapshot.getChildrenCount();

                 for (DataSnapshot ds: dataSnapshot.getChildren()){
                     SortByrankModel md=new SortByrankModel();
                     String id=ds.child("id").getValue(String.class);
                     md.setUser_id(id);

                     FirebaseDatabase.getInstance().getReference("Users").child(id).child("FightWin")
                             .addListenerForSingleValueEvent(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(DataSnapshot dataSnapshot) {
                                     // get total available quest
                                     int size = (int) dataSnapshot.getChildrenCount();

                                     if(size >1)
                                     {
                                         int result= size;
                                         String level = String.valueOf(result+1);
                                         md.setRank(level);
                                         sortmodel.add(md);
                                         setsorting(dssize);
                                     }
                                     else
                                     {
                                         String level ="1";
                                         md.setRank(level);
                                         sortmodel.add(md);
                                         setsorting(dssize);
                                     }

                                 }
                                 @Override
                                 public void onCancelled(DatabaseError databaseError) {
                                     md.setRank("1");
                                     sortmodel.add(md);
                                     setsorting(dssize);
                                 }
                             });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void topRank(){

        sortmodel.clear();

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int dssize=(int) snapshot.getChildrenCount();

                for (DataSnapshot ds: snapshot.getChildren()){
                    SortByrankModel md=new SortByrankModel();
                    String id=ds.child("id").getValue(String.class);
                    md.setUser_id(id);

                    String[] level = {"1"};
                    int[] postsize = {0};
                    final int[] postlevel = {0};
                    final int[] commentlevel = {0};
                    int[] commentsize = {0};
                    FirebaseDatabase.getInstance().getReference("Users").child(id).child("PostCount")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // get total available quest
                                    postsize[0] = (int) dataSnapshot.getChildrenCount();

                                    FirebaseDatabase.getInstance().getReference("Users").child(id).child("CommentCount")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // get total available quest
                                                    commentsize[0] = (int) dataSnapshot.getChildrenCount();
                                                    if(postsize[0]>=1 && commentsize[0]>=10)
                                                    {
                                                        postlevel[0] =postsize[0];
                                                        commentlevel[0] =commentsize[0]/10;
                                                        level[0]= String.valueOf(postlevel[0] + commentlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);

                                                    }
                                                    else if(postsize[0]>=1)
                                                    {
                                                        postlevel[0] =postsize[0];
                                                        level[0]= String.valueOf(postlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                    else if(commentsize[0]>=10)
                                                    {
                                                        commentlevel[0] =commentsize[0]/10;
                                                        level[0]= String.valueOf(commentlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                    else
                                                    {
                                                        level[0] ="1";
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    FirebaseDatabase.getInstance().getReference("Users").child(id).child("CommentCount")
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    // get total available quest
                                                    commentsize[0] = (int) dataSnapshot.getChildrenCount();
                                                    if(postsize[0]>=1 && commentsize[0]>=10)
                                                    {
                                                        postlevel[0] =postsize[0];
                                                        commentlevel[0] =commentsize[0]/10;
                                                        level[0]= String.valueOf(postlevel[0] + commentlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);

                                                    }
                                                    else if(postsize[0]>=1)
                                                    {
                                                        postlevel[0] =postsize[0];
                                                        level[0]= String.valueOf(postlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                    else if(commentsize[0]>=10)
                                                    {
                                                        commentlevel[0] =commentsize[0]/10;
                                                        level[0]= String.valueOf(commentlevel[0] +1);
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                    else
                                                    {
                                                        level[0] ="1";
                                                        md.setRank(level[0]);
                                                        sortmodel.add(md);
                                                        setsorting(dssize);
                                                    }
                                                }
                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                }
                            });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setsorting(int size) {

        if(sortmodel.size()==size)
        {
            Collections.sort(sortmodel, new SortByrankModel.RatingComparator());
            Comparator<SortByrankModel> cmp = Collections.reverseOrder(new SortByrankModel.RatingComparator());
            Collections.sort(sortmodel, cmp);

            new Handler().postDelayed(() -> {
                userList.clear();
                for(int a=0; a< sortmodel.size();a++)
                {
                    //Log.e("dataisssssssda",sortmodel.get(a).getUser_id()+" \n "+sortmodel.get(a).getRank());
                    getSkilluser(sortmodel.get(a).getUser_id());
                }
            },1000);

        }
    }

    private void filterPost(String query) {
        FirebaseDatabase.getInstance().getReference("Posts").orderByChild("counter").limitToFirst(currentPage*TOTAL_ITEM_EACH_LOAD)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPosts.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            if (Objects.requireNonNull(modelPost).getText().toLowerCase().contains(query.toLowerCase()) ||
                                    modelPost.getType().toLowerCase().contains(query.toLowerCase())){
                                modelPosts.add(modelPost);
                            }
//                            Collections.reverse(modelPosts);
                            adapterPost = new AdapterPost(SearchActivity.this, modelPosts);
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
                                more.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filterProduct(String query) {
        FirebaseDatabase.getInstance().getReference("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelProducts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelUser = ds.getValue(ModelProduct.class);
                    if (Objects.requireNonNull(modelUser).getTitle().toLowerCase().contains(query.toLowerCase())
                            || modelUser.getCat().toLowerCase().contains(query.toLowerCase())
                            || modelUser.getType().toLowerCase().contains(query.toLowerCase())
                            || modelUser.getDes().toLowerCase().contains(query.toLowerCase())){
                        long lastTime = Long.parseLong(modelUser.getpId());
                       String time = GetTimeAgo.getTimeAgo(lastTime);
                       String t= time.substring(time.length()-8, time.length());

                       if(t.equals("days ago"))
                       {

                       }
                       else if(time.equals("yesterday"))
                       {

                       }
                       else
                       {
                           modelProducts.add(modelUser);
                           findViewById(R.id.progressBar).setVisibility(View.GONE);
                       }

                    }
                    //Collections.reverse(modelProducts);
                    adapterProduct = new AdapterProduct(SearchActivity.this, modelProducts);
                    productList.setAdapter(adapterProduct);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterProduct.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterGroup(String query) {
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroups.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if(ds.child("status").getValue(String.class).equals("1"))
                    {
                        ModelGroups modelUser = ds.getValue(ModelGroups.class);

                        if (Objects.requireNonNull(modelUser).getgName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getgUsername().toLowerCase().contains(query.toLowerCase())){
                            modelGroups.add(modelUser);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
//                    Collections.reverse(modelGroups);
                        adapterGroups = new AdapterGroups(SearchActivity.this, modelGroups);
                        groups.setAdapter(adapterGroups);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if (adapterUsers.getItemCount() == 0){
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            findViewById(R.id.groups).setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            findViewById(R.id.groups).setVisibility(View.VISIBLE);
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

    private void loadMoreData() {
        currentPage++;
        getAllPost();
    }

    private void getAllPost() {
        //.limitToFirst(currentPage*TOTAL_ITEM_EACH_LOAD)
        FirebaseDatabase.getInstance().getReference("Posts")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelPosts.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelPost modelPost = ds.getValue(ModelPost.class);
                            modelPosts.add(modelPost);
                            //Collections.reverse(modelPosts);
                            adapterPost = new AdapterPost(SearchActivity.this, modelPosts);
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
                                if(adapterPost.getItemCount() == initial){
                                    more.setVisibility(View.GONE);
                                    currentPage--;
                                }else {
                                    more.setVisibility(View.VISIBLE);
                                }
                            }
                        }
//                        Collections.reverse(modelPosts);
                        adapterPost.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getAllProducts() {
        FirebaseDatabase.getInstance().getReference("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelProducts.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelProduct modelUser = ds.getValue(ModelProduct.class);

                    long lastTime = Long.parseLong(modelUser.getpId());
                    String time = GetTimeAgo.getTimeAgo(lastTime);
                    String t= time.substring(time.length()-8, time.length());

                    if(t.equals("days ago"))
                    {

                    }
                    else if(time.equals("yesterday"))
                    {

                    }
                    else
                    {
                        modelProducts.add(modelUser);
                    }

                    //modelProducts.add(modelUser);
                    Collections.reverse(modelProducts);
                    adapterProduct = new AdapterProduct(SearchActivity.this, modelProducts);
                    productList.setAdapter(adapterProduct);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterProduct.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.products).setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
                //reverse_layout();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*private void reverse_layout () {
        Collections.reverse(modelProducts);
        adapterProduct.notifyDataSetChanged();
    }*/

    private void getAllGroup() {
        FirebaseDatabase.getInstance().getReference("Groups").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroups.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    if(ds.child("status").getValue(String.class).equals("1"))
                    {

                        ModelGroups modelUser = ds.getValue(ModelGroups.class);
                        modelGroups.add(modelUser);
                        //Collections.reverse(modelGroups);
                        adapterGroups = new AdapterGroups(SearchActivity.this, modelGroups);
                        groups.setAdapter(adapterGroups);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);

                    }

                }

                if (modelGroups.size() == 0){
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.groups).setVisibility(View.GONE);
                    findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.groups).setVisibility(View.VISIBLE);
                    findViewById(R.id.nothing).setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getSkilluser(String user_id){
        FirebaseDatabase.getInstance().getReference("Users").child(user_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser = new ModelUser();
                modelUser.setId(Objects.requireNonNull(snapshot.child("id").getValue()).toString());
                modelUser.setName(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                modelUser.setUsername(Objects.requireNonNull(snapshot.child("username").getValue()).toString());
                modelUser.setLocation(Objects.requireNonNull(snapshot.child("location").getValue()).toString());
                modelUser.setPhoto(Objects.requireNonNull(snapshot.child("photo").getValue()).toString());
                modelUser.setVerified(Objects.requireNonNull(snapshot.child("verified").getValue()).toString());
                modelUser.setTypingTo(Objects.requireNonNull(snapshot.child("typingTo").getValue()).toString());
                userList.add(modelUser);

                adapterUsers = new AdapterUsers(SearchActivity.this, userList);
                users_rv.setAdapter(adapterUsers);
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if (adapterUsers.getItemCount() == 0){
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.users).setVisibility(View.GONE);
                    findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    findViewById(R.id.users).setVisibility(View.VISIBLE);
                    findViewById(R.id.nothing).setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getAllUsers() {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        userList.add(modelUser);
                    }

                    adapterUsers = new AdapterUsers(SearchActivity.this, userList);
                    users_rv.setAdapter(adapterUsers);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);

                    if (adapterUsers.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.users).setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.users).setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void filterUser(String query) {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser = ds.getValue(ModelUser.class);
                    if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(Objects.requireNonNull(modelUser).getId())){
                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                            userList.add(modelUser);
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    }
//                    Collections.reverse(userList);
                    adapterUsers = new AdapterUsers(SearchActivity.this, userList);
                    users_rv.setAdapter(adapterUsers);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterUsers.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.users).setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        findViewById(R.id.users).setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
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
        Intent intent = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}