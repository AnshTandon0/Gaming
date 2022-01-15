package com.gaming.community.flexster.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelGameList;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewGameAddActivity extends AppCompatActivity {

    ImageView back;
    EditText edt_title;
    CardView card_game_add;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_add);

        back = findViewById(R.id.back);
        edt_title = findViewById(R.id.edt_title);
        card_game_add = findViewById(R.id.card_game_add);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewGameAddActivity.this,GameListActivity.class));
                finish();
            }
        });

        card_game_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_title.getText().toString().trim().equals(""))
                {
                    setvalues(edt_title.getText().toString());
                }
                else
                {
                    edt_title.setError("Enter game name.");
                }
            }
        });

    }

    public void setvalues(String gamename)
    {
        ModelGameList md=new ModelGameList();
        md.setName(gamename);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("games");
        String autoid=databaseReference.push().getKey();
        databaseReference.child(autoid).setValue(md);
        startActivity(new Intent(NewGameAddActivity.this,GameListActivity.class));
        finish();

    }
}