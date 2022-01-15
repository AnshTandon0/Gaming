package com.gaming.community.flexster;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class PostCount {

    public static void increasePost(String timeStamp){
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("value", "1");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("PostCount")
                .child(timeStamp)
                .setValue(hashMap);
    }

    public static void increaseComment(String userID,String timeStamp){
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("value", "1");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("CommentCount")
                .child(timeStamp)
                .setValue(hashMap);
    }

    public static void increaseFightWin(String timeStamp, String c_id){
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("value", "1");
        FirebaseDatabase.getInstance().getReference("Users")
                .child(c_id)
                .child("FightWin")
                .child(timeStamp)
                .setValue(hashMap);
    }

    public static void getfightlevel(String userid, TextView txt_fight_count){
         String[] level = {"1"};
         int[] size = {0};
        Log.e("sizeofsize", String.valueOf(size[0]));
        FirebaseDatabase.getInstance().getReference("Users").child(userid).child("FightWin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        size[0] = (int) dataSnapshot.getChildrenCount();

                        if(size[0] >1)
                        {
                            float result= size[0];
                            int s_result = size[0];

                            level[0] = String.valueOf(s_result+1);

                            if (result < 999){
                                txt_fight_count.setText(level[0]);
                            }
                            else if (result > 9999){
                                txt_fight_count.setText("9.9k");
                            }
                            else {
                                float resultdone = result/1000;
                                level[0]=String.valueOf(resultdone);
                                String ans = level[0].substring(0,3);
                                txt_fight_count.setText(ans+"k");
                            }
                        }
                        else
                        {
                            txt_fight_count.setText("1");
                            level[0] ="1";
                        }
                        Log.e("sizeofwon", String.valueOf(size[0]));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public static  void getengagelevel(String udid, TextView txt_post_count) {
         String[] level = {"1"};
         int[] postsize = {0};
         final int[] postlevel = {0};
         final int[] commentlevel = {0};
         int[] commentsize = {0};
        FirebaseDatabase.getInstance().getReference("Users").child(udid).child("PostCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        postsize[0] = (int) dataSnapshot.getChildrenCount();

                        FirebaseDatabase.getInstance().getReference("Users").child(udid).child("CommentCount")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // get total available quest
                                        commentsize[0] = (int) dataSnapshot.getChildrenCount();
                                        if(postsize[0]>=1 && commentsize[0]>=10)
                                        {
                                            postlevel[0] =postsize[0];
                                            commentlevel[0] =commentsize[0]/10;
                                            level[0]= String.valueOf(postlevel[0] + commentlevel[0] +1);
                                            txt_post_count.setText(level[0]);

                                        }
                                        else if(postsize[0]>=1)
                                        {
                                            postlevel[0] =postsize[0];
                                            level[0]= String.valueOf(postlevel[0] +1);
                                            txt_post_count.setText(level[0]);
                                        }
                                        else if(commentsize[0]>=10)
                                        {
                                            commentlevel[0] =commentsize[0]/10;
                                            level[0]= String.valueOf(commentlevel[0] +1);
                                            txt_post_count.setText(level[0]);
                                        }
                                        else
                                        {
                                            level[0] ="1";
                                            txt_post_count.setText(level[0]);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        FirebaseDatabase.getInstance().getReference("Users").child(udid).child("CommentCount")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // get total available quest
                                        commentsize[0] = (int) dataSnapshot.getChildrenCount();
                                        if(postsize[0]>=1 && commentsize[0]>=10)
                                        {
                                            postlevel[0] =postsize[0];
                                            commentlevel[0] =commentsize[0]/10;
                                            level[0]= String.valueOf(postlevel[0] + commentlevel[0] +1);
                                            txt_post_count.setText(level[0]);

                                        }
                                        else if(postsize[0]>=1)
                                        {
                                            postlevel[0] =postsize[0];
                                            level[0]= String.valueOf(postlevel[0] +1);
                                            txt_post_count.setText(level[0]);
                                        }
                                        else if(commentsize[0]>=10)
                                        {
                                            commentlevel[0] =commentsize[0]/10;
                                            level[0]= String.valueOf(commentlevel[0] +1);
                                            txt_post_count.setText(level[0]);
                                        }
                                        else
                                        {
                                            level[0] ="1";
                                            txt_post_count.setText(level[0]);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                    }
                });
        Log.e("winningis", String.valueOf(commentsize[0]));
        Log.e("sizeissss", String.valueOf(postsize[0]));

    }


    //*****using groups*****

    public static void increaseGroupPost(String groupId,String timeStamp, String userid){

        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("value", "1");
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId)
                .child("GroupPostCount")
                .child(timeStamp).setValue(hashMap);


        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("value", "1");
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId)
                .child("Participants")
                .child(userid)
                .child("GroupUserPostCount")
                .child(timeStamp).setValue(hashMap1);

    }

    public static void increaseGroupComment(String groupId,String timeStamp, String userid){

        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("value", "1");
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId)
                .child("Participants")
                .child(userid)
                .child("GroupUserCommentCount")
                .child(timeStamp).setValue(hashMap1);

    }

    public static void increaseGroupMsg(String groupId,String timeStamp, String userid){

        HashMap<Object, String> hashMap1 = new HashMap<>();
        hashMap1.put("value", "1");
        FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId)
                .child("Participants")
                .child(userid)
                .child("GroupUserMsgCount")
                .child(timeStamp).setValue(hashMap1);

    }


    public static void getgrouplevel(String group_Id, TextView tv) {

        String[] level = {"1"};
        int[] size = {0};
        Log.e("sizeofsize", String.valueOf(size[0]));
        FirebaseDatabase.getInstance().getReference("Groups").child(group_Id).child("GroupPostCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        size[0] = (int) dataSnapshot.getChildrenCount();

                        if(size[0] >=10)
                        {
                            int result= size[0] /10;
                            level[0] = String.valueOf(result+1);
                            tv.setText(String.valueOf(level[0]));
                        }
                        else
                        {
                            tv.setText("1");
                            level[0] ="1";
                            tv.setText(String.valueOf(level[0]));
                        }

                        Log.e("sizeofwon", String.valueOf(size[0]));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public static void getgroupuserlevel(String group_Id,String user_id, TextView tv) {

        int[] commentsize = {0};
        int[] postsize = {0};
        int[] messagessize = {0};

        int[] commentlevel={0};
        int[] postlevel = { 0 };
        int[] messageslevel={0};
        int[] finallevl={0};
        FirebaseDatabase.getInstance().getReference("Groups").child(group_Id).child("Participants").child(user_id).child("GroupUserCommentCount")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // get total available quest
                        commentsize[0]=(int) dataSnapshot.getChildrenCount();
                        FirebaseDatabase.getInstance().getReference("Groups").child(group_Id).child("Participants").child(user_id).child("GroupUserMsgCount")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // get total available quest
                                        messagessize[0]=(int) dataSnapshot.getChildrenCount();
                                            FirebaseDatabase.getInstance().getReference("Groups").child(group_Id).child("Participants").child(user_id).child("GroupUserPostCount")
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            // get total available quest
                                                            postsize[0]=(int) dataSnapshot.getChildrenCount();

                                                            if(postsize[0]>=1)
                                                            {
                                                                postlevel[0] =postsize[0];
                                                            }
                                                            else
                                                            {
                                                                postlevel[0]=0;
                                                            }

                                                            if(commentsize[0]>=10)
                                                            {
                                                                commentlevel[0]=commentsize[0]/10;
                                                            }
                                                            else
                                                            {
                                                                commentlevel[0]=0;
                                                            }

                                                            if(messagessize[0]>=100)
                                                            {
                                                                messageslevel[0]=messagessize[0]/100;
                                                            }
                                                            else
                                                            {
                                                                messageslevel[0]=0;
                                                            }
                                                            finallevl[0]=postlevel[0]+commentlevel[0]+messageslevel[0]+1;
                                                            tv.setText(String.valueOf(finallevl[0]));

                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    //*****keyboard hide*****
    public static void hideKeyboard(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null) {
            v = new View(ctx);
        }
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
