package com.gaming.community.flexster.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterPost;
import com.gaming.community.flexster.model.ModelGameList;
import com.gaming.community.flexster.model.ModelPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrendingActivity extends AppCompatActivity {

    private static final int TOTAL_ITEM_EACH_LOAD = 12;
    //    String type = "";
    String selected = "All";
    Boolean isScrolling = false ;
    int currentItems, totalItems, scrollOutItems;
    //Post
    AdapterPost adapterPost;
    List<ModelPost> modelPosts;

    //Post
//    AdapterPost getAdapterPost;
//    List<ModelPost> modelPostList;
    //RecyclerView postView;
    RecyclerView post;
    Button more;
    //    long initial = 0;
    LinearLayout gamelist;
    DatabaseReference databaseReference;
    ArrayList<ModelGameList> modelGameLists = new ArrayList<>();
    int total_trnding = 0;
    NightMode sharedPref;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending);
        gamelist = findViewById(R.id.gamelist);

        //Back
//        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        loadtotalviews();
        //Search
        //findViewById(R.id.search).setOnClickListener(v1 -> startActivity(new Intent(TrendingActivity.this, SearchActivity.class)));

        more = findViewById(R.id.more);
        getgamelist();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){
//                        for (DataSnapshot dataSnapshot1 : ds.child("Voice").getChildren()){
//                            if (Objects.requireNonNull(dataSnapshot1.child("type").getValue()).toString().equals("calling")){
//
//                                if (!Objects.requireNonNull(dataSnapshot1.child("from").getValue()).toString().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                    if (!dataSnapshot1.child("end").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                        if (!dataSnapshot1.child("ans").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())){
//                                            Intent intent = new Intent(getApplicationContext(), RingingGroupVoiceActivity.class);
//                                            intent.putExtra("room", Objects.requireNonNull(dataSnapshot1.child("room").getValue()).toString());
//                                            intent.putExtra("group", Objects.requireNonNull(ds.child("groupId").getValue()).toString());
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //Call
//        Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("to").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    for (DataSnapshot ds : snapshot.getChildren()){
//                        if (Objects.requireNonNull(ds.child("type").getValue()).toString().equals("calling")){
//                            Intent intent = new Intent(TrendingActivity.this, RingingActivity.class);
//                            intent.putExtra("room", Objects.requireNonNull(ds.child("room").getValue()).toString());
//                            intent.putExtra("from", Objects.requireNonNull(ds.child("from").getValue()).toString());
//                            intent.putExtra("call", Objects.requireNonNull(ds.child("call").getValue()).toString());
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
//        reference.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int i = 0;
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    i++;
//                }
//                initial = i;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        //Post
        post = findViewById(R.id.post);
//        post.setItemViewCacheSize(20);
//        post.setDrawingCacheEnabled(true);
//        post.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);

        LinearLayoutManager manager = new LinearLayoutManager(this , LinearLayoutManager.VERTICAL , false);

        post.setLayoutManager(manager);
        modelPosts = new ArrayList<>();
        adapterPost = new AdapterPost(TrendingActivity.this, modelPosts);
        //adapterPost.setHasStableIds(true);

        post.setAdapter(adapterPost);
        post.hasFixedSize();
        trending();
//        findViewById(R.id.more).setOnClickListener(v -> loadMoreData());


        findViewById(R.id.all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage = 1;
                getrelatedpost("all");
            }
        });
//        post.setNestedScrollingEnabled(false);
//        NestedScrollView nested = findViewById(R.id.nested);
//        nested.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                View view = (View) nested.getChildAt(nested.getChildCount() - 1);
//
//                int diff = (view.getBottom() - (nested.getHeight() + nested.getScrollY()));
//                if (diff < 3) {
//                    if (modelPosts.size() != total_trnding) {
//                        loadMoreData();
//                    }
//                }
//            }
//        });


        post.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    loadMoreData();
                }

            }
        });

    }

    private void loadtotalviews() {
        FirebaseDatabase.getInstance().getReference("Post").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total_trnding = (int) snapshot.getChildrenCount();
                Log.d("hhhhhhhhhhhhhhh", String.valueOf(total_trnding));
                trending();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getgamelist() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("games");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = "";

                for (DataSnapshot np : dataSnapshot.getChildren()) {

                    name = np.child("name").getValue(String.class);

                    if (name != null) {
                        ModelGameList md = new ModelGameList();
                        md.setName(name);
                        modelGameLists.add(md);

                    }
                }

                addnewviews();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TrendingActivity.this, "Couldn't Load Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addnewviews() {

        for (int a = 0; a < modelGameLists.size(); a++) {
            final View row = getLayoutInflater().inflate(R.layout.design_game_list_tab, null, false);
            TextView tv = row.findViewById(R.id.all);
            tv.setText(modelGameLists.get(a).getName());
            int finalA = a;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getrelatedpost(modelGameLists.get(finalA).getName());
                }
            });
            gamelist.addView(row);
        }

    }

    private void getrelatedpost(String name) {
        if (name.equals("all")) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            selected = "All";

            Log.e("methodcalled", "all" + currentPage * TOTAL_ITEM_EACH_LOAD);
            FirebaseDatabase.getInstance().getReference("Post").limitToLast(currentPage * TOTAL_ITEM_EACH_LOAD)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            modelPosts.clear();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                ModelPost modelPost = ds.getValue(ModelPost.class);
                                modelPosts.add(modelPost);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                                if (adapterPost.getItemCount() == 0) {
                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    post.setVisibility(View.GONE);
                                    findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                                } else {
                                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                                    post.setVisibility(View.VISIBLE);
                                    findViewById(R.id.nothing).setVisibility(View.GONE);
//                                    if(adapterPost.getItemCount() == initial){
//                                        more.setVisibility(View.GONE);
//                                        currentPage--;
//                                    }else {
//                                        more.setVisibility(View.GONE);
//                                    }
                                }
                            }
//                            Collections.reverse(modelPosts);
                            adapterPost.notifyDataSetChanged();
                            post.scrollToPosition(((currentPage - 1) * 8) - 1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            selected = "game";
            findViewById(R.id.more).setVisibility(View.GONE);
            //post.removeAllViews();

            FirebaseDatabase.getInstance().getReference("postExtra")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            modelPosts.clear();
                            findViewById(R.id.more).setVisibility(View.GONE);
                            adapterPost.notifyDataSetChanged();
                            ArrayList<String> idarray = new ArrayList<>();
                            for (DataSnapshot ds : snapshot.getChildren()) {
                            /*ModelPost modelPost = ds.getValue(ModelPost.class);
                            modelPosts.add(modelPost);*/
                                if (ds.child("game").exists()) {
                                    String game = ds.child("game").getValue(String.class);
                                    String postid = ds.getKey();
                                    Log.e("calleddsds", postid);
                                    if (game.equals(name)) {
                                        idarray.add(postid);
                                    }
                                } else {
                                    Log.e("calleddsds", "inside_else");
                                }
                            }
//                            Collections.reverse(modelPosts);
                            fetchdatabyid(idarray);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }

    private void fetchdatabyid(ArrayList<String> idarray) {
        Log.e("calleddsdssizesis", String.valueOf(idarray.size()));


        for (int a = 0; a < idarray.size(); a++) {

            FirebaseDatabase.getInstance().getReference().child("Post").child(idarray.get(a))
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            modelPosts.clear();
                            ModelPost modelPost = snapshot.getValue(ModelPost.class);
                            modelPosts.add(modelPost);
                            Log.e("calleddsds", "casldas" + modelPosts.size());

                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            findViewById(R.id.more).setVisibility(View.GONE);
                            if (modelPosts.size() == 0) {
                                Log.e("modelsizeissszero", String.valueOf(modelPosts.size()));
                                post.setVisibility(View.GONE);
                                findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                            } else {
                                Log.e("modelsizeissszeroelse", String.valueOf(modelPosts.size()));
                                post.setVisibility(View.VISIBLE);
                                adapterPost = new AdapterPost(TrendingActivity.this, modelPosts);
                                post.setAdapter(adapterPost);
                                adapterPost.notifyDataSetChanged();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("erroererer", String.valueOf(error));
                        }
                    });
        }

    }

    private void loadMoreData() {
        total_trnding -= 10;
        trending();
    }

    private void trending() {
//        Log.e("currentisssss",currentPage*TOTAL_ITEM_EACH_LOAD+"");
        if( total_trnding >= 10 )
        FirebaseDatabase.getInstance().getReference("Post").orderByKey().startAt(String.valueOf(total_trnding-10)).endAt(String.valueOf(total_trnding))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int a=0;
//                        int b=modelPosts.size();
                        List<ModelPost> newdata=new ArrayList<>();
                        newdata.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ModelPost modelPost = ds.getValue(ModelPost.class);
//                            modelPosts.add(modelPost);
                            newdata.add(modelPost);
//                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }

                        Collections.reverse(newdata);
                        modelPosts.addAll(newdata);
                        adapterPost.notifyDataSetChanged();
//                        for(int c=0;c<newdata.size();c++)
//                        {
//                            if(c>=modelPosts.size())
//                            {
//                                ModelPost mds=newdata.get(c);
//                                modelPosts.add(mds);
//                                adapterPost.notifyDataSetChanged();
//                            }
//                        }
//                        NestedScrollView nested=findViewById(R.id.nested);
                        if (adapterPost.getItemCount() == 0) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            post.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        } else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            post.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
//                            if(adapterPost.getItemCount() == initial){
//                                more.setVisibility(View.GONE);
//                                currentPage--;
//                            }else {
//                                more.setVisibility(View.GONE);
//                            }
                        }

                        //post.scrollToPosition(((currentPage-1) * 8) - 1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

//    @Override
//    public void onBackPressed() {
//        FirebaseDatabase.getInstance("Posts").setPersistenceEnabled(true);
//        startActivity(new Intent(TrendingActivity.this, MainActivity.class));
//        finish();
//    }

}