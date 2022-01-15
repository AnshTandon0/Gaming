package com.gaming.community.flexster.marketPlace;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gaming.community.flexster.R;
import com.gaming.community.flexster.adapter.AdapterGameList;
import com.gaming.community.flexster.admin.GameListActivity;
import com.gaming.community.flexster.model.ModelGameList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TypePick extends DialogFragment {

    int position = 0;
    @SuppressWarnings("EmptyMethod")
    public interface SingleChoiceListener{
        void onPositiveButtonClicked(String[] list, int position);
        void onNegativeButtonClicked();

    }

    SingleChoiceListener listener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SingleChoiceListener) context;
        } catch (Exception e) {
            throw new ClassCastException(requireActivity().toString()+" SingleChoiceListener must implemented");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ArrayList<String> gamenamelist = new ArrayList<>();

        DatabaseReference databaseReference;
        databaseReference= FirebaseDatabase.getInstance().getReference().child("games");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name="";

                for (DataSnapshot np : dataSnapshot.getChildren()) {

                    name=np.child("name").getValue(String.class);

                    if(name!=null) {
                        /*ModelGameList md=new ModelGameList();
                        md.setName(name);*/
                        gamenamelist.add(name);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Couldn't Load Data", Toast.LENGTH_SHORT).show();
            }
        });

        String[] list = new String[100];

        for (int a=0;a<gamenamelist.size();a++)
        {
            list[a]=gamenamelist.get(a);
        }

        builder.setTitle("Select Game")
                .setSingleChoiceItems(list, position, (dialog, which) -> position = which).setPositiveButton("Ok", (dialog, which) -> listener.onPositiveButtonClicked(list,position)).setNegativeButton("Cancel", (dialog, which) -> listener.onNegativeButtonClicked());
        return builder.create();
    }
}