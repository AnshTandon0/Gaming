package com.gaming.community.flexster.group;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterDialogGameList;
import com.gaming.community.flexster.adapter.CustStickerAdapter;
import com.gaming.community.flexster.adapter.ShowCustSctickerAdapter;
import com.gaming.community.flexster.model.CustomeGifModel;
import com.gaming.community.flexster.model.ModelUser;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AddStickerActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    RelativeLayout main;
    ImageView back;
    TextView txt_add_new_sticker;
    RecyclerView rec_sticker;
    private RecyclerView.LayoutManager mlayoutManager;
    ArrayList<CustomeGifModel> customeGifModels = new ArrayList<>();
    CustStickerAdapter custStickerAdapter;
    int cut_sicker_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sticker);

        main = findViewById(R.id.main);
        back = findViewById(R.id.back);
        txt_add_new_sticker = findViewById(R.id.txt_add_new_sticker);
        rec_sticker = findViewById(R.id.rec_sticker);

        getCustomeGif();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        txt_add_new_sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cut_sicker_size >= 20){
                    Toast.makeText(AddStickerActivity.this, "Maximum 20 stickers allowed", Toast.LENGTH_SHORT).show();
                }
                else {

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
                }

            }
        });

    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/gif");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            Uri dp_uri = Objects.requireNonNull(data).getData();
            sendImage(dp_uri);
            Snackbar.make(main, "Please wait, Sending...", Snackbar.LENGTH_LONG).show();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImage(Uri dp_uri){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("custom_sticker/" + ""+System.currentTimeMillis());
        storageReference.putFile(dp_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){

                String stamp = ""+System.currentTimeMillis();
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uri", downloadUri.toString());
                hashMap.put("type", "image");
                hashMap.put("timestamp", stamp);

                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("CustomSticker")
                        .child(stamp)
                        .setValue(hashMap);

            }
        });
    }

    private void getCustomeGif(){
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("CustomSticker")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        customeGifModels.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            CustomeGifModel md = ds.getValue(CustomeGifModel.class);
                            customeGifModels.add(md);
                        }

                        cut_sicker_size = customeGifModels.size();

                        custStickerAdapter = new CustStickerAdapter(AddStickerActivity.this,customeGifModels);
                        mlayoutManager = new LinearLayoutManager(AddStickerActivity.this, LinearLayoutManager.VERTICAL, false);
                        rec_sticker.setLayoutManager(mlayoutManager);
                        rec_sticker.setItemAnimator(new DefaultItemAnimator());
                        rec_sticker.setAdapter(custStickerAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

}