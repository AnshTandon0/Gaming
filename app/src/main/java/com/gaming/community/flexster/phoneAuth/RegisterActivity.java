package com.gaming.community.flexster.phoneAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
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
import com.gaming.community.flexster.MainActivity;
import com.gaming.community.flexster.R;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    String phonenumber;
    String privateMessage = "anyone";
    String privateClubs = "anyone";
    String privatepoke = "anyone";
    //Switch sw_message,sw_clubs;
    RadioButton redio_pm_anyone,redio_pm_private,radio_pc_anyone,radio_pc_private,radio_poke_anyone,radio_poke_private;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*sw_message = findViewById(R.id.sw_message);
        sw_clubs = findViewById(R.id.sw_clubs);*/

        redio_pm_anyone = findViewById(R.id.redio_pm_anyone);
        redio_pm_private = findViewById(R.id.redio_pm_private);
        radio_pc_anyone = findViewById(R.id.radio_pc_anyone);
        radio_pc_private = findViewById(R.id.radio_pc_private);
        radio_poke_anyone = findViewById(R.id.radio_poke_anyone);
        radio_poke_private = findViewById(R.id.radio_poke_private);

        redio_pm_anyone.setChecked(true);
        redio_pm_anyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateMessage = "anyone";
                redio_pm_private.setChecked(false);
            }
        });

        redio_pm_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateMessage = "private";
                redio_pm_anyone.setChecked(false);
            }
        });

        /*redio_pm_private.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/

        /*sw_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (privateMessage.equals("anyone")){
                    privateMessage = "private";
                    Toast.makeText(RegisterActivity.this, "Players I follow", Toast.LENGTH_SHORT).show();
                }
                else {
                    privateMessage = "anyone";
                    Toast.makeText(RegisterActivity.this, "Anyone", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        /*sw_clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (privateClubs.equals("anyone")){
                    privateClubs = "private";
                    Toast.makeText(RegisterActivity.this, "Players I follow", Toast.LENGTH_SHORT).show();
                }
                else {
                    privateClubs = "anyone";
                    Toast.makeText(RegisterActivity.this, "Anyone", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        radio_pc_anyone.setChecked(true);
        radio_pc_anyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateClubs = "anyone";
                radio_pc_private.setChecked(false);
            }
        });

        radio_pc_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privateClubs = "private";
                radio_pc_anyone.setChecked(false);
            }
        });

        radio_poke_anyone.setChecked(true);
        radio_poke_anyone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privatepoke = "anyone";
                radio_poke_private.setChecked(false);
            }
        });

        radio_poke_private.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privatepoke = "private";
                radio_poke_anyone.setChecked(false);
            }
        });

        phonenumber = getIntent().getStringExtra("phone");

        EditText name = findViewById(R.id.name);
        EditText username = findViewById(R.id.username);

        //Button
        findViewById(R.id.login).setOnClickListener(v -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            String mName = name.getText().toString().trim();
            String mUsername = username.getText().toString().trim();
            if (mName.isEmpty()){
                Snackbar.make(v,"Enter your Name", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }else if(mUsername.isEmpty()){
                Snackbar.make(v,"Enter your Username", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }else {
                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(mUsername);
                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount()>0){
                            Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                        }else {
                            register(mName,mUsername);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(v,error.getMessage(), Snackbar.LENGTH_LONG).show();
                        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
    }


    private void register(String mName, String mUsername) {

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("name", mName);
        hashMap.put("email", "");
        hashMap.put("username", mUsername);
        hashMap.put("bio", "");
        hashMap.put("verified","");
        hashMap.put("location","");
        hashMap.put("phone",phonenumber);
        hashMap.put("status", ""+System.currentTimeMillis());
        hashMap.put("typingTo","noOne");
        hashMap.put("link","");
        hashMap.put("photo", "");
        hashMap.put("privateMessage",privateMessage);
        hashMap.put("privateClubs",privateClubs);
        hashMap.put("privatePoke",privatepoke);
        hashMap.put("isactive","1");
        FirebaseDatabase.getInstance().getReference("Users").child(userId).setValue(hashMap).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
        });

    }

}