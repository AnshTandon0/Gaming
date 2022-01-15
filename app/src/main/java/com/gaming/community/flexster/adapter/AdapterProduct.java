package com.gaming.community.flexster.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.marketPlace.ProductDetailsActivity;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelProduct;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.MyHolder>{

    final Context context;
    final List<ModelProduct> userList;

    public AdapterProduct(Context context, List<ModelProduct> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.product_list, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        Picasso.get().load(userList.get(position).getPhoto()).into(holder.image);
        holder.title.setText(userList.get(position).getCat()+" "+userList.get(position).getTitle());
        holder.game_name.setText(userList.get(position).getType());
        holder.des.setText(userList.get(position).getDes());

        //*****AdminInfo1*****
        FirebaseDatabase.getInstance().getReference("Admin").child(userList.get(position).getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.admin.setVisibility(View.VISIBLE);
                        } else {
                            holder.admin.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //PostCount1
        PostCount.getfightlevel(userList.get(position).getId(), holder.txt_fight_count);
        PostCount.getengagelevel(userList.get(position).getId(), holder.txt_post_count);

        //*****User1 Info*****
        FirebaseDatabase.getInstance().getReference().child("Users").child(userList.get(position).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        /*if (!snapshot.child("photo").getValue().toString().isEmpty()) Picasso.get()
                                .load(snapshot.child("photo").getValue().toString()).into(holder.dpiv1);*/

                        holder.user_name.setText(snapshot.child("name").getValue().toString());

                        //Verify
                        if (snapshot.child("verified").getValue().toString().equals("yes")){
                            holder.verified.setVisibility(View.VISIBLE);
                        }
                        else {
                            holder.verified.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra("pId", userList.get(position).getpId());
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        final ImageView image;
        final TextView title;
        final TextView game_name;
        TextView user_name,des;
        ImageView verified,admin;
        TextView txt_post_count,txt_fight_count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            game_name = itemView.findViewById(R.id.game_name);
            user_name = itemView.findViewById(R.id.user_name);
            des = itemView.findViewById(R.id.des);
            verified = itemView.findViewById(R.id.verified);
            admin = itemView.findViewById(R.id.admin);
            txt_post_count = itemView.findViewById(R.id.txt_post_count);
            txt_fight_count = itemView.findViewById(R.id.txt_fight_count);

        }

    }
}
