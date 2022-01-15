package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.gaming.community.flexster.OnSelectedItemListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.CustomeGifModel;

import java.util.ArrayList;

public class ShowCustSctickerAdapter extends RecyclerView.Adapter<ShowCustSctickerAdapter.ShowCustSctickerViewHolder> {

    Context context;
    ArrayList<CustomeGifModel> customeGifModels = new ArrayList<>();
    OnSelectedItemListener onSelectedItemListener;


    public ShowCustSctickerAdapter(Context context, ArrayList<CustomeGifModel> customeGifModels,OnSelectedItemListener onSelectedItemListener){
        this.context=context;
        this.customeGifModels=customeGifModels;
        this.onSelectedItemListener=onSelectedItemListener;
    }

    @NonNull
    @Override
    public ShowCustSctickerAdapter.ShowCustSctickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_cust_sticker_adapter,parent,false);
        return new ShowCustSctickerAdapter.ShowCustSctickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowCustSctickerAdapter.ShowCustSctickerViewHolder holder, @SuppressLint("RecyclerView") int position) {

        Glide.with(context).load(customeGifModels.get(position).getUri()).thumbnail(0.1f).into(holder.img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSelectedItemListener.setOnClick(customeGifModels.get(position).getUri(),position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return customeGifModels.size();
    }

    public class ShowCustSctickerViewHolder extends RecyclerView.ViewHolder{

        ImageView img;

        public ShowCustSctickerViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);

        }
    }
}