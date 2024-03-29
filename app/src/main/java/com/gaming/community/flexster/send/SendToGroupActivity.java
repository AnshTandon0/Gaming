package com.gaming.community.flexster.send;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterSendGroups;
import com.gaming.community.flexster.model.ModelGroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SendToGroupActivity extends AppCompatActivity {

    private static String type;
    private static String uri;
    private static String win_post_id;
    private static String creater_win_id;
    private static String win_type;

    public static String getType() {
        return type;
    }

    public static String getUri() {
        return uri;
    }

    public static String getWin_post_id(){
        return win_post_id;
    }

    public static String getCreater_win_id(){
        return creater_win_id;
    }

    public static String getWin_type(){
        return win_type;
    }

    public SendToGroupActivity(){

    }

    //User
    AdapterSendGroups adapterGroups;
    List<ModelGroups> modelGroups;
    RecyclerView users_rv;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);

        //Strings
        type =getIntent().getStringExtra("type");
        uri =getIntent().getStringExtra("uri");

        if (getIntent().getStringExtra("creater_win_id") != null){
            creater_win_id = getIntent().getStringExtra("creater_win_id");
        }

        if (getIntent().getStringExtra("win_type") != null){
            win_type = getIntent().getStringExtra("win_type");
        }

        if (getIntent().getStringExtra("win_post_id") != null){
            win_post_id = getIntent().getStringExtra("win_post_id");
        }

        //User
        users_rv = findViewById(R.id.users);
        users_rv.setLayoutManager(new LinearLayoutManager(SendToGroupActivity.this));
        modelGroups = new ArrayList<>();
        getAllGroups();
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(editText.getText().toString());
                return true;
            }
            return false;
        });


    }

    private void filter(String query) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroups.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){
                        ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);
                        if (Objects.requireNonNull(modelGroups1).getgName().toLowerCase().contains(query.toLowerCase()) ||
                                modelGroups1.getgUsername().toLowerCase().contains(query.toLowerCase())){
                            modelGroups.add(modelGroups1);
                        }
                    }
                    adapterGroups = new AdapterSendGroups(SendToGroupActivity.this, modelGroups);
                    users_rv.setAdapter(adapterGroups);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterGroups.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        users_rv.setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        users_rv.setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAllGroups() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelGroups.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists()){
                        ModelGroups modelGroups1 = ds.getValue(ModelGroups.class);
                        modelGroups.add(modelGroups1);
                    }
                    adapterGroups = new AdapterSendGroups(SendToGroupActivity.this, modelGroups);
                    users_rv.setAdapter(adapterGroups);
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                    if (adapterGroups.getItemCount() == 0){
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        users_rv.setVisibility(View.GONE);
                        findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                    }else {
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        users_rv.setVisibility(View.VISIBLE);
                        findViewById(R.id.nothing).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}