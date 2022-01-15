package com.gaming.community.flexster.marketPlace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gaming.community.flexster.OnSelectedItemListener;
import com.gaming.community.flexster.adapter.AdapterDialogGameList;
import com.gaming.community.flexster.model.ModelGameList;
import com.gaming.community.flexster.post.CommentActivity;
import com.gaming.community.flexster.post.CreatePostActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class PostProductActivity extends AppCompatActivity implements CatPick.SingleChoiceListener{

    //TypePick.SingleChoiceListener,

    //Permission
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private static final int LOCATION_PICK_CODE = 1009;

    //String
    Uri dp_uri;
    String typeString = "";
    String catString = "1 vs 1";

    //Id
    RoundedImageView roundedImageView;
    TextView type;
    TextView cat;

    private RecyclerView.LayoutManager mlayoutManager;
    AdapterDialogGameList adapterDialogGameList;

    //EdiText
    EditText price,des,location;

    NightMode sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_product);

        //Id
        roundedImageView = findViewById(R.id.cover);
        cat = findViewById(R.id.category);
        type = findViewById(R.id.condition);

        //back
        findViewById(R.id.imageView).setOnClickListener(v -> onBackPressed());

        cat.setOnClickListener(v -> {
            DialogFragment dialogFragment = new CatPick();
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(), "Single Choice Dialog");
        });

        type.setOnClickListener(v -> {

            Dialog dialog = new Dialog(PostProductActivity.this,R.style.CustomDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_game_list);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.show();

            ImageView back = dialog.findViewById(R.id.back);
            EditText editText = dialog.findViewById(R.id.editText);
            RecyclerView rec_game_list = dialog.findViewById(R.id.rec_game_list);
            CardView card_new_game_add = dialog.findViewById(R.id.card_new_game_add);
            card_new_game_add.setVisibility(View.GONE);

            OnSelectedItemListener gamenameItemListener=new OnSelectedItemListener() {
                @Override
                public void setOnClick(String gamename, int position) {
                    type.setText(gamename);
                    typeString = type.getText().toString();
                    type.setTextColor(Color.parseColor("#4e4f54"));
                    dialog.dismiss();
                }
            };

            DatabaseReference databaseReference;
            ArrayList<ModelGameList> modelGameLists = new ArrayList<>();

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

                    adapterDialogGameList = new AdapterDialogGameList(PostProductActivity.this,modelGameLists,gamenameItemListener);
                    mlayoutManager = new LinearLayoutManager(PostProductActivity.this, LinearLayoutManager.VERTICAL, false);
                    rec_game_list.setLayoutManager(mlayoutManager);
                    rec_game_list.setItemAnimator(new DefaultItemAnimator());
                    rec_game_list.setAdapter(adapterDialogGameList);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PostProductActivity.this, "Couldn't Load Data", Toast.LENGTH_SHORT).show();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // filter recycler view when query submitted
                    adapterDialogGameList.getFilter().filter(s);
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });

        });

        //Cover
        findViewById(R.id.cover).setOnClickListener(v -> {
            //Check Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else {
                    pickImage();
                }
            }
            else {
                pickImage();
            }
        });


         //title = findViewById(R.id.title);
         des = findViewById(R.id.des);

        //Post
        findViewById(R.id.login).setOnClickListener(v -> {

            if (typeString.equals("")){
                Snackbar.make(findViewById(R.id.main), "Please select a game name", Snackbar.LENGTH_LONG).show();
            }
            else if (dp_uri == null){
                Snackbar.make(findViewById(R.id.main), "Please add an image", Snackbar.LENGTH_LONG).show();
            }
            else if (des.getText().toString().trim().isEmpty()){
                Snackbar.make(findViewById(R.id.main), "Please put a description", Snackbar.LENGTH_LONG).show();
            }
            else {
                compressImage(dp_uri);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }
        });

        //Intent
        if(getIntent().hasExtra("uri") && getIntent().hasExtra("type")){
         if (getIntent().getStringExtra("type").equals("image")){
             dp_uri = Uri.parse(getIntent().getStringExtra("uri"));
             Picasso.get().load(dp_uri).into(roundedImageView);
            }
        }

    }

    private void compressImage(Uri image_uri) {

        //Upload
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("product_photo/" + ""+System.currentTimeMillis());
        storageReference.putFile(image_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                String timeStamp = String.valueOf(System.currentTimeMillis());
                HashMap<Object, String> hashMap = new HashMap<>();
                hashMap.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                hashMap.put("pId", timeStamp);
                hashMap.put("title", "Challenge");
                hashMap.put("des", des.getText().toString());
                hashMap.put("cat", cat.getText().toString());
                hashMap.put("type", type.getText().toString());
                hashMap.put("photo", downloadUri.toString());

                FirebaseDatabase.getInstance().getReference().child("Product").child(timeStamp).setValue(hashMap);

                //title.setText("");
                des.setText("");
                cat.setText("1 vs 1");
                type.setText("Select game");
                catString = "1 vs 1";
                typeString = "";
                roundedImageView.setImageResource(R.drawable.upload_product);
                Snackbar.make(findViewById(R.id.main), "Challenge Posted", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                Intent intent = new Intent(this, ProductDetailsActivity.class);
                intent.putExtra("pId", timeStamp);
                startActivity(intent);
                finish();

            }
        });

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(findViewById(R.id.main), "Storage permission allowed", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(R.id.main), "Storage permission is required", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            dp_uri = data.getData();
              Picasso.get().load(dp_uri).into(roundedImageView);
        }
        //Location
        /*if (resultCode == Activity.RESULT_OK && requestCode == LOCATION_PICK_CODE && data != null) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            location.setText(feature.text());
        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCatPickPositiveButtonClicked(String[] list, int position) {
        catString = list[position];
        cat.setText(catString);
    }

    @Override
    public void onCatPickNegativeButtonClicked() {

    }

}