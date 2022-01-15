package com.gaming.community.flexster.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterUsers;
import com.gaming.community.flexster.model.ModelGameList;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalAdminRequestActivity extends AppCompatActivity {

    ImageView imageView;
    Button btn_apply;

    RecyclerView rec_admin_users;
    List<ModelUser> userList;
    AdapterUsers adapterUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_admin_request);

        imageView = findViewById(R.id.imageView);
        btn_apply = findViewById(R.id.btn_apply);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        rec_admin_users = findViewById(R.id.rec_admin_users);
        rec_admin_users.setLayoutManager(new LinearLayoutManager(GlobalAdminRequestActivity.this , LinearLayoutManager.VERTICAL , false));
        userList = new ArrayList<>();
        getUserList();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScsDgolRIo3u8YnZIFojcEZh-MiORX9JZtHrRgp3KIB_Pe5_A/viewform"));
                startActivity(browserIntent);
            }
        });

    }

    private void getUserList() {

        FirebaseDatabase.getInstance().getReference("Users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            ModelUser modelUser = ds.getValue(ModelUser.class);
                            //userList.add(modelUser);

                            //AdminInfo
                            FirebaseDatabase.getInstance().getReference("Admin").child(modelUser.getId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                getAdminUser(modelUser.getId());
                                                Log.e("admin_users", modelUser.getName());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void getAdminUser(String user_id){

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

                        adapterUsers = new AdapterUsers(GlobalAdminRequestActivity.this, userList);
                        rec_admin_users.setAdapter(adapterUsers);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);

                        if (adapterUsers.getItemCount() == 0) {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            rec_admin_users.setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                            rec_admin_users.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}