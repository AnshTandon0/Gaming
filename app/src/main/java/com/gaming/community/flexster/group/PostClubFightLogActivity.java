package com.gaming.community.flexster.group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterApproval;
import com.gaming.community.flexster.adapter.AdapterDialogGameList;
import com.gaming.community.flexster.adapter.AdapterNotification;
import com.gaming.community.flexster.adapter.ScrimNotification;
import com.gaming.community.flexster.model.ModelGameList;
import com.gaming.community.flexster.model.ModelNotification;
import com.gaming.community.flexster.model.ModelPostClubFight;
import com.gaming.community.flexster.notifications.AnNotificationScreen;
import com.gaming.community.flexster.send.SendToGroupActivity;
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

public class PostClubFightLogActivity extends AppCompatActivity {

    ImageView imageView;
    LinearLayout ll_save_you_win,ll_approval,ll_scrim;
    TabLayout tabLayout;

    TextView select_game,select_scrim_type;

    EditText edt_total_rounds,edt_won_rounds,edt_content;

    Button post;

    String selected_game="",content = "",mygroupID = "";
    private RecyclerView.LayoutManager mlayoutManager;
    AdapterDialogGameList adapterDialogGameList;

    RecyclerView rec_approval;
    AdapterApproval adapterApproval;
    ArrayList<ModelPostClubFight> modelPostClubFights = new ArrayList<>();

    RecyclerView rec_scrim;
    ArrayList<ModelNotification> notifications;
    ScrimNotification scrimNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_club_fight_log);

        imageView = findViewById(R.id.imageView);
        ll_save_you_win = findViewById(R.id.ll_save_you_win);
        ll_approval = findViewById(R.id.ll_approval);
        ll_scrim = findViewById(R.id.ll_scrim);
        rec_approval = findViewById(R.id.rec_approval);
        rec_scrim = findViewById(R.id.rec_scrim);
        tabLayout = findViewById(R.id.tabLayout);
        select_game = findViewById(R.id.select_game);
        select_scrim_type = findViewById(R.id.select_scrim_type);
        edt_total_rounds = findViewById(R.id.edt_total_rounds);
        edt_won_rounds = findViewById(R.id.edt_won_rounds);
        edt_content = findViewById(R.id.edt_content);
        post = findViewById(R.id.post);

        Intent intent = getIntent();
        mygroupID = intent.getStringExtra("group_id");
        getPostClubFight();
        getScrimNotification();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        select_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostClubFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.WHITE));
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

                        adapterDialogGameList = new AdapterDialogGameList(PostClubFightLogActivity.this,modelGameLists,gamenameItemListener);
                        mlayoutManager = new LinearLayoutManager(PostClubFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
                        rec_game_list.setLayoutManager(mlayoutManager);
                        rec_game_list.setItemAnimator(new DefaultItemAnimator());
                        rec_game_list.setAdapter(adapterDialogGameList);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(PostClubFightLogActivity.this, "Couldn't Load Data", Toast.LENGTH_SHORT).show();
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


        select_scrim_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(PostClubFightLogActivity.this,R.style.CustomDialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.activity_game_list);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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
                        select_scrim_type.setText(gamename);
                        dialog.dismiss();
                    }
                };

                ArrayList<ModelGameList> scrimTypeLists = new ArrayList<>();
                ModelGameList md = new ModelGameList();
                md.setName("Mixed mode");
                scrimTypeLists.add(md);

                ModelGameList md1 = new ModelGameList();
                md1.setName("Solo mode");
                scrimTypeLists.add(md1);

                ModelGameList md2 = new ModelGameList();
                md2.setName("Duo mode");
                scrimTypeLists.add(md2);

                ModelGameList md3 = new ModelGameList();
                md3.setName("Trio mode");
                scrimTypeLists.add(md3);

                ModelGameList md4 = new ModelGameList();
                md4.setName("Squad mode");
                scrimTypeLists.add(md4);

                ModelGameList md5 = new ModelGameList();
                md5.setName("Penta mode");
                scrimTypeLists.add(md5);

                adapterDialogGameList = new AdapterDialogGameList(PostClubFightLogActivity.this,scrimTypeLists,gamenameItemListener);
                mlayoutManager = new LinearLayoutManager(PostClubFightLogActivity.this, LinearLayoutManager.VERTICAL, false);
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
                    uploadPostClubFight();
                }
                else {
                    content = edt_content.getText().toString().trim();
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    uploadPostClubFight();
                }

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {

                    ll_approval.setVisibility(View.GONE);
                    ll_save_you_win.setVisibility(View.GONE);
                    ll_scrim.setVisibility(View.VISIBLE);

                }
                else if (tabLayout.getSelectedTabPosition() == 1) {

                    ll_scrim.setVisibility(View.GONE);
                    ll_approval.setVisibility(View.GONE);
                    ll_save_you_win.setVisibility(View.VISIBLE);

                }
                else if (tabLayout.getSelectedTabPosition() == 2) {

                    ll_scrim.setVisibility(View.GONE);
                    ll_save_you_win.setVisibility(View.GONE);
                    ll_approval.setVisibility(View.VISIBLE);

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

    private void uploadPostClubFight(){

        if (select_game.getText().toString().equals("Game name")){
            Toast.makeText(PostClubFightLogActivity.this, "Select game first", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
        else if (edt_total_rounds.getText().toString().isEmpty()){
            Toast.makeText(PostClubFightLogActivity.this, "Enter total rounds played", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
        else if (edt_won_rounds.getText().toString().isEmpty()){
            Toast.makeText(PostClubFightLogActivity.this, "Enter rounds won", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
        else if (Integer.parseInt(edt_won_rounds.getText().toString()) > Integer.parseInt(edt_total_rounds.getText().toString())){
            Toast.makeText(PostClubFightLogActivity.this, "Total round is higher than won....", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }
        else {
            post.setVisibility(View.GONE);

            int total_rounds = Integer.parseInt(edt_total_rounds.getText().toString());
            int total_won = Integer.parseInt(edt_won_rounds.getText().toString());

            String timeStamp = String.valueOf(System.currentTimeMillis());
            HashMap<Object, String> hashMap = new HashMap<>();
            hashMap.put("pId", timeStamp);
            hashMap.put("game_name",select_game.getText().toString().trim());
            hashMap.put("category","Club Screen");
            hashMap.put("scrim_type",select_scrim_type.getText().toString().trim());
            hashMap.put("total_rounds",edt_total_rounds.getText().toString().trim());
            hashMap.put("user_won",edt_won_rounds.getText().toString().trim());
            hashMap.put("group_id",mygroupID);
            hashMap.put("group_won", String.valueOf(total_rounds-total_won));
            hashMap.put("creatore_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("content",content);
            hashMap.put("status","unapproved");

            FirebaseDatabase.getInstance().getReference().child("PostUserClubFight")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(timeStamp).setValue(hashMap);

            FirebaseDatabase.getInstance().getReference().child("PostClubFight")
                    .child(mygroupID)
                    .child(timeStamp).setValue(hashMap);

            //sendClubMassage
            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap1.put("msg", "");
            hashMap1.put("type", "text");
            hashMap1.put("timestamp", timeStamp);
            hashMap1.put("replayId","");
            hashMap1.put("replayMsg","");
            hashMap1.put("replayUserId","");
            hashMap1.put("creater_win_id",mygroupID);
            hashMap1.put("win_log_msg","Saved a fight result");
            hashMap1.put("win_post_id", timeStamp);
            hashMap1.put("win_type","club_fight_log");

            FirebaseDatabase.getInstance().getReference("Groups").child(mygroupID).child("Message").child(timeStamp)
                    .setValue(hashMap1);
            //**********************************

            Toast.makeText(PostClubFightLogActivity.this, "Your fight is saved!", Toast.LENGTH_SHORT).show();

            tabLayout.getTabAt(1).select();

            select_game.setText("Select Game");
            select_scrim_type.setText("Mixed mode");
            edt_total_rounds.setText("");
            edt_won_rounds.setText("");
            edt_content.setText("");
            post.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);

        }

    }

    private void getPostClubFight(){
        FirebaseDatabase.getInstance().getReference("PostClubFight").child(mygroupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        modelPostClubFights.clear();
                        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                        for (DataSnapshot ds : snapshot.getChildren())
                        {
                            ModelPostClubFight  md = ds.getValue(ModelPostClubFight.class);
                            if (md.getStatus().equals("unapproved")){
                                modelPostClubFights.add(md);
                            }
                        }

                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        Collections.reverse(modelPostClubFights);
                        adapterApproval = new AdapterApproval(PostClubFightLogActivity.this,modelPostClubFights);
                        rec_approval.setLayoutManager(new LinearLayoutManager(PostClubFightLogActivity.this,LinearLayoutManager.VERTICAL , false));
                        rec_approval.setAdapter(adapterApproval);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void getScrimNotification(){
        notifications = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Groups")
                .child(mygroupID)
                .child("AnnouncementScrim")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifications.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModelNotification modelNotification = ds.getValue(ModelNotification.class);
                    notifications.add(modelNotification);
                }
                Collections.reverse(notifications);
                scrimNotification = new ScrimNotification(PostClubFightLogActivity.this, notifications);
                rec_scrim.setLayoutManager(new LinearLayoutManager(PostClubFightLogActivity.this,LinearLayoutManager.VERTICAL , false));
                rec_scrim.setAdapter(scrimNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PostClubFightLogActivity.this,GroupChatActivity.class);
        intent.putExtra("group",mygroupID);
        intent.putExtra("type","create");
        startActivity(intent);
    }

}