package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaming.community.flexster.OnSelectedReplayItem;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.chat.FightLogPostCommentActivity;
import com.gaming.community.flexster.group.ClubFightLogCommentActivity;
import com.gaming.community.flexster.group.CommentGroupActivity;
import com.gaming.community.flexster.group.GroupFightLogCommentActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.api.staticmap.v1.MapboxStaticMap;
import com.mapbox.api.staticmap.v1.StaticMapCriteria;
import com.mapbox.geojson.Point;
import com.gaming.community.flexster.MediaViewActivity;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.group.GroupChatActivity;
import com.gaming.community.flexster.meeting.MeetingActivity;
import com.gaming.community.flexster.model.ModelGroupChat;
import com.gaming.community.flexster.post.CommentActivity;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.gaming.community.flexster.reel.ViewReelActivity;
import com.gaming.community.flexster.search.SearchActivity;
import com.gaming.community.flexster.story.ChatStoryViewActivity;
import com.gaming.community.flexster.story.HighViewActivity;
import com.gaming.community.flexster.watchParty.StartPartyActivity;
import com.gaming.community.flexster.watchParty.StartYouTubeActivity;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.gaming.community.flexster.group.GroupChatActivity.getGroupId;

@SuppressWarnings("ALL")
public class AdapterGroupChat extends RecyclerView.Adapter<AdapterGroupChat.MyHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final List<ModelGroupChat> modelChats;
    String postType;

    String groupId = "";
    OnSelectedReplayItem onSelectedReplayItem;
    String user_name = "";
    RecyclerView recyclerView;

    MyHolder gholder;
    String myid="";
    DatabaseReference databaseReference =  FirebaseDatabase.getInstance()
            .getReference("Groups")
            .child(getGroupId())
            .child("Message");
    Query query = databaseReference.limitToLast(1000).orderByChild("timestamp");

    public AdapterGroupChat(Context context, List<ModelGroupChat> modelChats,String groupId,OnSelectedReplayItem onSelectedReplayItem,RecyclerView recyclerView) {
        this.context = context;
        this.modelChats = modelChats;
        this.groupId = groupId;
        this.onSelectedReplayItem = onSelectedReplayItem;
        this.recyclerView=recyclerView;
        query.addChildEventListener(new ChildGroupChatEventListener());
        myid=FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    class ChildGroupChatEventListener implements ChildEventListener{

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            ModelGroupChat modelGroupChat = snapshot.getValue(ModelGroupChat.class);
            modelChats.add(modelGroupChat);
            notifyDataSetChanged();
            getItemCount();
            recyclerView.smoothScrollToPosition(modelChats.size()-1);

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            String key = snapshot.getKey();
            for (ModelGroupChat md : modelChats){
                if (key.equals(md.getTimestamp())){
                    modelChats.remove(md);
                    notifyDataSetChanged();
                    getItemCount();
                    break;
                }
            }

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    }

    @NonNull
    @Override
    public AdapterGroupChat.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.group_chat_right_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterGroupChat.MyHolder holder, final int position) {

        //getReplaydmsg
        if(!modelChats.get(position).getReplayId().equals("")) {
            holder.ll_replay_msg.setVisibility(View.VISIBLE);
            holder.txt_replay_msg.setText(modelChats.get(position).getReplayMsg());

            //getReplayUserInfo
            FirebaseDatabase.getInstance().getReference().child("Users").child(modelChats.get(position).getReplayUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            holder.txt_replay_user.setText(snapshot.child("name").getValue().toString()+": ");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            //getReplayUserRole
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(groupId).child("Participants").child(modelChats.get(position).getReplayUserId())
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String hisRole = ""+snapshot.child("role").getValue();
                                //holder.username.setText(mUsername + " - " +hisRole);
                                if (hisRole.equals("owner")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#F20694"));
                                }
                                else if (hisRole.equals("co-owner")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#F20694"));
                                }
                                else if (hisRole.equals("vip guest")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#7E43FC"));
                                }
                                else if (hisRole.equals("guest")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#444444"));
                                }
                                else if (hisRole.equals("member")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#12BEB4"));
                                }
                                else if (hisRole.equals("senior")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#75AB30"));
                                }
                                else if (hisRole.equals("mod")){
                                    holder.txt_replay_user.setTextColor(Color.parseColor("#FF6B00"));
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            //*****************

        }
        else {
            holder.ll_replay_msg.setVisibility(View.GONE);
        }

        //chkEEmojis
        chkEmojis(holder,modelChats.get(position).getTimestamp());

        /*holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( modelChats.get(position).getType().equalsIgnoreCase("meet") ) {
                    Intent intent21 = new Intent(context, MeetingActivity.class);
                    String text = modelChats.get(position).getMsg();
                    String a[] = text.split(" " , 3);
                    intent21.putExtra("meet", a[2]);
                    context.startActivity(intent21);
                }
            }
        });*/

        if (modelChats.get(position).getType().equals("text")){
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setLinkText(modelChats.get(position).getMsg());

            holder.text.setOnLinkClickListener((i, s) -> {
                if (i == 1){

                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("hashtag", s);
                    context.startActivity(intent);

                }
                else if (i == 2){
                    String username = s.replaceFirst("@","");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = ref.orderByChild("username").equalTo(username.trim());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot ds : snapshot.getChildren()){
                                    String id = ds.child("id").getValue().toString();
                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                        Snackbar.make(holder.itemView,"It's you", Snackbar.LENGTH_LONG).show();
                                    }else {
                                        Intent intent = new Intent(context, UserProfileActivity.class);
                                        intent.putExtra("hisUID", id);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }
                                }
                            }else {
                                Snackbar.make(holder.itemView,"Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Snackbar.make(holder.itemView,error.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                else if (i == 16){
                    if (!s.startsWith("https://") && !s.startsWith("http://")){
                        s = "http://" + s;
                    }
                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    context.startActivity(openUrlIntent);
                }
                else if (i == 4){
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                    context.startActivity(intent);
                }
                else if (i == 8){
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    context.startActivity(intent);

                }
            });

        }
        else if (modelChats.get(position).getType().equals("image")){
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText("Sent a photo");
            holder.media.setVisibility(View.VISIBLE);
            holder.media_layout.setVisibility(View.VISIBLE);
            Picasso.get().load(modelChats.get(position).getMsg()).into(holder.media);
        }
        else if (modelChats.get(position).getType().equals("gif")){
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText("Sent a sticker");
            holder.sticker_media.setVisibility(View.VISIBLE);
            holder.media_layout.setVisibility(View.VISIBLE);
            Glide.with(context).load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.sticker_media);
        }
        else if (modelChats.get(position).getType().equals("cust_gif")){
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText(modelChats.get(position).getMsg());
            holder.sticker_media.setVisibility(View.VISIBLE);
            holder.media_layout.setVisibility(View.VISIBLE);
            Glide.with(context).load(modelChats.get(position).getMsg()).thumbnail(0.1f).into(holder.sticker_media);
        }
        
        //More
        holder.itemView.setOnClickListener(v -> {
            more_bottom(holder, position);
            gholder = holder;
            //reel_options.show();
            //return false;
        });

        holder.text.setOnLongClickListener(v -> {
            more_bottom(holder, position);
            gholder = holder;
            //reel_options.show();
            return false;
        });

        holder.media.setOnLongClickListener(v -> {
            more_bottom(holder, position);
            //reel_options.show();
            return false;
        });

        //Click
        holder.media_layout.setOnClickListener(v -> {

            switch (modelChats.get(position).getType()) {

                case "image":

                    Intent intent = new Intent(context, MediaViewActivity.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent);

                    break;
            }

        });

        holder.media.setOnClickListener(v -> {

            switch (modelChats.get(position).getType()) {

                case "image":

                    Intent intent = new Intent(context, MediaViewActivity.class);
                    intent.putExtra("type", "image");
                    intent.putExtra("uri", modelChats.get(position).getMsg());
                    context.startActivity(intent);

                    break;
            }

        });

        holder.seen.setVisibility(View.GONE);


        //UserInfo
        FirebaseDatabase.getInstance().getReference().child("Users").child(modelChats.get(position).getSender())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.sender_name.setText(snapshot.child("name").getValue().toString());
                //user_name.add(snapshot.child("name").getValue().toString());
                if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.dp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //getGroupUserLevel
        PostCount.getgroupuserlevel(groupId,modelChats.get(position).getSender(),holder.txt_clan_post_count);

        //getUserRole
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelChats.get(position).getSender())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            String hisRole = ""+snapshot.child("role").getValue();
                            String scrimster = ""+snapshot.child("scrimster").getValue();

                            if (scrimster.equals("yes")){
                                holder.img_scrimster.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.img_scrimster.setVisibility(View.GONE);
                            }

                            if (hisRole.equals("owner")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_leader);
                                holder.sender_name.setTextColor(Color.parseColor("#F20694"));
                            }
                            else if (hisRole.equals("co-owner")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_co_leader);
                                holder.sender_name.setTextColor(Color.parseColor("#F20694"));
                            }
                            else if (hisRole.equals("vip guest")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_vip);
                                holder.sender_name.setTextColor(Color.parseColor("#7E43FC"));
                            }
                            else if (hisRole.equals("guest")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_guest);
                                holder.sender_name.setTextColor(Color.parseColor("#444444"));
                            }
                            else if (hisRole.equals("member")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_member);
                                holder.sender_name.setTextColor(Color.parseColor("#12BEB4"));
                            }
                            else if (hisRole.equals("senior")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_tester);
                                holder.sender_name.setTextColor(Color.parseColor("#75AB30"));
                            }
                            else if (hisRole.equals("mod")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_captain);
                                holder.sender_name.setTextColor(Color.parseColor("#FF6B00"));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        //*****************


        if (!modelChats.get(position).getCreater_win_id().isEmpty()){

            String type = modelChats.get(position).getType();

            if (type.equals("cust_gif")){
                if (modelChats.get(position).getWin_log_msg().equals("Sent a custom emoji")){
                    holder.text.setVisibility(View.GONE);
                    holder.text_win.setVisibility(View.VISIBLE);
                    holder.img_win.setVisibility(View.VISIBLE);
                    holder.text_win.setText(modelChats.get(position).getWin_log_msg());
                }
                else {
                    holder.text.setVisibility(View.GONE);
                    holder.text_win.setVisibility(View.VISIBLE);
                    holder.img_win.setVisibility(View.GONE);
                    holder.text_win.setText(modelChats.get(position).getWin_log_msg());
                    holder.text_win.setTextColor(Color.parseColor("#212830"));
                }
            }
            else {
                holder.text.setVisibility(View.GONE);
                holder.text_win.setVisibility(View.VISIBLE);
                holder.img_win.setVisibility(View.VISIBLE);
                holder.text_win.setText(modelChats.get(position).getWin_log_msg());
            }

        }
        else {
            holder.text_win.setVisibility(View.GONE);
            holder.img_win.setVisibility(View.GONE);
        }

        holder.text_win.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (modelChats.get(position).getWin_type().equals("group_fight_log")){
                    Intent intent = new Intent(context, GroupFightLogCommentActivity.class);
                    intent.putExtra("group_id",modelChats.get(position).getCreater_win_id());
                    intent.putExtra("postID", modelChats.get(position).getWin_post_id());
                    context.startActivity(intent);
                }
                else if (modelChats.get(position).getWin_type().equals("user_fight_log")){
                    Intent intent = new Intent(context, FightLogPostCommentActivity.class);
                    intent.putExtra("user_id",modelChats.get(position).getCreater_win_id());
                    intent.putExtra("postID", modelChats.get(position).getWin_post_id());
                    context.startActivity(intent);
                }
                else if (modelChats.get(position).getWin_type().equals("club_fight_log")){
                    Intent intent = new Intent(context, ClubFightLogCommentActivity.class);
                    intent.putExtra("group_id",modelChats.get(position).getCreater_win_id());
                    intent.putExtra("postID", modelChats.get(position).getWin_post_id());
                    context.startActivity(intent);
                }
                else if (modelChats.get(position).getWin_type().equals("post")){
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("creatore_id",modelChats.get(position).getCreater_win_id());
                    intent.putExtra("postID", modelChats.get(position).getWin_post_id());
                    context.startActivity(intent);
                }
                else if (modelChats.get(position).getWin_type().equals("clubpost")){
                    Intent intent = new Intent(context, CommentGroupActivity.class);
                    intent.putExtra("group",modelChats.get(position).getCreater_win_id());
                    intent.putExtra("postID", modelChats.get(position).getWin_post_id());
                    context.startActivity(intent);
                }
                else if (modelChats.get(position).getWin_type().equals("meet")){
                    Intent intent21 = new Intent(context, MeetingActivity.class);
                    intent21.putExtra("meet", modelChats.get(position).getTimestamp());
                    context.startActivity(intent21);
                }

            }
        });

    }

    private void downloadDoc(Context context, String directoryDownloads, String url, String extension) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri1 = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri1);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, directoryDownloads, extension);
        Objects.requireNonNull(downloadManager).enqueue(request);
    }

    private void more_bottom(MyHolder holder, int position) {
        
        Log.e("aboveif", holder.sender_name.getText().toString());
        user_name = holder.sender_name.getText().toString();
        BottomSheetDialog reel_options;

        reel_options = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_bottom, null);
        reel_options.setContentView(view);
        reel_options.show();

        TextView time = view.findViewById(R.id.time);
        @SuppressLint("SimpleDateFormat")
        String value = new java.text.SimpleDateFormat("dd/MM/yy - h:mm a")
                .format(new java.util.Date(Long.parseLong(modelChats.get(position).getTimestamp()) * 1000));

        time.setText(value);
        time.setVisibility(View.GONE);

        LinearLayout report = view.findViewById(R.id.report);

        ImageView emoji_1 = view.findViewById(R.id.emoji_1);
        ImageView emoji_2 = view.findViewById(R.id.emoji_2);
        ImageView emoji_3 = view.findViewById(R.id.emoji_3);
        ImageView emoji_4 = view.findViewById(R.id.emoji_4);
        ImageView emoji_5 = view.findViewById(R.id.emoji_5);
        ImageView emoji_6 = view.findViewById(R.id.emoji_6);

        emoji_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("1",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        emoji_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("2",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        emoji_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("3",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        emoji_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("4",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        emoji_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("5",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        emoji_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setEmojiCount("6",gholder,modelChats.get(position).getTimestamp(),position);

                reel_options.dismiss();
            }
        });

        report.setOnClickListener(v -> {

            String type = modelChats.get(position).getType();
            String replay_msg = "";

            /*if (type.equals("image")){
                replay_msg = "Sent a photo";
            }
            else if (type.equals("video")){
                replay_msg = "Sent a video clip";
            }
            else if (type.equals("audio")){
                replay_msg = "Sent an audio clip";
            }
            else if (type.equals("gif")){
                replay_msg = "Sent a sticker";
            }*/
            if (!modelChats.get(position).getCreater_win_id().isEmpty()){
                replay_msg = modelChats.get(position).getWin_log_msg();
            }
            else {
                replay_msg = modelChats.get(position).getMsg();
            }

            onSelectedReplayItem.setOnClick(modelChats.get(position).getTimestamp(),
                    replay_msg,
                    user_name,
                    modelChats.get(position).getSender(),
                    position);

            reel_options.dismiss();

        });

        LinearLayout copy = view.findViewById(R.id.copy);
        copy.setOnClickListener(v -> {

            String type = modelChats.get(position).getType();

            if (type.equals("image")){
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "Sent a photo");
                clipboard.setPrimaryClip(clip);
                reel_options.dismiss();
            }
            else if (type.equals("video")){
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "Sent a video clip");
                clipboard.setPrimaryClip(clip);
                reel_options.dismiss();
            }
            else if (type.equals("audio")){
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "Sent an audio clip");
                clipboard.setPrimaryClip(clip);
                reel_options.dismiss();
            }
            else if (type.equals("gif")){
                Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", "Sent a sticker");
                clipboard.setPrimaryClip(clip);
                reel_options.dismiss();
            }
            else {

                if (!modelChats.get(position).getCreater_win_id().isEmpty()){
                    Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", modelChats.get(position).getWin_log_msg());
                    clipboard.setPrimaryClip(clip);
                    reel_options.dismiss();
                }
                else {
                    Snackbar.make(holder.itemView,"Copied", Snackbar.LENGTH_LONG).show();
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("text", modelChats.get(position).getMsg());
                    clipboard.setPrimaryClip(clip);
                    reel_options.dismiss();
                }

            }

        });


        LinearLayout delete = view.findViewById(R.id.delete);

        if (myid.equals(modelChats.get(position).getSender())){
            delete.setVisibility(View.VISIBLE);
        }
        else {
            FirebaseDatabase.getInstance().getReference("Groups")
                    .child(groupId).child("Participants").child(myid)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.exists()){
                                String hisRole = ""+snapshot.child("role").getValue();
                                if (hisRole.equals("owner")){
                                    delete.setVisibility(View.VISIBLE);
                                }
                                else if (hisRole.equals("co-owner")){
                                    delete.setVisibility(View.VISIBLE);
                                }
                                else if (hisRole.equals("mod")){
                                    delete.setVisibility(View.VISIBLE);
                                }
                                else if (hisRole.equals("vip guest")){
                                    delete.setVisibility(View.GONE);
                                }
                                else if (hisRole.equals("guest")){
                                    delete.setVisibility(View.GONE);
                                }
                                else if (hisRole.equals("member")){
                                    delete.setVisibility(View.GONE);
                                }
                                else if (hisRole.equals("senior")){
                                    delete.setVisibility(View.GONE);
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        delete.setOnClickListener(v -> {
            String type = modelChats.get(position).getType();
            if (type.equals("text")){
                reel_options.dismiss();
                Query query =  FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();

                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            /*else if (type.equals("voice_call")){
                reel_options.dismiss();
                Query query = FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();

                            Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("room").equalTo(modelChats.get(position).getTimestamp());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }*/
            /*else if (type.equals("video_call")){
                reel_options.dismiss();
                Query query = FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();

                            Query query = FirebaseDatabase.getInstance().getReference().child("calling").orderByChild("room").equalTo(modelChats.get(position).getTimestamp());
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()){
                                        ds.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }*/
            else if (type.equals("post")){
                reel_options.dismiss();
                Query query = FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            /*else if (type.equals("reel")){
                reel_options.dismiss();
                Query query =  FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            //Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }*/
            else  if (type.equals("gif")){
                reel_options.dismiss();
                Query query =  FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else  if (type.equals("cust_gif")){
                reel_options.dismiss();
                Query query =  FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                            Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else{
                Log.e("deleting","by method");
                reel_options.dismiss();
                FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg()).delete().addOnCompleteListener(task -> {
                    Query query = FirebaseDatabase.getInstance().getReference("Groups").child(getGroupId()).child("Message").orderByChild("timestamp").equalTo(modelChats.get(position).getTimestamp());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()){
                                ds.getRef().removeValue();
                                Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                });
            }

        });

        LinearLayout download = view.findViewById(R.id.download);
        download.setVisibility(View.GONE);

        /*LinearLayout download = view.findViewById(R.id.download);
            download.setOnClickListener(v -> {
                String type = modelChats.get(position).getType();

                if (type.equals("doc")){
                    Snackbar.make(v, "Downloading...", Snackbar.LENGTH_LONG).show();

                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(modelChats.get(position).getMsg());
                    picRef.getDownloadUrl().addOnSuccessListener(uri -> {

                        picRef.getMetadata().addOnSuccessListener(storageMetadata -> {
                            String extension = storageMetadata.getContentType();
                            String url = uri.toString();
                            downloadDoc(context, DIRECTORY_DOWNLOADS, url, extension);
                        });


                    });

                }
                else if (type.equals("video")){
                    Snackbar.make(holder.itemView,"Please wait downloading", Snackbar.LENGTH_LONG).show();
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".mp4");
                    Objects.requireNonNull(downloadManager).enqueue(request);
                }
                else if (type.equals("reel")){
                    Snackbar.make(holder.itemView,"Please wait downloading", Snackbar.LENGTH_LONG).show();
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".mp4");
                    Objects.requireNonNull(downloadManager).enqueue(request);
                }
                else if (type.equals("image")){
                    Snackbar.make(holder.itemView,"Please wait downloading", Snackbar.LENGTH_LONG).show();
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".png");
                    Objects.requireNonNull(downloadManager).enqueue(request);
                }
                else if (type.equals("audio")){
                    Snackbar.make(holder.itemView,"Please wait downloading", Snackbar.LENGTH_LONG).show();
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelChats.get(position).getMsg()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, System.currentTimeMillis() + ".mp3");
                    Objects.requireNonNull(downloadManager).enqueue(request);
                }
                else {
                    Snackbar.make(holder.itemView,"This type of message can't be downloaded", Snackbar.LENGTH_LONG).show();
                }
            });*/

        LinearLayout maximize = view.findViewById(R.id.maximize);
        maximize.setVisibility(View.GONE);

        /*switch (modelChats.get(position).getType()) {
            case "image":
                maximize.setVisibility(View.VISIBLE);
                case "video":
                    maximize.setVisibility(View.VISIBLE);
                    break;
        }*/

        /*if (modelChats.get(position).getType().equals("image") || modelChats.get(position).getType().equals("video")  || modelChats.get(position).getType().equals("doc") || modelChats.get(position).getType().equals("audio") || modelChats.get(position).getType().equals("reel")){
                download.setVisibility(View.VISIBLE);
            }else {
                download.setVisibility(View.GONE);
            }*/

        /*maximize.setOnClickListener(v -> {
                switch (modelChats.get(position).getType()) {
                    case "image":

                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra("type", "image");
                        intent.putExtra("uri", modelChats.get(position).getMsg());
                        context.startActivity(intent);

                        break;
                    case "video":

                        Intent intent1 = new Intent(context, MediaViewActivity.class);
                        intent1.putExtra("type", "video");
                        intent1.putExtra("uri", modelChats.get(position).getMsg());
                        context.startActivity(intent1);

                        break;
                }
            });*/

    }

    @Override
    public int getItemCount() {
        return modelChats.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final TextView seen;
        final TextView sender_name;
        final SocialTextView text,text_win;
        final ImageView media;
        final ImageView post_media;
        final CircleImageView dp;
        final RelativeLayout media_layout;

        //Post
        final TextView name;
        final LinearLayout post;

        //Call
        final LinearLayout main;

        ImageView img_role;
        TextView txt_clan_post_count;

        LinearLayout ll_replay_msg;
        TextView txt_replay_user,txt_replay_msg;

        LinearLayout ll_emojis;
        ImageView emo_1,emo_2,emo_3,emo_4,emo_5,emo_6;
        TextView txt_reacts;

        ImageView sticker_media,img_win,img_scrimster;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("hhhhhhhhhhhhhhhhh_holder", String.valueOf(7));
            text = itemView.findViewById(R.id.text);
            media =  itemView.findViewById(R.id.media);
            name  =  itemView.findViewById(R.id.name);
            post_media =  itemView.findViewById(R.id.post_media);
            seen   =  itemView.findViewById(R.id.seen);
            media_layout =  itemView.findViewById(R.id.media_layout);
            post =  itemView.findViewById(R.id.post);
            main =  itemView.findViewById(R.id.main);
            sender_name =  itemView.findViewById(R.id.sender_name);
            dp =  itemView.findViewById(R.id.dp);

            img_role = itemView.findViewById(R.id.img_role);
            txt_clan_post_count = itemView.findViewById(R.id.txt_clan_post_count);

            ll_replay_msg = itemView.findViewById(R.id.ll_replay_msg);
            txt_replay_user = itemView.findViewById(R.id.txt_replay_user);
            txt_replay_msg = itemView.findViewById(R.id.txt_replay_msg);

            ll_emojis = itemView.findViewById(R.id.ll_emojis);
            emo_1 = itemView.findViewById(R.id.emo_1);
            emo_2 = itemView.findViewById(R.id.emo_2);
            emo_3 = itemView.findViewById(R.id.emo_3);
            emo_4 = itemView.findViewById(R.id.emo_4);
            emo_5 = itemView.findViewById(R.id.emo_5);
            emo_6 = itemView.findViewById(R.id.emo_6);
            txt_reacts = itemView.findViewById(R.id.txt_reacts);

            sticker_media = itemView.findViewById(R.id.sticker_media);

            text_win = itemView.findViewById(R.id.text_win);
            img_win = itemView.findViewById(R.id.img_win);
            img_scrimster = itemView.findViewById(R.id.img_scrimster);

        }

    }

    private void setEmojiCount(String emojitype,MyHolder holder,String msg_id,int position){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emoji_type", emojitype);

        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId)
                .child("Message").child(modelChats.get(position).getTimestamp()).child("Emoji")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(hashMap);

        chkEmojis(holder,msg_id);

    }

    private void chkEmojis(MyHolder holder,String msg_id){

        ArrayList<String> emoji_list = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Message").child(msg_id).child("Emoji")
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    holder.ll_emojis.setVisibility(View.VISIBLE);
                    holder.ll_emojis.setVisibility(View.VISIBLE);
                    holder.emo_1.setVisibility(View.GONE);
                    holder.emo_2.setVisibility(View.GONE);
                    holder.emo_3.setVisibility(View.GONE);
                    holder.emo_4.setVisibility(View.GONE);
                    holder.emo_5.setVisibility(View.GONE);
                    holder.emo_6.setVisibility(View.GONE);

                    for (DataSnapshot ds: snapshot.getChildren()) {
                        String emoji = ds.child("emoji_type").getValue(String.class);
                        emoji_list.add(emoji);

                        if (ds.child("emoji_type").getValue(String.class).equals("1")){
                            holder.emo_1.setVisibility(View.VISIBLE);
                        }
                        else if (ds.child("emoji_type").getValue(String.class).equals("2")){
                            holder.emo_2.setVisibility(View.VISIBLE);
                        }
                        else if (ds.child("emoji_type").getValue(String.class).equals("3")){
                            holder.emo_3.setVisibility(View.VISIBLE);
                        }
                        else if (ds.child("emoji_type").getValue(String.class).equals("4")){
                            holder.emo_4.setVisibility(View.VISIBLE);
                        }
                        else if (ds.child("emoji_type").getValue(String.class).equals("5")){
                            holder.emo_5.setVisibility(View.VISIBLE);
                        }
                        else if (ds.child("emoji_type").getValue(String.class).equals("6")){
                            holder.emo_6.setVisibility(View.VISIBLE);
                        }

                    }

                    holder.txt_reacts.setVisibility(View.VISIBLE);
                    holder.txt_reacts.setText(String.valueOf(emoji_list.size())+" Reacts");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}

