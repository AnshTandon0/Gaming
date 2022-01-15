package com.gaming.community.flexster.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaming.community.flexster.OnSelectedItemListener;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterApproval;
import com.gaming.community.flexster.adapter.AdapterApprovalChat;
import com.gaming.community.flexster.adapter.AdapterApprovalClubFight;
import com.gaming.community.flexster.adapter.AdapterDialogGameList;
import com.gaming.community.flexster.adapter.AdapterGroups;
import com.gaming.community.flexster.group.GroupFragment;
import com.gaming.community.flexster.group.PostClubFightLogActivity;
import com.gaming.community.flexster.model.ModelGameList;
import com.gaming.community.flexster.model.ModelGroupVsFight;
import com.gaming.community.flexster.model.ModelGroups;
import com.gaming.community.flexster.model.ModelVsFight;
import com.gaming.community.flexster.send.SendToUserActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Objects;

public class PostFightLogActivity extends AppCompatActivity {

    //Permission
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    ArrayList<ModelGameList> roundslist;
    ArrayList<ModelGameList> wonlist;
    ImageView imageView;
    ArrayList<ModelGameList> vslist;
    ArrayList<ModelGameList> myGrouplist;
    ArrayList<String> myGroupIdlist;
    ArrayList<ModelGameList> opponentGrouplist;
    ArrayList<String> opponentGroupIdlist;
    //RoundedImageView roundedImageView;
    TextView select_game,category,textView2;
    EditText edt_content,edt_total_rounds,edt_won_rounds;
    Button post;
    LinearLayout vsll;
    String his_id="";
    String content = "";

    String me = "";
    String his = "";

    String selected_game="";
    private RecyclerView.LayoutManager mlayoutManager;
    AdapterDialogGameList adapterDialogGameList;

    LinearLayout ll_select_my_group,ll_select_opponent_group;
    TextView select_my_group,select_opponent_group;

    String mygroupID = "",opponentgroupID ="";

    LinearLayout ll_save_you_win;
    TabLayout tabLayout;

    RecyclerView rec_fight_log;
    ArrayList<ModelVsFight> modelVsFights = new ArrayList<>();
    AdapterApprovalChat adapterApprovalChat;

    RecyclerView rec_fight_club_log;
    ArrayList<ModelGroupVsFight> modelGroupVsFightArrayList = new ArrayList<>();
    AdapterApprovalClubFight adapterApprovalClubFight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_fight_log);

        roundslist=Constantdatas.getroundlist();
        wonlist=Constantdatas.getroundlist();
        vslist=Constantdatas.getvsdatas();
        vsll=findViewById(R.id.vsll);

        imageView = findViewById(R.id.imageView);
        //roundedImageView = findViewById(R.id.cover);
        select_game = findViewById(R.id.select_game);
        category = findViewById(R.id.category);
        edt_total_rounds = findViewById(R.id.edt_total_rounds);
        edt_content = findViewById(R.id.edt_content);
        textView2 = findViewById(R.id.textView2);

        edt_won_rounds = findViewById(R.id.edt_won_rounds);
        post = findViewById(R.id.post);

        ll_select_my_group = findViewById(R.id.ll_select_my_group);
        ll_select_opponent_group = findViewById(R.id.ll_select_opponent_group);
        select_my_group = findViewById(R.id.select_my_group);
        select_opponent_group = findViewById(R.id.select_opponent_group);

        ll_save_you_win = findViewById(R.id.ll_save_you_win);
        tabLayout = findViewById(R.id.tabLayout);
        rec_fight_log = findViewById(R.id.rec_fight_log);
        rec_fight_club_log = findViewById(R.id.rec_fight_club_log);

        Intent i=getIntent();
        his_id=i.getStringExtra("oponentidis");
        getMygrouplist();
        getOpponentgrouplist();
        getFightLog();
        getClubFightLog();

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                me =snapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(his_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                his =snapshot.child("name").getValue().toString();

                textView2.setText(me + " vs " + his);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        category.setText(vslist.get(0).getName());

        vsll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);
                LinearLayout searchll=dialog.findViewById(R.id.searchll);
                searchll.setVisibility(View.GONE);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {
                        category.setText(gamename);
                        if (position == 1){
                            ll_select_my_group.setVisibility(View.VISIBLE);
                            ll_select_opponent_group.setVisibility(View.VISIBLE);
                        }
                        else {
                            ll_select_my_group.setVisibility(View.GONE);
                            ll_select_opponent_group.setVisibility(View.GONE);
                        }
                        dialog.dismiss();
                    }
                };

                adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,vslist,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterDialogGameList);

            }
        });

        select_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                EditText editText = dialog.findViewById(R.id.editText);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {
                        select_game.setText(gamename);
                        select_game.setTextColor(Color.parseColor("#4e4f54"));
                        selected_game = select_game.getText().toString();
                        dialog.dismiss();
                    }
                };

                DatabaseReference databaseReference;
                ArrayList<ModelGameList> modelGameLists = new ArrayList<>();

                databaseReference= FirebaseDatabase.getInstance().getReference().child("games");

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name="";

                        for (DataSnapshot np : dataSnapshot.getChildren()) {

                            name=np.child("name").getValue(String.class);

                            if(name!=null) {
                                ModelGameList md=new ModelGameList();
                                md.setName(name);
                                modelGameLists.add(md);
                            }
                        }

                        adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,modelGameLists,gamenameItemListener);
                        mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                        rec_game_list.setLayoutManager(mlayoutManager);
                        rec_game_list.setItemAnimator(new DefaultItemAnimator());
                        rec_game_list.setAdapter(adapterDialogGameList);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(PostFightLogActivity.this, "Couldn't Load Data", Toast.LENGTH_SHORT).show();
                    }
                });

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // filter recycler view when query submitted
                        adapterDialogGameList.getFilter().filter(s);
                    }
                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });

            }
        });

        /*select_total_rounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);
                LinearLayout searchll=dialog.findViewById(R.id.searchll);
                searchll.setVisibility(View.GONE);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {

                        select_total_rounds.setText(gamename);
                        dialog.dismiss();
                    }
                };

                adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,roundslist,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterDialogGameList);
            }

        });*/

        /*select_you_won.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);
                LinearLayout searchll=dialog.findViewById(R.id.searchll);
                searchll.setVisibility(View.GONE);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {

                        boolean selection=checkselection(gamename);
                        if(selection)
                        {
                            select_you_won.setText(gamename);
                        }
                        else
                        {
                            Toast.makeText(PostFightLogActivity.this, "Total round is lower than won", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                };

                adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,wonlist,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterDialogGameList);

            }
        });*/

        select_my_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);
                LinearLayout searchll=dialog.findViewById(R.id.searchll);
                searchll.setVisibility(View.GONE);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {
                        select_my_group.setText(gamename);
                        select_my_group.setTextColor(Color.parseColor("#4e4f54"));
                        mygroupID = myGroupIdlist.get(position);
                        dialog.dismiss();
                    }
                };

                adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,myGrouplist,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterDialogGameList);

            }
        });

        select_opponent_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(PostFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.show();

                ImageView back = dialog.findViewById(R.id.back);
                RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
                CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
                card_new_game_add.setVisibility(View.GONE);
                LinearLayout searchll=dialog.findViewById(R.id.searchll);
                searchll.setVisibility(View.GONE);

                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                    @Override
                    public void setOnClick(String gamename, int position) {
                        select_opponent_group.setText(gamename);
                        select_opponent_group.setTextColor(Color.parseColor("#4e4f54"));
                        opponentgroupID = opponentGroupIdlist.get(position);
                        dialog.dismiss();
                    }
                };

                adapterDialogGameList = new AdapterDialogGameList(PostFightLogActivity.this,opponentGrouplist,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterDialogGameList);

            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_content.getText().toString().isEmpty()){
                    content = "";
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    compressImage();
                }
                else {
                    content = edt_content.getText().toString().trim();
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    compressImage();
                }

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {

                    rec_fight_log.setVisibility(View.GONE);
                    rec_fight_club_log.setVisibility(View.GONE);
                    ll_save_you_win.setVisibility(View.VISIBLE);

                }
                else if (tabLayout.getSelectedTabPosition() == 1) {

                    ll_save_you_win.setVisibility(View.GONE);
                    rec_fight_club_log.setVisibility(View.GONE);
                    rec_fight_log.setVisibility(View.VISIBLE);

                }
                else if (tabLayout.getSelectedTabPosition() == 2) {

                    ll_save_you_win.setVisibility(View.GONE);
                    rec_fight_log.setVisibility(View.GONE);
                    rec_fight_club_log.setVisibility(View.VISIBLE);

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

    /*private boolean checkselection(String gamename) {
        boolean select=false;
        if(select_total_rounds.getText().toString().equals("total rounds"))
        {
            select=false;
        }
        else if(gamename.equals("you won"))
        {
            select=false;
        }
        else
        {
            int total= Integer.parseInt(select_total_rounds.getText().toString());
            int won=Integer.parseInt(gamename);
            if(won<=total)
            {
                select=true;
            }
            else
            {
                select=false;
            }
        }
        return select;
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(R.id.main), "Storage permission allowed", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(R.id.main), "Storage permission is required", Snackbar.LENGTH_LONG).show();
            }
        }
    }


    private void compressImage(){

        if(edt_total_rounds.getText().toString().isEmpty())
        {
            Toast.makeText(PostFightLogActivity.this, "Enter total rounds", Toast.LENGTH_SHORT).show();
        }
        else if(edt_won_rounds.getText().toString().isEmpty())
        {
            Toast.makeText(PostFightLogActivity.this, "Enter won round", Toast.LENGTH_SHORT).show();
        }
        else if(select_game.getText().toString().equals("Game name"))
        {
            Toast.makeText(PostFightLogActivity.this, "select game first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            int total= Integer.parseInt(edt_total_rounds.getText().toString());
            int won=Integer.parseInt(edt_won_rounds.getText().toString());
            if(won<=total)
            {
                int total_rounds= Integer.parseInt(edt_total_rounds.getText().toString());
                int won_round=Integer.parseInt(edt_won_rounds.getText().toString());

                if (category.getText().toString().equals("Club vs Club")){

                    if (mygroupID.equals("")){

                        Toast.makeText(PostFightLogActivity.this, "Select your clan", Toast.LENGTH_SHORT).show();

                    }
                    else if (opponentgroupID.equals("")){

                        Toast.makeText(PostFightLogActivity.this, "Select opponent clan", Toast.LENGTH_SHORT).show();

                    }
                    else {

                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        HashMap<Object, String> hashMap = new HashMap<>();
                        hashMap.put("pId", timeStamp);
                        hashMap.put("game_name",select_game.getText().toString().trim());
                        hashMap.put("category",category.getText().toString().trim());
                        hashMap.put("total_rounds",edt_total_rounds.getText().toString().trim());
                        hashMap.put("group1_won",edt_won_rounds.getText().toString().trim());
                        hashMap.put("group1_id",mygroupID);
                        hashMap.put("group1_name",select_my_group.getText().toString().trim());
                        hashMap.put("group2_won", String.valueOf(total_rounds-won_round));
                        hashMap.put("group2_id",opponentgroupID);
                        hashMap.put("group2_name",select_opponent_group.getText().toString().trim());
                        hashMap.put("creatore_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("photo", "");
                        hashMap.put("content",content);
                        hashMap.put("Status", "unapproved");
                        hashMap.put("chk_main_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                                .child(mygroupID)
                                .child(timeStamp).setValue(hashMap);

                        HashMap<Object, String> hashMap1 = new HashMap<>();
                        hashMap1.put("pId", timeStamp);
                        hashMap1.put("game_name",select_game.getText().toString().trim());
                        hashMap1.put("category",category.getText().toString().trim());
                        hashMap1.put("total_rounds",edt_total_rounds.getText().toString().trim());
                        hashMap1.put("group1_won",String.valueOf(total_rounds-won_round));
                        hashMap1.put("group1_id",opponentgroupID);
                        hashMap1.put("group1_name",select_opponent_group.getText().toString().trim());
                        hashMap1.put("group2_won",edt_won_rounds.getText().toString().trim());
                        hashMap1.put("group2_id",mygroupID);
                        hashMap1.put("group2_name",select_my_group.getText().toString().trim());
                        hashMap1.put("creatore_id",his_id);
                        hashMap1.put("photo", "");
                        hashMap1.put("content",content);
                        hashMap1.put("Status", "unapproved");
                        hashMap1.put("chk_main_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        FirebaseDatabase.getInstance().getReference().child("GroupFightLog")
                                .child(opponentgroupID)
                                .child(timeStamp).setValue(hashMap1);

                        //increaseGroupPostCount && increaseGroupUserPostCount
                        PostCount.increaseGroupPost(mygroupID,timeStamp,FirebaseAuth.getInstance().getCurrentUser().getUid());

                        select_game.setText("Game name");
                        category.setText("1 vs 1");
                        edt_total_rounds.setText("");
                        edt_won_rounds.setText("");
                        edt_content.setText("");
                        select_my_group.setText("select my group");
                        select_opponent_group.setText("select opponent group");
                        mygroupID = "";
                        opponentgroupID = "";
                        ll_select_my_group.setVisibility(View.GONE);
                        ll_select_opponent_group.setVisibility(View.GONE);
                        post.setVisibility(View.VISIBLE);

                        //Snackbar.make(findViewById(R.id.main), "Fight Posted", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(PostFightLogActivity.this, "Your fight is saved!", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);

                        tabLayout.getTabAt(2).select();

                    }

                }
                else {

                    String timeStamp = String.valueOf(System.currentTimeMillis());
                    HashMap<Object, String> hashMap = new HashMap<>();
                    hashMap.put("pId", timeStamp);
                    hashMap.put("game_name",select_game.getText().toString().trim());
                    hashMap.put("category",category.getText().toString().trim());
                    hashMap.put("total_rounds",edt_total_rounds.getText().toString().trim());
                    hashMap.put("won_round",edt_won_rounds.getText().toString().trim());
                    hashMap.put("creatore_won",edt_won_rounds.getText().toString().trim());
                    hashMap.put("user2_won", String.valueOf(total_rounds-won_round));
                    hashMap.put("user2_id",his_id);
                    hashMap.put("creatore_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap.put("Status", "unapproved");
                    hashMap.put("content",content);
                    hashMap.put("chk_main_id",FirebaseAuth.getInstance().getCurrentUser().getUid());

                    FirebaseDatabase.getInstance().getReference().child("FightLog")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(timeStamp).setValue(hashMap);

                    int his_won = total_rounds - won_round;

                    HashMap<Object, String> hashMap1 = new HashMap<>();
                    hashMap1.put("pId", timeStamp);
                    hashMap1.put("game_name",select_game.getText().toString().trim());
                    hashMap1.put("category",category.getText().toString().trim());
                    hashMap1.put("total_rounds",edt_total_rounds.getText().toString().trim());
                    hashMap1.put("won_round",String.valueOf(his_won));
                    hashMap1.put("creatore_won",String.valueOf(his_won));
                    hashMap1.put("user2_won", edt_won_rounds.getText().toString().trim());
                    hashMap1.put("user2_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap1.put("creatore_id",his_id);
                    hashMap1.put("Status", "unapproved");
                    hashMap1.put("content",content);
                    hashMap1.put("chk_main_id",FirebaseAuth.getInstance().getCurrentUser().getUid());

                    FirebaseDatabase.getInstance().getReference().child("FightLog")
                              .child(his_id)
                              .child(timeStamp).setValue(hashMap1);

                    //increasePost
                    PostCount.increasePost(timeStamp);

                    select_game.setText("Game name");
                    category.setText("1 vs 1");
                    edt_total_rounds.setText("");
                    edt_won_rounds.setText("");
                    edt_content.setText("");
                    post.setVisibility(View.VISIBLE);

                    Toast.makeText(PostFightLogActivity.this, "Your win is saved!", Toast.LENGTH_SHORT).show();

                    //sendMassage
                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    hashMap2.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap2.put("receiver", his_id);
                    hashMap2.put("msg", "Saved a fight result");
                    hashMap2.put("isSeen", false);
                    hashMap2.put("timestamp", ""+System.currentTimeMillis());
                    hashMap2.put("type", "text");
                    hashMap2.put("post_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    hashMap2.put("win_post_id",timeStamp);
                    hashMap2.put("win_type","user_fight_log");
                    FirebaseDatabase.getInstance().getReference().child("Chats").push().setValue(hashMap2);

                    findViewById(R.id.progressBar).setVisibility(View.GONE);

                    tabLayout.getTabAt(1).select();

                }
            }
            else
            {
                Toast.makeText(PostFightLogActivity.this, "Total round is higher than won....", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getMygrouplist(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                myGrouplist=new ArrayList<>();
                myGroupIdlist = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String status = ds.child("status").getValue().toString();

                    if (status.equals("1")){

                        if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()) {

                            ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);

                            String role = ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("role").getValue(String.class);

                            if(role.equals("owner")||role.equals("co-owner"))
                            {
                                myGroupIdlist.add(modelGroups1.getGroupId());
                                ModelGameList md = new ModelGameList();
                                md.setName(modelGroups1.getgName());
                                myGrouplist.add(md);
                                Log.e("roleissssssss",role+" - "+modelGroups1.getgName());

                            }

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getOpponentgrouplist(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                opponentGrouplist=new ArrayList<>();
                opponentGroupIdlist = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    String status = ds.child("status").getValue().toString();
                    if (status.equals("1")){

                        if (ds.child("Participants").child(Objects.requireNonNull(his_id)).exists()) {

                            ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);

                            String role = ds.child("Participants").child(Objects.requireNonNull(his_id)).child("role").getValue(String.class);

                            if(role.equals("owner")||role.equals("co-owner"))
                            {
                                opponentGroupIdlist.add(modelGroups1.getGroupId());
                                ModelGameList md = new ModelGameList();
                                md.setName(modelGroups1.getgName());
                                opponentGrouplist.add(md);

                            }

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getFightLog(){

        FirebaseDatabase.getInstance().getReference("FightLog").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        modelVsFights.clear();
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            ModelVsFight modelfights = ds.getValue(ModelVsFight.class);

                            if (modelfights.getStatus().equals("unapproved")){
                                modelVsFights.add(modelfights);
                            }

                        }

                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        Collections.reverse(modelVsFights);
                        adapterApprovalChat = new AdapterApprovalChat(PostFightLogActivity.this,modelVsFights);
                        rec_fight_log.setLayoutManager(new LinearLayoutManager(PostFightLogActivity.this,LinearLayoutManager.VERTICAL , false));
                        rec_fight_log.setAdapter(adapterApprovalChat);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getClubFightLog(){

        modelGroupVsFightArrayList.clear();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren())
                {

                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){

                        ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);

                        String role = ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                .child("role").getValue(String.class);

                        if(role.equals("owner")||role.equals("co-owner")){

                            FirebaseDatabase.getInstance().getReference("GroupFightLog").child(modelGroups1.getGroupId())
                                    .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot ds : snapshot.getChildren()){

                                        ModelGroupVsFight modelGroupVsFight = ds.getValue(ModelGroupVsFight.class);

                                        if(modelGroupVsFight.getGroup1_id().equals(modelGroups1.getGroupId()))
                                        {
                                            if (modelGroupVsFight.getCreatore_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) &&
                                                    modelGroupVsFight.getStatus().equals("unapproved"))
                                            {
                                                modelGroupVsFightArrayList.add(modelGroupVsFight);
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }
                }

                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Collections.reverse(modelGroupVsFightArrayList);
                adapterApprovalClubFight = new AdapterApprovalClubFight(PostFightLogActivity.this,modelGroupVsFightArrayList);
                rec_fight_club_log.setLayoutManager(new LinearLayoutManager(PostFightLogActivity.this,LinearLayoutManager.VERTICAL , false));
                rec_fight_club_log.setAdapter(adapterApprovalClubFight);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}