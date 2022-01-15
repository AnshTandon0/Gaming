package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaming.community.flexster.OnSelectedItemListener;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelGameList;

import java.util.ArrayList;

public class AdapterDialogGameList extends RecyclerView.Adapter<AdapterDialogGameList.GameListViewHolder> implements Filterable {

    Context context;
    ArrayList<ModelGameList> modelGameLists = new ArrayList<>();
    ArrayList<ModelGameList> gamelistFilterModel = new ArrayList<>();
    OnSelectedItemListener onSelectedItemListener;

    public AdapterDialogGameList(Context context, ArrayList<ModelGameList> modelGameLists,OnSelectedItemListener onSelectedItemListener){
        this.context=context;
        this.modelGameLists=modelGameLists;
        this.gamelistFilterModel = modelGameLists;
        this.onSelectedItemListener=onSelectedItemListener;
    }

    @NonNull
    @Override
    public AdapterDialogGameList.GameListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_game_list_adapter,parent,false);
        return new AdapterDialogGameList.GameListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDialogGameList.GameListViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.txt_game_name.setText(gamelistFilterModel.get(position).getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onSelectedItemListener.setOnClick(gamelistFilterModel.get(position).getName(),position);

            }
        });

    }

    @Override
    public int getItemCount() {
        return gamelistFilterModel.size();
    }

    public class GameListViewHolder extends RecyclerView.ViewHolder{

        TextView txt_game_name;

        public GameListViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_game_name = itemView.findViewById(R.id.txt_game_name);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    gamelistFilterModel = modelGameLists;
                } else {
                    ArrayList<ModelGameList> filteredList = new ArrayList<>();
                    for (ModelGameList row : modelGameLists) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    gamelistFilterModel = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = gamelistFilterModel;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                gamelistFilterModel = (ArrayList<ModelGameList>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}