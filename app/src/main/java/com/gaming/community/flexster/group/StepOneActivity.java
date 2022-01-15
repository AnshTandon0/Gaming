package com.gaming.community.flexster.group;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;

import java.util.HashMap;
import java.util.Objects;

public class StepOneActivity extends AppCompatActivity {

    NightMode sharedPref;

    String club_private="public";
    //Switch sw_clubs;
    RadioButton radio_club_public,radio_club_private;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_one);

        //sw_clubs = findViewById(R.id.sw_clubs);
        radio_club_public = findViewById(R.id.radio_club_public);
        radio_club_private = findViewById(R.id.radio_club_private);

        //back
        findViewById(R.id.imageView).setOnClickListener(v -> onBackPressed());

        //EditText
        EditText name = findViewById(R.id.name);
        EditText username = findViewById(R.id.username);
        EditText link = findViewById(R.id.link);
        EditText details = findViewById(R.id.details);

       /* sw_clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (club_private.equals("private")){
                    club_private="public";
                    Toast.makeText(StepOneActivity.this, "Public", Toast.LENGTH_SHORT).show();
                }
                else {
                    club_private="private";
                    Toast.makeText(StepOneActivity.this, "Private", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        radio_club_public.setChecked(true);
        radio_club_public.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                club_private="public";
                radio_club_private.setChecked(false);
            }
        });

        radio_club_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                club_private="private";
                radio_club_public.setChecked(false);
            }
        });

        //Next
        findViewById(R.id.next).setOnClickListener(v -> {

            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            if (name.getText().toString().isEmpty() || username.getText().toString().isEmpty()){
                Snackbar.make(v,"Name & username should not be empty", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
            else {
                //checkUsername
                Query query = FirebaseDatabase.getInstance().getReference("Groups").orderByChild("gUsername").equalTo(username.getText().toString());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                        else {
                            String timeStamp = ""+System.currentTimeMillis();
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("groupId", ""+timeStamp);
                            hashMap.put("gName", ""+name.getText().toString());
                            hashMap.put("gUsername", ""+username.getText().toString());
                            hashMap.put("gBio", ""+details.getText().toString());
                            hashMap.put("gLink", ""+link.getText().toString());
                            hashMap.put("gIcon", "");
                            hashMap.put("timestamp", ""+timeStamp);
                            hashMap.put("clubPrivacy",club_private);
                            hashMap.put("status", "1");
                            hashMap.put("createdBy", ""+ Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                            FirebaseDatabase.getInstance().getReference("Groups").child(timeStamp).setValue(hashMap).addOnCompleteListener(task -> {
                                HashMap<String, String> hashMap1 = new HashMap<>();
                                hashMap1.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                hashMap1.put("role","owner");
                                hashMap1.put("scrimster", "yes");
                                hashMap1.put("timestamp", timeStamp);
                                FirebaseDatabase.getInstance().getReference("Groups")
                                        .child(timeStamp).child("Participants")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        .setValue(hashMap1);
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                                Intent intent = new Intent(getApplicationContext(), StepTwoActivity.class);
                                intent.putExtra("group", timeStamp);
                                startActivity(intent);
                                finish();
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });
    }

}