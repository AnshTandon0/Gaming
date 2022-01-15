package com.gaming.community.flexster.group;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.gaming.community.flexster.adapter.AdapterCreateChat;
import com.gaming.community.flexster.adapter.GroupUserListAdapter;
import com.gaming.community.flexster.chat.CreateChatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterUsers;
import com.gaming.community.flexster.model.ModelUser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class GroupMembersActivity extends AppCompatActivity {

    //User
    private RecyclerView users_rv;
    private List<ModelUser> userList;
    private GroupUserListAdapter adapterUsers;
    String myGroupRole="";
    String id;
    String official = "";
    List<String> list;

    NightMode sharedPref;

    List<ModelUser> updatedlist=new ArrayList<>();
    List<String> rolesare=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState())
        {
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_who);

        id  = getIntent().getStringExtra("group");
        official = getIntent().getStringExtra("official");

        //Back
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        //User
        users_rv = findViewById(R.id.list);
        users_rv.setLayoutManager(new LinearLayoutManager(GroupMembersActivity.this));
        userList = new ArrayList<>();
        getMembers();

        //EdiText
        EditText editText = findViewById(R.id.editText);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filter(editText.getText().toString());
                return true;
            }
            return false;
        });

    }

    private void getMembers() {
        list = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(id).child("Participants").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    list.add(ds.getKey());
                }


                FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("Participants")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists())
                                {
                                    myGroupRole = ""+snapshot.child("role").getValue();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                FirebaseDatabase.getInstance().getReference().child("Groups").child(id).child("Participants")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                rolesare.clear();
                                for (DataSnapshot ds: snapshot.getChildren())
                                {
                                    String name= (String) ds.child("role").getValue();

                                    rolesare.add(name);
/*
                                    if (official.equals("official"))
                                    {
                                        if(name.equals("vip guest")||name.equals("guest"))
                                        {

                                        }
                                        else
                                        {
                                            rolesare.add(name);
                                        }
                                    }
                                    else {
                                        rolesare.add(name);
                                    }*/

                                    Log.e("stringissss",name+"abcd");
                                }
                                getUser();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getUser() {
        userList.clear();
        ArrayList<ModelUser> owner=new ArrayList<>();
        ArrayList<ModelUser> coowner=new ArrayList<>();
        ArrayList<ModelUser> mod=new ArrayList<>();
        ArrayList<ModelUser> senior=new ArrayList<>();
        ArrayList<ModelUser> member=new ArrayList<>();
        ArrayList<ModelUser> vip=new ArrayList<>();
        ArrayList<ModelUser> guest=new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        owner.clear();
                        coowner.clear();
                        mod.clear();
                        senior.clear();
                        member.clear();
                        vip.clear();
                        guest.clear();
                        updatedlist.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("isactive").getValue(String.class).equals("1"))
                            {
                                ModelUser modelUser = ds.getValue(ModelUser.class);
                                for(String id : list)
                                {
                                    int position=list.indexOf(id);
                                    String role=rolesare.get(position);
                                    if (Objects.requireNonNull(modelUser).getId().equals(id))
                                    {
                                        if(role.equals("owner"))
                                        {
                                            owner.add(modelUser);
                                        }
                                        else if(role.equals("co-owner"))
                                        {
                                            coowner.add(modelUser);
                                        }
                                        else if(role.equals("mod"))
                                        {
                                            mod.add(modelUser);
                                        }
                                        else if(role.equals("senior"))
                                        {
                                            senior.add(modelUser);
                                        }
                                        else if(role.equals("member"))
                                        {
                                            member.add(modelUser);
                                        }
                                        else if(role.equals("vip guest"))
                                        {
                                            vip.add(modelUser);
                                        }
                                        else if(role.equals("guest"))
                                        {
                                            guest.add(modelUser);
                                        }
                                    }
                                }
                            }
                        }

                        Log.e("ownersizeee", String.valueOf(owner.size()));
                        Log.e("guestsizeee", String.valueOf(guest.size()));

                        for(ModelUser md:owner)
                        {
                            updatedlist.add(md);
                        }
                        for(ModelUser md:coowner)
                        {
                            updatedlist.add(md);
                        }
                        for(ModelUser md:mod)
                        {
                            updatedlist.add(md);
                        }
                        for(ModelUser md:senior)
                        {
                            updatedlist.add(md);
                        }
                        for(ModelUser md:member)
                        {
                            updatedlist.add(md);
                        }
                        if (!official.equals("official"))
                        {
                            for(ModelUser md:vip)
                            {
                                updatedlist.add(md);
                            }
                            for(ModelUser md:guest)
                            {
                                updatedlist.add(md);
                            }
                        }

                        Log.e("arraysizeisssws", String.valueOf(updatedlist.size()));
                        //setsortingofgroup();
                        setadapters();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setadapters()
    {
        adapterUsers = new GroupUserListAdapter(GroupMembersActivity.this, updatedlist,id,myGroupRole);
        adapterUsers.notifyDataSetChanged();
        /*users_rv.setHasFixedSize(true);
        users_rv.setItemViewCacheSize(20);
        users_rv.setDrawingCacheEnabled(true);
        users_rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/
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

    private void filter(String query) {

        FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(id)
                .child("Participants")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            myGroupRole = ""+snapshot.child("role").getValue();
                        }
                        getuserdatas(query);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void getuserdatas(String query) {
        userList.clear();
        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            if(ds.child("isactive").getValue(String.class).equals("1"))
                            {
                                ModelUser modelUser = ds.getValue(ModelUser.class);
                                for (String id : list){
                                    if (Objects.requireNonNull(modelUser).getId().equals(id)){
                                        if (modelUser.getName().toLowerCase().contains(query.toLowerCase()) ||
                                                modelUser.getUsername().toLowerCase().contains(query.toLowerCase())){
                                            userList.add(modelUser);
                                        }
                                    }
                                }
                            }
                        }
                        adapterUsers = new GroupUserListAdapter(GroupMembersActivity.this, userList,id,myGroupRole);
                        adapterUsers.notifyDataSetChanged();
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

}