package com.gaming.community.flexster.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterWarnGroups;
import com.gaming.community.flexster.model.ModelGroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WarnGroupActivity extends AppCompatActivity {

    String hisUid;
    private RecyclerView users_rv;
    private List<ModelGroups> userList;
    private AdapterWarnGroups adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);

        //Back
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        //User
        users_rv = findViewById(R.id.list);
        users_rv.setLayoutManager(new LinearLayoutManager(WarnGroupActivity.this));
        userList = new ArrayList<>();
        getdatas();
        //EdiText
        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if(editText.getText().toString().trim().equals(""))
                {
                    getdatas();
                }
                else {
                    getdatasasfilter(editText.getText().toString().trim());
                }
                return true;
            }
            return false;
        });

    }

    private void getdatasasfilter(String trim)
    {
        FirebaseDatabase.getInstance().getReference("warn").child("group")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){

                    hisUid = ""+ ds.getRef().getKey();

                    filter(trim);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getdatas()
    {
        FirebaseDatabase.getInstance().getReference("warn").child("group").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    hisUid = ""+ ds.getRef().getKey();
                    getAllUsers();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void getAllUsers() {
        FirebaseDatabase.getInstance().getReference("Groups").orderByChild("groupId").equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("status").getValue(String.class).equals("1"))
                            {
                                ModelGroups modelUser = ds.getValue(ModelGroups.class);
                                userList.add(modelUser);
                            }
                        }
                        adapterUsers = new AdapterWarnGroups(WarnGroupActivity.this, userList);
                        users_rv.setAdapter(adapterUsers);
                        if (adapterUsers.getItemCount() == 0){
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void filter(String query) {
        FirebaseDatabase.getInstance().getReference("Groups").orderByChild("groupId").equalTo(hisUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("status").getValue(String.class).equals("1"))
                            {
                                ModelGroups modelUser = ds.getValue(ModelGroups.class);
                                if (Objects.requireNonNull(modelUser).getgName().toLowerCase().contains(query.toLowerCase()) ||
                                        modelUser.getgUsername().toLowerCase().contains(query.toLowerCase())){
                                    userList.add(modelUser);
                                }
                            }

                        }
                        adapterUsers = new AdapterWarnGroups(WarnGroupActivity.this, userList);
                        users_rv.setAdapter(adapterUsers);
                        adapterUsers.notifyDataSetChanged();
                        if (adapterUsers.getItemCount() == 0){
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.GONE);
                            findViewById(R.id.nothing).setVisibility(View.VISIBLE);
                        }else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            users_rv.setVisibility(View.VISIBLE);
                            findViewById(R.id.nothing).setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}