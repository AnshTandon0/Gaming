package com.gaming.community.flexster.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;

import java.util.HashMap;
import java.util.Objects;

public class VerificationActivity extends AppCompatActivity {

    NightMode sharedPref;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        findViewById(R.id.imageView).setOnClickListener(v -> onBackPressed());

        EditText name = findViewById(R.id.name);
        EditText username = findViewById(R.id.username);
        EditText known = findViewById(R.id.known);
        EditText id = findViewById(R.id.id);
        apply = findViewById(R.id.apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSeXxg1rV_n66JXl--Kd4oKND8-rsyfa2fwfFbgfA2r9X39txA/viewform"));
                startActivity(browserIntent);
            }
        });

        findViewById(R.id.send).setOnClickListener(v -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            if (name.getText().toString().isEmpty()) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Snackbar.make(v, "Enter name", Snackbar.LENGTH_SHORT).show();
            }
            else if (username.getText().toString().isEmpty()) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Snackbar.make(v, "Enter username", Snackbar.LENGTH_SHORT).show();
            }
            /*else if (known.getText().toString().isEmpty()) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Snackbar.make(v, "Enter known as", Snackbar.LENGTH_SHORT).show();
            }
            else if (id.getText().toString().isEmpty()) {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                Snackbar.make(v, "Enter photo id link", Snackbar.LENGTH_SHORT).show();
            }*/
            else {
                String timeStamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name",  name.getText().toString());
                hashMap.put("username",  username.getText().toString());
                hashMap.put("vId",  timeStamp);
                hashMap.put("uID", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                hashMap.put("link", "flexster");
                hashMap.put("known", "flexter");
                FirebaseDatabase.getInstance().getReference().child("Verification").child(timeStamp).setValue(hashMap);
                Snackbar.make(v, "Request Sent", Snackbar.LENGTH_LONG).show();
                name.setText("");
                username.setText("");
                /*known.setText("");
                id.setText("");*/
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });

    }
}