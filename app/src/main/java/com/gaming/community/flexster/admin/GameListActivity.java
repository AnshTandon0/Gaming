package com.gaming.community.flexster.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterGameList;
import com.gaming.community.flexster.model.ModelGameList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity {

    ImageView back;
    EditText editText;
    private RecyclerView.LayoutManager mlayoutManager;
    RecyclerView rec_game_list;
    ArrayList<ModelGameList> modelGameLists = new ArrayList<>();
    AdapterGameList adapterGameList;
    CardView card_new_game_add;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        back = findViewById(R.id.back);
        editText = findViewById(R.id.editText);
        rec_game_list = findViewById(R.id.rec_game_list);
        card_new_game_add = findViewById(R.id.card_new_game_add);

        fetchdatas();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameListActivity.this,AdminActivity.class));
                finish();
            }
        });

        card_new_game_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameListActivity.this,NewGameAddActivity.class));
                finish();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // filter recycler view when query submitted
                adapterGameList.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    private void fetchdatas() {

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
                adapterGameList = new AdapterGameList(GameListActivity.this,modelGameLists);
                mlayoutManager = new LinearLayoutManager(GameListActivity.this, LinearLayoutManager.VERTICAL, false);
                rec_game_list.setLayoutManager(mlayoutManager);
                rec_game_list.setItemAnimator(new DefaultItemAnimator());
                rec_game_list.setAdapter(adapterGameList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(GameListActivity.this, "Couldn't Load Data", Toast.LENGTH_SHORT).show();
            }
        });

    }
}