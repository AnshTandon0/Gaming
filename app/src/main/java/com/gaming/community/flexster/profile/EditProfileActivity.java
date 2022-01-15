package com.gaming.community.flexster.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.gaming.community.flexster.MainActivity;
import com.gaming.community.flexster.fragment.ProfileFragment;
import com.gaming.community.flexster.phoneAuth.RegisterActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hendraanggrian.appcompat.widget.SocialEditText;
import com.iceteck.silicompressorr.SiliCompressor;
import com.gaming.community.flexster.MediaViewActivity;
import com.gaming.community.flexster.NightMode;
import com.gaming.community.flexster.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    //ID
    ImageView cover,editCover,editDp;
    CircleImageView dp;
    VideoView videoView;
    EditText name,username,loc,link;
    SocialEditText bio;
    ConstraintLayout main;

    //Bottom
    BottomSheetDialog dp_edit,cover_edit;
    LinearLayout upload,delete,video,image,trash;

    //String
    String mDp;
    String getUsername;
    boolean isThere = false;

    //Permission
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int COVER_IMAGE_PICK_CODE = 1002;
    private static final int COVER_VIDEO_PICK_CODE = 1003;
    private static final int PERMISSION_CODE = 1001;

    NightMode sharedPref;

    String privateMessage = "";
    String privateClubs = "";
    String privatepoke = "";
    //Switch sw_message,sw_clubs;
    RadioButton redio_pm_anyone,redio_pm_private,radio_pc_anyone,radio_pc_private,radio_poke_anyone,radio_poke_private;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new NightMode(this);
        if (sharedPref.loadNightModeState()){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Declaring
        cover = findViewById(R.id.cover);
        editCover = findViewById(R.id.editCover);
        editDp = findViewById(R.id.editDp);
        dp = findViewById(R.id.dp);
        videoView = findViewById(R.id.video);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        loc = findViewById(R.id.location);
        link = findViewById(R.id.link);
        main = findViewById(R.id.main);
        /*sw_message = findViewById(R.id.sw_message);
        sw_clubs = findViewById(R.id.sw_clubs);*/
        redio_pm_anyone = findViewById(R.id.redio_pm_anyone);
        redio_pm_private = findViewById(R.id.redio_pm_private);
        radio_pc_anyone = findViewById(R.id.radio_pc_anyone);
        radio_pc_private = findViewById(R.id.radio_pc_private);
        radio_poke_anyone = findViewById(R.id.radio_poke_anyone);
        radio_poke_private = findViewById(R.id.radio_poke_private);

        //Back
        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        //Cam
        if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{
                    Manifest.permission.CAMERA
            }, 99);
        }

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

        /*sw_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (privateMessage.equals("anyone")){
                    privateMessage = "private";
                    Toast.makeText(EditProfileActivity.this, "Players I follow", Toast.LENGTH_SHORT).show();
                }
                else {
                    privateMessage = "anyone";
                    Toast.makeText(EditProfileActivity.this, "Anyone", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        /*sw_clubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (privateClubs.equals("anyone")){
                    privateClubs = "private";
                    Toast.makeText(EditProfileActivity.this, "Players I follow", Toast.LENGTH_SHORT).show();
                }
                else {
                    privateClubs = "anyone";
                    Toast.makeText(EditProfileActivity.this, "Anyone", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        //Firebase
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDp = snapshot.child("photo").getValue().toString();
                String mName = snapshot.child("name").getValue().toString();
                getUsername = snapshot.child("username").getValue().toString();
                String mBio = snapshot.child("bio").getValue().toString();
                String mLocation = snapshot.child("location").getValue().toString();
                String mLink = snapshot.child("link").getValue().toString();
                privateMessage = snapshot.child("privateMessage").getValue().toString();
                privateClubs = snapshot.child("privateClubs").getValue().toString();
                privatepoke = snapshot.child("privatePoke").getValue().toString();

                if (!mDp.isEmpty()){
                    Picasso.get().load(mDp).placeholder(R.drawable.avatar).into(dp);
                }else {
                    //Picasso.get().load(R.drawable.avatar).into(dp);
                    dp.setImageResource(R.drawable.avatar);
                    delete.setVisibility(View.GONE);
                }

                name.setText(mName);
                username.setText(getUsername);
                loc.setText(mLocation);
                bio.setText(mBio);
                link.setText(mLink);

                if (privateMessage.equals("private")){
                    redio_pm_private.setChecked(true);
                    redio_pm_anyone.setChecked(false);
                }
                else {
                    redio_pm_anyone.setChecked(true);
                    redio_pm_private.setChecked(false);
                }

                if (privateClubs.equals("private")){
                    radio_pc_private.setChecked(true);
                    radio_pc_anyone.setChecked(false);
                }
                else {
                    radio_pc_anyone.setChecked(true);
                    radio_pc_private.setChecked(false);
                }

                if (privatepoke.equals("private")){
                    radio_poke_private.setChecked(true);
                    radio_poke_anyone.setChecked(false);
                }
                else {
                    radio_poke_anyone.setChecked(true);
                    radio_poke_private.setChecked(false);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        //Cover
        FirebaseDatabase.getInstance().getReference().child("Cover")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String type = snapshot.child("type").getValue().toString();
                    String uri = snapshot.child("uri").getValue().toString();

                    isThere = true;

                    if (type.equals("image")){
                        Picasso.get().load(uri).placeholder(R.drawable.cover).into(cover);
                        videoView.setVisibility(View.GONE);
                        cover.setVisibility(View.VISIBLE);
                    }
                    else if (type.equals("video")){

                        videoView.setVisibility(View.VISIBLE);
                        cover.setVisibility(View.GONE);
                        videoView.setVideoURI(Uri.parse(uri));
                        videoView.start();
                        videoView.setOnPreparedListener(mp -> {
                            mp.setLooping(true);
                            mp.setVolume(0, 0);
                        });
                        setDimension();

                        videoView.setOnClickListener(v -> {
                            Intent i = new Intent(getApplicationContext(), MediaViewActivity.class);
                            i.putExtra("type", "video");
                            i.putExtra("uri", uri);
                            startActivity(i);
                        });

                    }

                }else {
                    //Picasso.get().load(R.drawable.cover).into(cover);
                    cover.setImageResource(R.drawable.cover);
                    videoView.setVisibility(View.GONE);
                    cover.setVisibility(View.VISIBLE);
                    trash.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar.make(main, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        //Save
        findViewById(R.id.signUp).setOnClickListener(v -> {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            String mName = name.getText().toString().trim();
            String mUsername = username.getText().toString().trim();
            String mBio = bio.getText().toString().trim();
            String mLink = link.getText().toString().trim();
            String mLocation = loc.getText().toString().trim();

            if (mName.isEmpty()){
                Snackbar.make(v, "Enter your name", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }else if (mUsername.isEmpty()){
                Snackbar.make(v, "Enter your username", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }else {
                if (!getUsername.equals(mUsername)){
                    Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").equalTo(mUsername);
                    usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.getChildrenCount()>0){
                                Snackbar.make(v,"Username already exist, try with new one", Snackbar.LENGTH_LONG).show();
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                            }else {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("name", mName);
                                hashMap.put("username", mUsername);
                                hashMap.put("bio", mBio);
                                hashMap.put("location",mLocation);
                                hashMap.put("link",mLink);
                                hashMap.put("privateMessage",privateMessage);
                                hashMap.put("privateClubs",privateClubs);
                                hashMap.put("privatePoke",privatepoke);
                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .updateChildren(hashMap);
                                Snackbar.make(v, "Saved", Snackbar.LENGTH_LONG).show();
                                findViewById(R.id.progressBar).setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Snackbar.make(v,error.getMessage(), Snackbar.LENGTH_LONG).show();
                            findViewById(R.id.progressBar).setVisibility(View.GONE);
                        }
                    });
                }else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("name", mName);
                    hashMap.put("username", mUsername);
                    hashMap.put("bio", mBio);
                    hashMap.put("location",mLocation);
                    hashMap.put("link",mLink);
                    hashMap.put("privateMessage",privateMessage);
                    hashMap.put("privateClubs",privateClubs);
                    hashMap.put("privatePoke",privatepoke);
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(hashMap);
                    Snackbar.make(v, "Saved", Snackbar.LENGTH_LONG).show();
                    findViewById(R.id.progressBar).setVisibility(View.GONE);
                }
            }

        });

        //EditImage
        editDp.setOnClickListener(v -> dp_edit.show());

        //EditCover
        editCover.setOnClickListener(v -> cover_edit.show());

        //Bottom
        edit_dp();
        edit_cover();

    }

    private void edit_cover() {
        if (cover_edit == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.cover_edit, null);
            image = view.findViewById(R.id.image);
            video = view.findViewById(R.id.video);
            trash = view.findViewById(R.id.trash);
            LinearLayout camera = view.findViewById(R.id.camera);
            camera.setOnClickListener(v -> {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent , 130);
            });
            image.setOnClickListener(this);
            video.setOnClickListener(this);
            trash.setOnClickListener(this);
            cover_edit = new BottomSheetDialog(this);
            cover_edit.setContentView(view);
        }
    }

    private void edit_dp() {
        if (dp_edit == null){
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dp_edit, null);
            upload = view.findViewById(R.id.upload);
            delete = view.findViewById(R.id.delete);
            LinearLayout camera = view.findViewById(R.id.camera);
            camera.setOnClickListener(v -> {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent , 120);
            });
            upload.setOnClickListener(this);
            delete.setOnClickListener(this);
            dp_edit = new BottomSheetDialog(this);
            dp_edit.setContentView(view);
        }
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void pickCoverVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent, COVER_VIDEO_PICK_CODE);
    }

    private void pickCoverImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, COVER_IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(main, "Storage permission allowed", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(main, "Storage permission is required", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE && data != null){
            Uri dp_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(dp_uri).into(dp);
            uploadDp(dp_uri);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Snackbar.make(main, "Please wait, uploading...", Snackbar.LENGTH_LONG).show();

        }
        if (resultCode == RESULT_OK && requestCode == 120 && data != null){
            Uri dp_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(dp_uri).into(dp);
            uploadDp(dp_uri);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Snackbar.make(main, "Please wait, uploading...", Snackbar.LENGTH_LONG).show();

        }
        if (resultCode == RESULT_OK && requestCode == 130 && data != null){
            Uri cover_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(cover_uri).into(cover);
            videoView.setVisibility(View.GONE);
            cover.setVisibility(View.VISIBLE);
            uploadCoverImage(cover_uri);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Snackbar.make(main, "Please wait, uploading...", Snackbar.LENGTH_LONG).show();

        }
        if (resultCode == RESULT_OK && requestCode == COVER_IMAGE_PICK_CODE && data != null){
            Uri cover_uri = Objects.requireNonNull(data).getData();
            Picasso.get().load(cover_uri).into(cover);
            videoView.setVisibility(View.GONE);
            cover.setVisibility(View.VISIBLE);
            uploadCoverImage(cover_uri);
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            Snackbar.make(main, "Please wait, uploading...", Snackbar.LENGTH_LONG).show();

        }
        if (resultCode == RESULT_OK && requestCode == COVER_VIDEO_PICK_CODE && data != null){

            Uri video_uri = Objects.requireNonNull(data).getData();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getApplicationContext(), video_uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMilli = Long.parseLong(time);
            retriever.release();

            if (timeInMilli > 7000){
                Snackbar.make(main, "Cover video must be of 7 seconds or less", Snackbar.LENGTH_LONG).show();
            }else {
                videoView.setVisibility(View.VISIBLE);
                cover.setVisibility(View.GONE);
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                videoView.setVideoURI(video_uri);
                videoView.start();
                videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    mp.setVolume(0, 0);
                });
                Snackbar.make(main, "Please wait, uploading...", Snackbar.LENGTH_LONG).show();
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
                new CompressVideo().execute("false",video_uri.toString(),file.getPath());
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("StaticFieldLeak")
    private class CompressVideo extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            String videoPath = null;
            try {
                Uri mUri = Uri.parse(strings[1]);
                videoPath = SiliCompressor.with(EditProfileActivity.this)
                        .compressVideo(mUri,strings[2]);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return videoPath;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            File file = new File(s);
            Uri videoUri = Uri.fromFile(file);
            uploadVideo(videoUri);
        }
    }

    private void uploadVideo(Uri video_uri){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("cover_video/" + ""+System.currentTimeMillis());
        storageReference.putFile(video_uri).addOnSuccessListener(taskSnapshot -> {

            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uri", downloadUri.toString());
                hashMap.put("type", "video");
                if (isThere){
                    FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap);
                    isThere = true;
                }
                Snackbar.make(main, "Cover video updated", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }

        });
    }

    private void uploadCoverImage(Uri cover_uri) {


        StorageReference storageReference = FirebaseStorage.getInstance().getReference("cover_photo/" + ""+System.currentTimeMillis());
        storageReference.putFile(cover_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("uri", downloadUri.toString());
                hashMap.put("type", "image");
                if (isThere){
                    FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(hashMap);
                    isThere = true;
                }

                Snackbar.make(main, "Cover photo updated", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });
    }

    private void uploadDp(Uri dp_uri){

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_photo/" + ""+System.currentTimeMillis());
        storageReference.putFile(dp_uri).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful()) ;
            Uri downloadUri = uriTask.getResult();
            if (uriTask.isSuccessful()){
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("photo", downloadUri.toString());
                FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                Snackbar.make(main, "Profile photo updated", Snackbar.LENGTH_LONG).show();
                findViewById(R.id.progressBar).setVisibility(View.GONE);
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.upload:

                dp_edit.cancel();

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

                break;
            case R.id.delete:

                dp_edit.cancel();

                if (!mDp.isEmpty()){
                    findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(mDp);
                    picRef.delete().addOnCompleteListener(task -> {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("photo", "");
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(hashMap);
                        Snackbar.make(main, "Profile photo deleted", Snackbar.LENGTH_LONG).show();
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                    });
                }

                break;
            case R.id.image:

                cover_edit.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                       pickCoverImage();
                    }
                }
                else {
                    pickCoverImage();
                }

                break;
            case R.id.video:

                cover_edit.cancel();

                //Check Permission
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permissions, PERMISSION_CODE);
                    }
                    else {
                        pickCoverVideo();
                    }
                }
                else {
                    pickCoverVideo();
                }

                break;

            case R.id.trash:

                cover_edit.cancel();

                FirebaseDatabase.getInstance().getReference().child("Cover").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String uri = snapshot.child("uri").getValue().toString();

                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri);
                        picRef.delete().addOnCompleteListener(task -> {
                            snapshot.getRef().removeValue();
                            Snackbar.make(main, "Cover deleted", Snackbar.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(main, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

                break;
        }
    }

    private void setDimension() {

        float videoProportion = getVideoProportion();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        float screenProportion = (float) screenHeight / (float) screenWidth;
        android.view.ViewGroup.LayoutParams lp = videoView.getLayoutParams();

        if (videoProportion < screenProportion) {
            lp.height= screenHeight;
            lp.width = (int) ((float) screenHeight / videoProportion);
        } else {
            lp.width = screenWidth;
            lp.height = (int) ((float) screenWidth * videoProportion);
        }
        videoView.setLayoutParams(lp);
    }

    private float getVideoProportion(){
        return 1.5f;
    }

    @Override
    public void onBackPressed() {
        //startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();
    }
}