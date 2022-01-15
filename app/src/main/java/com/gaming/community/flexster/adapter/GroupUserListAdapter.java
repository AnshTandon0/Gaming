package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.group.PostClubFightLogActivity;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

public class GroupUserListAdapter extends RecyclerView.Adapter<GroupUserListAdapter.MyHolder>{

    final Context context;
    final List<ModelUser> userList;
    String grp_id;
    private final String myGroupRole;
    private boolean notify = false;
    private RequestQueue requestQueue;

    public GroupUserListAdapter(Context context, List<ModelUser> userList, String grp_id,String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.grp_id=grp_id;
        this.myGroupRole=myGroupRole;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(userList.get(position).getName());

        requestQueue = Volley.newRequestQueue(context);


        Log.e("userprofileisss",userList.get(position).getId());
        //holder.username.setText(userList.get(position).getUsername());
        if (userList.get(position).getPhoto().isEmpty()){
            //Picasso.get().load(R.drawable.avatar).into(holder.dp);
            holder.dp.setImageResource(R.drawable.avatar);
        }else {
            Picasso.get().load(userList.get(position).getPhoto()).into(holder.dp);
        }

        checkAlreadyExists(userList.get(position),holder,userList.get(position).getUsername(),position);

       // if (userList.get(position).getVerified().equals("yes"))  holder.verified.setVisibility(View.VISIBLE);
        holder.ll_g_engagement.setVisibility(View.GONE);
        holder.ll_g_skill.setVisibility(View.GONE);
        holder.ll_c_engagement.setVisibility(View.VISIBLE);

        //getGroupUserLevel
        PostCount.getgroupuserlevel(grp_id,userList.get(position).getId(),holder.txt_clan_post_count);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserProfileActivity.class);
            intent.putExtra("hisUID", userList.get(position).getId());
            context.startActivity(intent);
        });

        //check Scrimster
        FirebaseDatabase.getInstance().getReference().child("Groups").child(grp_id)
                .child("Participants").child(userList.get(position).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            if (snapshot.child("scrimster").getValue().equals("yes")){
                                holder.img_scrimster.setVisibility(View.VISIBLE);
                            }
                            else {
                                holder.img_scrimster.setVisibility(View.GONE);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        if(myGroupRole.equals("co-owner")||myGroupRole.equals("owner")||myGroupRole.equals("mod"))
        {
            holder.options.setVisibility(View.VISIBLE);
        }

        ModelUser modelUser = userList.get(position);

        holder.options.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
            ref.child(grp_id).child("Participants").child(userList.get(position).getId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                String hisPrevRole = ""+snapshot.child("role").getValue();

                                Dialog dialog = new Dialog(context,R.style.CustomDialog);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(true);
                                dialog.setContentView(R.layout.design_role_assign);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                //dialog.show();

                                TextView txt_co_owner,txt_mod,txt_senior,txt_member,txt_vip_guest,txt_gust,txt_scrimster,txt_kick_player;
                                View line_1,line_2;

                                txt_co_owner = dialog.findViewById(R.id.txt_co_owner);
                                txt_mod = dialog.findViewById(R.id.txt_mod);
                                txt_senior = dialog.findViewById(R.id.txt_senior);
                                txt_member = dialog.findViewById(R.id.txt_member);
                                txt_vip_guest = dialog.findViewById(R.id.txt_vip_guest);
                                txt_gust = dialog.findViewById(R.id.txt_gust);
                                txt_scrimster = dialog.findViewById(R.id.txt_scrimster);
                                txt_kick_player = dialog.findViewById(R.id.txt_kick_player);
                                line_1 = dialog.findViewById(R.id.line_1);
                                line_2 = dialog.findViewById(R.id.line_2);

                                FirebaseDatabase.getInstance().getReference().child("Groups").child(grp_id)
                                        .child("Participants").child(userList.get(position).getId())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()){

                                                    if (snapshot.child("scrimster").getValue().equals("yes")){
                                                        txt_scrimster.setText("Remove Scrimster");
                                                    }
                                                    else {
                                                        txt_scrimster.setText("Assign Scrimster");
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                if (myGroupRole.equals("owner")){

                                    if (hisPrevRole.equals("co-owner")){
                                        txt_co_owner.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("mod")){
                                        txt_mod.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("senior")){
                                        txt_senior.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("member")){
                                        txt_member.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("vip guest")){
                                        txt_vip_guest.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("guest")){
                                        txt_gust.setVisibility(View.GONE);
                                        dialog.show();
                                    }

                                }
                                else if (myGroupRole.equals("co-owner")){

                                    txt_co_owner.setVisibility(View.GONE);
                                    txt_scrimster.setVisibility(View.GONE);
                                    line_1.setVisibility(View.GONE);

                                    if (hisPrevRole.equals("mod")){
                                        txt_mod.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("senior")){
                                        txt_senior.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("member")){
                                        txt_member.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("vip guest")){
                                        txt_vip_guest.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("guest")){
                                        txt_gust.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else {
                                        Toast.makeText(context, "You are not authorised!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else if (myGroupRole.equals("mod")){

                                    txt_co_owner.setVisibility(View.GONE);
                                    txt_scrimster.setVisibility(View.GONE);
                                    line_1.setVisibility(View.GONE);
                                    txt_mod.setVisibility(View.GONE);

                                    if (hisPrevRole.equals("senior")){
                                        txt_senior.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("member")){
                                        txt_member.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("vip guest")){
                                        txt_vip_guest.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else if (hisPrevRole.equals("guest")){
                                        txt_gust.setVisibility(View.GONE);
                                        dialog.show();
                                    }
                                    else {
                                        Toast.makeText(context, "You are not authorised!", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else {
                                    Toast.makeText(context, "You are not authorised!", Toast.LENGTH_SHORT).show();
                                }

                                txt_co_owner.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makeco_owner(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_mod.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makemod(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_senior.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makesenior(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_member.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makemember(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_vip_guest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makevip(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_gust.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makeguest(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_kick_player.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeParticipants(modelUser, holder);
                                        dialog.dismiss();
                                    }
                                });

                                txt_scrimster.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (txt_scrimster.getText().toString().equals("Assign Scrimster")){
                                            makeScrimster(modelUser, holder);
                                            dialog.dismiss();
                                        }
                                        else {
                                            removeScrimster(modelUser, holder);
                                            dialog.dismiss();
                                        }

                                    }
                                });

                                /*AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Choose Option");
                                    if (myGroupRole.equals("owner"))
                                    {
                                        Log.e("amicreatrore",myGroupRole);
                                        if (hisPrevRole.equals("co-owner"))
                                        {
                                            options = new String[]{"Make Guest","Vip - guest","Make mod","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    //removeAdmin(modelUser, holder);
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makesenior(modelUser, holder);

                                                }
                                                else if(which == 4)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("mod"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make co-owner","Make Guest","Vip - guest","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    makeco_owner(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else if(which == 4)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("senior"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make co-owner","Make Guest","Vip - guest","Make mod","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    makeco_owner(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 4)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("member"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make co-owner","Make Guest","Vip - guest","Make mod","Make senior", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    makeco_owner(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 4)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("vip guest"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make co-owner","Make Guest","Make mod","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    makeco_owner(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else if(which == 4)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("guest"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make co-owner","Vip - guest","Make mod","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if (which == 0) {
                                                    makeco_owner(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else if(which == 4)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }


                                    }
                                    else if (myGroupRole.equals("co-owner"))
                                    {
                                        Log.e("amicreatrore",myGroupRole);
                                        if (hisPrevRole.equals("mod")) {
                                        Log.e("amicreatroreparti",myGroupRole);
                                        options = new String[]{"Make Guest","Vip - guest","Make senior","Make member", "Kick player"};
                                        builder.setItems(options, (dialog, which) -> {
                                            if(which == 0)
                                            {
                                                makeguest(modelUser, holder);
                                            }
                                            else if(which == 1)
                                            {
                                                makevip(modelUser, holder);
                                            }
                                            else if(which ==2)
                                            {
                                                makesenior(modelUser, holder);
                                            }
                                            else if(which == 3)
                                            {
                                                makemember(modelUser, holder);
                                            }
                                            else {
                                                removeParticipants(modelUser, holder);
                                            }
                                        }).show();
                                    }
                                        else if (hisPrevRole.equals("senior"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make Guest","Vip - guest","Make mod","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if(which == 0)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("member"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make Guest","Vip - guest","Make mod","Make senior", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if(which == 0)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("vip guest"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Make Guest","Make mod","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {
                                                if(which == 0)
                                                {
                                                    makeguest(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }
                                        else if (hisPrevRole.equals("guest"))
                                        {
                                            Log.e("amicreatroreparti",myGroupRole);
                                            options = new String[]{"Vip - guest","Make mod","Make senior","Make member", "Kick player"};
                                            builder.setItems(options, (dialog, which) -> {

                                                if(which == 0)
                                                {
                                                    makevip(modelUser, holder);
                                                }
                                                else if(which == 1)
                                                {
                                                    makemod(modelUser, holder);
                                                }
                                                else if(which == 2)
                                                {
                                                    makesenior(modelUser, holder);
                                                }
                                                else if(which == 3)
                                                {
                                                    makemember(modelUser, holder);
                                                }
                                                else {
                                                    removeParticipants(modelUser, holder);
                                                }
                                            }).show();
                                        }

                                 }
                                    else if (myGroupRole.equals("mod"))
                                    {
                                            if (hisPrevRole.equals("senior"))
                                            {
                                                Log.e("amicreatroreparti",myGroupRole);
                                                options = new String[]{"Make Guest","Vip - guest","Make member"};
                                                builder.setItems(options, (dialog, which) -> {
                                                    if(which == 0)
                                                    {
                                                        makeguest(modelUser, holder);
                                                    }
                                                    else if(which == 1)
                                                    {
                                                        makevip(modelUser, holder);
                                                    }
                                                    else if(which == 2)
                                                    {
                                                        makemember(modelUser, holder);
                                                    }
                                                }).show();
                                            }
                                            else if (hisPrevRole.equals("member"))
                                            {
                                                Log.e("amicreatroreparti",myGroupRole);
                                                options = new String[]{"Make Guest","Vip - guest","Make senior"};
                                                builder.setItems(options, (dialog, which) -> {
                                                    if(which == 0)
                                                    {
                                                        makeguest(modelUser, holder);
                                                    }
                                                    else if(which == 1)
                                                    {
                                                        makevip(modelUser, holder);
                                                    }
                                                    else if(which == 2)
                                                    {
                                                        makesenior(modelUser, holder);
                                                    }

                                                }).show();
                                            }
                                            else if (hisPrevRole.equals("vip guest"))
                                            {
                                                Log.e("amicreatroreparti",myGroupRole);
                                                options = new String[]{"Make Guest","Make senior","Make member", "Kick player"};
                                                builder.setItems(options, (dialog, which) -> {
                                                    if(which == 0)
                                                    {
                                                        makeguest(modelUser, holder);
                                                    }
                                                    else if(which == 1)
                                                    {
                                                        makesenior(modelUser, holder);
                                                    }
                                                    else if(which == 2)
                                                    {
                                                        makemember(modelUser, holder);
                                                    }
                                                    else {
                                                        removeParticipants(modelUser, holder);
                                                    }
                                                }).show();
                                            }
                                            else if (hisPrevRole.equals("guest"))
                                            {
                                                Log.e("amicreatroreparti",myGroupRole);
                                                options = new String[]{"Vip - guest","Make senior","Make member", "Kick player"};
                                                builder.setItems(options, (dialog, which) -> {

                                                    if(which == 0)
                                                    {
                                                        makevip(modelUser, holder);
                                                    }
                                                    else if(which == 1)
                                                    {
                                                        makesenior(modelUser, holder);
                                                    }
                                                    else if(which == 2)
                                                    {
                                                        makemember(modelUser, holder);
                                                    }
                                                    else {
                                                        removeParticipants(modelUser, holder);
                                                    }
                                                }).show();
                                            }
                                    }*/

                                //*************************************************************************************
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Add Participant")
                                        .setMessage("Add this user in this group?")
                                        .setPositiveButton("Add", (dialog, which) -> addParticipants(modelUser, holder)).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        });

        //UserInfo
        FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Time
                if (Objects.requireNonNull(snapshot.child("status").getValue()).toString().equals("online")) holder.online.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final CircleImageView dp;
        final ImageView verified,img_role,options;
        final ImageView online;
        final TextView name;
        final TextView username;
        LinearLayout ll_g_engagement,ll_g_skill,ll_c_engagement;
        TextView txt_clan_post_count;
        ImageView img_scrimster;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            online = itemView.findViewById(R.id.imageView2);
            img_role=itemView.findViewById(R.id.img_role);
            options=itemView.findViewById(R.id.options);
            ll_g_engagement = itemView.findViewById(R.id.ll_g_engagement);
            ll_g_skill = itemView.findViewById(R.id.ll_g_skill);
            ll_c_engagement = itemView.findViewById(R.id.ll_c_engagement);
            txt_clan_post_count = itemView.findViewById(R.id.txt_clan_post_count);
            img_scrimster = itemView.findViewById(R.id.img_scrimster);

        }

    }


    private void makevip(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "vip guest");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned VIP-Guest", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you vip-gust");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void makemod(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","mod");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "mod");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Mod", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you mod");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void makesenior(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "senior");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Senior", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you senior");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void makemember(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "member");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Member", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you member");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void makeguest(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "guest");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Guest", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you guest");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addParticipants(ModelUser modelUser, MyHolder holder) {
        String timestamp = ""+System.currentTimeMillis();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", modelUser.getId());
        hashMap.put("role", "guest");
        hashMap.put("scrimster", "no");
        hashMap.put("timestamp", ""+timestamp);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).setValue(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Player added", Toast.LENGTH_SHORT).show());
        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Let's go!",modelUser.getId(), Objects.requireNonNull(user).getName(), "added you to club");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void makeco_owner(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","co-owner");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "co-owner");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Co-Owner", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you co-owner");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private void makeScrimster(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("scrimster", "yes");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Assigned Scrimster", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Made you scrimster");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeScrimster(ModelUser modelUser, MyHolder holder) {
        Log.e("Makingadmin","vip");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("scrimster", "no");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Remove Scrimster", Toast.LENGTH_SHORT).show());

        notify = true;
        FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                if (notify){
                    //sendNotification("Congrats!",modelUser.getId(), Objects.requireNonNull(user).getName(), "Removed scrimster");
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void removeParticipants(ModelUser modelUser, MyHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");

        String stamp = ""+System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("msg", "");
        hashMap.put("type", "text");
        hashMap.put("timestamp", stamp);
        hashMap.put("replayId","");
        hashMap.put("replayMsg","");
        hashMap.put("replayUserId","");
        hashMap.put("creater_win_id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("win_log_msg","Kicked "+modelUser.getName()+" from the club");
        hashMap.put("win_post_id","");
        hashMap.put("win_type","");

        FirebaseDatabase.getInstance().getReference("Groups").child(grp_id).child("Message").child(stamp)
                .setValue(hashMap);

        ref.child(grp_id).child("Participants").child(modelUser.getId()).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Player removed from the club", Toast.LENGTH_SHORT).show());
    }


    private void removeAdmin(ModelUser modelUser, MyHolder holder) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("role", "guest");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId()).updateChildren(hashMap)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Admin removed", Toast.LENGTH_SHORT).show());
    }


    private void checkAlreadyExists(ModelUser modelUser, MyHolder holder,String mUsername,int position) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(grp_id).child("Participants").child(modelUser.getId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            String hisRole = ""+snapshot.child("role").getValue();
                            //holder.username.setText(mUsername + " - " +hisRole);
                            if (hisRole.equals("owner")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_leader);
                                holder.username.setText("Owner");
                            }
                            else if (hisRole.equals("co-owner")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_co_leader);
                                holder.username.setText("Co-Owner");
                            }
                            else if (hisRole.equals("vip guest")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_vip);
                                holder.username.setText("VIP-Guest");
                            }
                            else if (hisRole.equals("guest")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_guest);
                                holder.username.setText("Guest");
                            }
                            else if (hisRole.equals("member")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_member);
                                holder.username.setText("Member");
                            }
                            else if (hisRole.equals("senior")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_tester);
                                holder.username.setText("Senior");
                            }
                            else if (hisRole.equals("mod")){
                                holder.img_role.setVisibility(View.VISIBLE);
                                holder.img_role.setImageResource(R.drawable.ic_captain);
                                holder.username.setText("Mod");
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void sendNotification(final String title,final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, title, hisId, "profile", R.drawable.ic_push_notification);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse%s", response.toString()), error -> Timber.d("onResponse%s", error.toString())){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAALM19fr0:APA91bFu8hZyEWAUhRieVN8SHIkt5wV_Kb5f4aar4-Zang3fmD3BbdbqzxP4wNFOGx_C2Mc0fxNtYg4JCvVBDQUU3C7b-pkX4DGfMA5v93FIYtFKQ96Opb0ATQHR5lKoNVitdV9L8oNo");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /*private void sendNotification(final String title,final String hisId, final String name,final String message){
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), name + " " + message, title, hisId, "profile", R.drawable.ic_push_notification);
                    assert token != null;
                    Sender sender = new Sender(data, token.getToken());
                    try {
                        JSONObject jsonObject = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", jsonObject, response -> Timber.d("onResponse%s", response.toString()), error -> Timber.d("onResponse%s", error.toString())){
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json");
                                headers.put("Authorization", "key=AAAALM19fr0:APA91bFu8hZyEWAUhRieVN8SHIkt5wV_Kb5f4aar4-Zang3fmD3BbdbqzxP4wNFOGx_C2Mc0fxNtYg4JCvVBDQUU3C7b-pkX4DGfMA5v93FIYtFKQ96Opb0ATQHR5lKoNVitdV9L8oNo");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }*/

}
