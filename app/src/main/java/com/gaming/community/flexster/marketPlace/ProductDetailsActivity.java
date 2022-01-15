package com.gaming.community.flexster.marketPlace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.MediaViewActivity;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.chat.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductDetailsActivity extends AppCompatActivity {

    String pId;

    NightMode sharedPref;

    ImageView verified,admin;
    TextView txt_post_count,txt_fight_count;
    ImageView more;

    String user_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        verified = findViewById(R.id.verified);
        admin = findViewById(R.id.admin);
        txt_post_count = findViewById(R.id.txt_post_count);
        txt_fight_count = findViewById(R.id.txt_fight_count);
        more = findViewById(R.id.more);

        pId = getIntent().getStringExtra("pId");

        //Id
        ImageView cover = findViewById(R.id.cover);
        //TextView price = findViewById(R.id.price);
        TextView title = findViewById(R.id.title);
        TextView des = findViewById(R.id.des);
        TextView type = findViewById(R.id.type);
        //TextView location = findViewById(R.id.location);
        TextView user = findViewById(R.id.user);
        TextView time = findViewById(R.id.time);
        CircleImageView dp  = findViewById(R.id.dp);

        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        FirebaseDatabase.getInstance().getReference("Product").child(pId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(cover);
                //price.setText(Objects.requireNonNull(snapshot.child("price").getValue()).toString());
                title.setText(Objects.requireNonNull(snapshot.child("cat").getValue()).toString()+" "+Objects.requireNonNull(snapshot.child("title").getValue()).toString());
                des.setText(Objects.requireNonNull(snapshot.child("des").getValue()).toString());
                type.setText(Objects.requireNonNull(snapshot.child("type").getValue()).toString());
                long lastTime = Long.parseLong(Objects.requireNonNull(snapshot.child("pId").getValue()).toString());
                time.setText(GetTimeAgo.getTimeAgo(lastTime));
                //location.setText(Objects.requireNonNull(snapshot.child("location").getValue()).toString());
                FirebaseDatabase.getInstance().getReference("Users")
                        .child(Objects.requireNonNull(snapshot.child("id").getValue()).toString())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        user.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                        if (!Objects.requireNonNull(snapshot.child("photo").getValue()).toString().isEmpty()){
                            Picasso.get().load(Objects.requireNonNull(snapshot.child("photo").getValue()).toString()).into(dp);
                        }

                        user_id = Objects.requireNonNull(snapshot.child("id").getValue()).toString();

                        //PostCount
                        PostCount.getfightlevel(user_id,txt_fight_count);
                        PostCount.getengagelevel(user_id,txt_post_count);

                        //UserInfo
                        FirebaseDatabase.getInstance().getReference()
                                .child("Users")
                                .child(user_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        //Verify
                                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                                            verified.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            verified.setVisibility(View.GONE);
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                        //AdminInfo
                        FirebaseDatabase.getInstance().getReference("Admin").child(user_id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            admin.setVisibility(View.VISIBLE);
                                        } else {
                                            admin.setVisibility(View.GONE);
                                        }
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

                cover.setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailsActivity.this, MediaViewActivity.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", Objects.requireNonNull(snapshot.child("photo").getValue()).toString());
                    startActivity(intent);
                });

                findViewById(R.id.message).setOnClickListener(v -> {
                    Intent intent = new Intent(ProductDetailsActivity.this, ChatActivity.class);
                    intent.putExtra("hisUID", Objects.requireNonNull(snapshot.child("id").getValue()).toString());
                    startActivity(intent);
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Context moreWrapper = new ContextThemeWrapper(ProductDetailsActivity.this, R.style.popupMenuStyle);
        PopupMenu morePop = new PopupMenu(moreWrapper, more);
        morePop.getMenu().add(Menu.NONE,1,1, "Remove");

        morePop.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == 1){
                    if (user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        FirebaseDatabase.getInstance().getReference("Product").child(pId).getRef().removeValue();
                        Toast.makeText(ProductDetailsActivity.this, "Removed challenge", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else {
                        Toast.makeText(ProductDetailsActivity.this, "You are not authorized", Toast.LENGTH_SHORT).show();
                    }

                }
                return false;
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morePop.show();
            }
        });

    }
}