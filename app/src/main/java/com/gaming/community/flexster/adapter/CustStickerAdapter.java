package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.CustomeGifModel;
import com.gaming.community.flexster.send.SendToUserActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CustStickerAdapter extends RecyclerView.Adapter<CustStickerAdapter.CustStickerViewHolder> {

    Context context;
    ArrayList<CustomeGifModel> customeGifModels = new ArrayList<>();


    public CustStickerAdapter(Context context, ArrayList<CustomeGifModel> customeGifModels){
        this.context=context;
        this.customeGifModels=customeGifModels;
    }

    @NonNull
    @Override
    public CustStickerAdapter.CustStickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_edit_cust_sticker_adapter,parent,false);
        return new CustStickerAdapter.CustStickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustStickerAdapter.CustStickerViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(customeGifModels.get(position).getUri()).thumbnail(0.1f).into(holder.img);

        String emoji_name = customeGifModels.get(position).getUri().substring(customeGifModels.get(position).getUri().length() - 10,customeGifModels.get(position).getUri().length());
        holder.emojiname.setText(emoji_name);

        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu remove = new PopupMenu(wrapper, holder.img_menu);
        remove.getMenu().add(Menu.NONE,0,0, "Remove");

        remove.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 0){
                FirebaseStorage.getInstance().getReferenceFromUrl(customeGifModels.get(position).getUri()).delete().addOnCompleteListener(task -> {
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("CustomSticker")
                            .child(customeGifModels.get(position).getTimestamp())
                            .getRef().removeValue();
                    Snackbar.make(holder.itemView,"Deleted", Snackbar.LENGTH_LONG).show();
                });
            }
            return false;
        });

        holder.img_menu.setOnClickListener(v -> remove.show());

    }

    @Override
    public int getItemCount() {
        return customeGifModels.size();
    }

    public class CustStickerViewHolder extends RecyclerView.ViewHolder{

        ImageView img,img_menu;
        TextView emojiname;

        public CustStickerViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            img_menu = itemView.findViewById(R.id.img_menu);
            emojiname = itemView.findViewById(R.id.emojiname);

        }
    }
}