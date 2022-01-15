package com.gaming.community.flexster.adapter;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.gaming.community.flexster.PostCount;
import com.gaming.community.flexster.admin.AdminActivity;
import com.gaming.community.flexster.menu.MenuActivity;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.gaming.community.flexster.GetTimeAgo;
import com.gaming.community.flexster.MediaViewActivity;
import com.gaming.community.flexster.R;
import com.gaming.community.flexster.model.ModelPost;
import com.gaming.community.flexster.model.ModelUser;
import com.gaming.community.flexster.notifications.Data;
import com.gaming.community.flexster.notifications.Sender;
import com.gaming.community.flexster.notifications.Token;
import com.gaming.community.flexster.post.CommentActivity;
import com.gaming.community.flexster.profile.UserProfileActivity;
import com.gaming.community.flexster.search.SearchActivity;
import com.gaming.community.flexster.send.SendToGroupActivity;
import com.gaming.community.flexster.send.SendToUserActivity;
import com.gaming.community.flexster.watchParty.StartPartyActivity;
import com.gaming.community.flexster.watchParty.StartYouTubeActivity;
import com.gaming.community.flexster.who.LikedActivity;
import com.squareup.picasso.Picasso;
import com.tylersuehr.socialtextview.SocialTextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import me.jagar.chatvoiceplayerlibrary.VoicePlayerView;
import timber.log.Timber;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

@SuppressWarnings("ALL")
public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    final Context context;
    final List<ModelPost> modelPosts;

    public AdapterPost(Context context, List<ModelPost> modelPosts) {
        this.context = context;
        this.modelPosts = modelPosts;
    }

    private boolean notify = false;

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_list, parent, false);
        return new MyHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //advertice native ad
        if (context.getString(R.string.check_ad_status).equals("1")) {
            if (position > 1 && (position + 1) % 4 == 0) {
                holder.ad.setVisibility(View.VISIBLE);
            }
        }


        Log.e("modeliddddd", "idis" + modelPosts.get(position).getId());


        //PostCount
        PostCount.getfightlevel(modelPosts.get(position).getId(), holder.txt_fight_count);
        PostCount.getengagelevel(modelPosts.get(position).getId(), holder.txt_post_count);

        //AdminInfo
        FirebaseDatabase.getInstance().getReference("Admin").child(modelPosts.get(position).getId())
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


        //UserInfo
        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(modelPosts.get(position).getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.child("photo").getValue().toString().isEmpty())
                            Picasso.get().load(snapshot.child("photo").getValue().toString()).into(holder.dp);
                        holder.name.setText(snapshot.child("name").getValue().toString());
                        //holder.username.setText(snapshot.child("username").getValue().toString());

                        //SetOnClick
                        holder.dp.setOnClickListener(v -> {
                            if (!modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Intent intent = new Intent(context, UserProfileActivity.class);
                                intent.putExtra("hisUID", modelPosts.get(position).getId());
                                context.startActivity(intent);
                            } else {
                                Snackbar.make(v, "It's you", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        holder.name.setOnClickListener(v -> {
                            if (!modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Intent intent = new Intent(context, UserProfileActivity.class);
                                intent.putExtra("hisUID", modelPosts.get(position).getId());
                                context.startActivity(intent);
                            } else {
                                Snackbar.make(v, "It's you", Snackbar.LENGTH_LONG).show();
                            }
                        });
                        holder.username.setOnClickListener(v -> {
                            if (!modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                Intent intent = new Intent(context, UserProfileActivity.class);
                                intent.putExtra("hisUID", modelPosts.get(position).getId());
                                context.startActivity(intent);
                            } else {
                                Snackbar.make(v, "It's you", Snackbar.LENGTH_LONG).show();
                            }
                        });

                        //Verify
                        if (snapshot.child("verified").getValue().toString().equals("yes")) {
                            holder.verified.setVisibility(View.VISIBLE);
                        } else {
                            holder.verified.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Time
        long lastTime = Long.parseLong(modelPosts.get(position).getpTime());
        holder.time.setText(GetTimeAgo.getTimeAgo(lastTime));

        //Extra
        FirebaseDatabase.getInstance().getReference("postExtra").child(modelPosts.get(position).getpId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                   /* if (!snapshot.child("location").getValue().toString().isEmpty()) holder.location.setText(" . " + snapshot.child("location").getValue().toString());
                    holder.location.setOnClickListener(v -> {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.co.in/maps?q=" + snapshot.child("location").getValue().toString()));
                        context.startActivity(i);
                    });*/

                            if (snapshot.child("game").exists()) {
                                if (snapshot.child("game").getValue().toString().equals("")) {
                                    holder.username.setText("");
                                    holder.dot.setVisibility(View.GONE);
                                } else {
                                    holder.username.setText(snapshot.child("game").getValue().toString());
                                    holder.dot.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.username.setText("");
                                holder.dot.setVisibility(View.GONE);
                            }


                            if (!snapshot.child("feeling").getValue().toString().isEmpty()) {
                                holder.ll_feeling.setVisibility(View.VISIBLE);
                                holder.feeling.setText(snapshot.child("feeling").getValue().toString());
                            } else {
                                holder.ll_feeling.setVisibility(View.GONE);
                            }


                            if (!snapshot.child("feeling").getValue().toString().isEmpty()) {
                                String mFeeling = snapshot.child("feeling").getValue().toString();
                                if (mFeeling.contains("Traveling")) {
                                    holder.activity.setImageResource(R.drawable.airplane);
                                } else if (mFeeling.contains("Watching")) {
                                    holder.activity.setImageResource(R.drawable.watching);
                                } else if (mFeeling.contains("Listening")) {
                                    holder.activity.setImageResource(R.drawable.listening);
                                } else if (mFeeling.contains("Thinking")) {
                                    holder.activity.setImageResource(R.drawable.thinking);
                                } else if (mFeeling.contains("Celebrating")) {
                                    holder.activity.setImageResource(R.drawable.celebration);
                                } else if (mFeeling.contains("Looking")) {
                                    holder.activity.setImageResource(R.drawable.looking);
                                } else if (mFeeling.contains("Playing")) {
                                    holder.activity.setImageResource(R.drawable.playing);
                                } else if (mFeeling.contains("happy")) {
                                    holder.activity.setImageResource(R.drawable.smiling);
                                } else if (mFeeling.contains("loved")) {
                                    holder.activity.setImageResource(R.drawable.love);
                                } else if (mFeeling.contains("sad")) {
                                    holder.activity.setImageResource(R.drawable.sad);
                                } else if (mFeeling.contains("crying")) {
                                    holder.activity.setImageResource(R.drawable.crying);
                                } else if (mFeeling.contains("angry")) {
                                    holder.activity.setImageResource(R.drawable.angry);
                                } else if (mFeeling.contains("confused")) {
                                    holder.activity.setImageResource(R.drawable.confused);
                                } else if (mFeeling.contains("broken")) {
                                    holder.activity.setImageResource(R.drawable.broken);
                                } else if (mFeeling.contains("cool")) {
                                    holder.activity.setImageResource(R.drawable.cool);
                                } else if (mFeeling.contains("funny")) {
                                    holder.activity.setImageResource(R.drawable.joy);
                                } else if (mFeeling.contains("tired")) {
                                    holder.activity.setImageResource(R.drawable.tired);
                                } else if (mFeeling.contains("shock")) {
                                    holder.activity.setImageResource(R.drawable.shocked);
                                } else if (mFeeling.contains("love")) {
                                    holder.activity.setImageResource(R.drawable.heart);
                                } else if (mFeeling.contains("sleepy")) {
                                    holder.activity.setImageResource(R.drawable.sleeping);
                                } else if (mFeeling.contains("expressionless")) {
                                    holder.activity.setImageResource(R.drawable.muted);
                                } else if (mFeeling.contains("blessed")) {
                                    holder.activity.setImageResource(R.drawable.angel);
                                }
                            }

                            if (snapshot.child("privacy").getValue().toString().equals("No one")) {
                                if (!modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                    params.height = 0;
                                    holder.itemView.setLayoutParams(params);
                                }

                            } else if (snapshot.child("privacy").getValue().toString().equals("Followers")) {
                                FirebaseDatabase.getInstance().getReference().child("Follow").child(modelPosts.get(position).getId())
                                        .child("Followers").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid()) && !modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                            params.height = 0;
                                            holder.itemView.setLayoutParams(params);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //PostDetails
        String type = modelPosts.get(position).getType();
        if (!type.equals("bg")) {
            holder.text.setLinkText(modelPosts.get(position).getText());
            holder.text.setOnLinkClickListener((i, s) -> {
                if (i == 1) {

                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("hashtag", s);
                    context.startActivity(intent);

                } else if (i == 2) {
                    String username = s.replaceFirst("@", "");
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = ref.orderByChild("username").equalTo(username.trim());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String id = ds.child("id").getValue().toString();
                                    if (id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        Snackbar.make(holder.itemView, "It's you", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        Intent intent = new Intent(context, UserProfileActivity.class);
                                        intent.putExtra("hisUID", id);
                                        context.startActivity(intent);
                                    }
                                }
                            } else {
                                Snackbar.make(holder.itemView, "Invalid username, can't find user with this username", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Snackbar.make(holder.itemView, error.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else if (i == 16) {
                    if (!s.startsWith("https://") && !s.startsWith("http://")) {
                        s = "http://" + s;
                    }
                    Intent openUrlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(s));
                    context.startActivity(openUrlIntent);
                } else if (i == 4) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", s, null));
                    context.startActivity(intent);
                } else if (i == 8) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, s);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "");
                    context.startActivity(intent);

                }
            });
            holder.mediaView.setVisibility(View.GONE);
        }

        if (type.equals("image")) {
            holder.mediaView.setVisibility(View.VISIBLE);
            Picasso.get().load(modelPosts.get(position).getMeme()).into(holder.mediaView);
        }
        //CheckComments
        FirebaseDatabase.getInstance().getReference("Posts")
                .child(modelPosts.get(position).getpId())
                .child("Comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.layout.setVisibility(View.VISIBLE);
                            holder.commentLayout.setVisibility(View.VISIBLE);
                            holder.noComments.setText(String.valueOf(snapshot.getChildrenCount()));
                        } else {
                            holder.commentLayout.setVisibility(View.GONE);
                            holder.noComments.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //CheckViews
        FirebaseDatabase.getInstance().getReference().child("Views").child(modelPosts.get(position).getpId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.layout.setVisibility(View.VISIBLE);
                            holder.viewsLayout.setVisibility(View.VISIBLE);
                            holder.noViews.setText(String.valueOf(snapshot.getChildrenCount()));
                        } else {
                            holder.viewsLayout.setVisibility(View.GONE);
                            holder.noViews.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //CheckLikes
        FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId())
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.likeLayout.setVisibility(View.VISIBLE);
                            holder.line.setVisibility(View.VISIBLE);
                            holder.noLikes.setText(String.valueOf(snapshot.getChildrenCount()));
                            if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                //CheckNew
                                FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {

                                            String react = snapshot.child("type").getValue().toString();
                                            if (react.equals("like")) {
                                                holder.like_img.setImageResource(R.drawable.ic_thumb);
                                                holder.like_text.setText("GG");
                                            }
                                            if (react.equals("love")) {
                                                holder.like_img.setImageResource(R.drawable.ic_love);
                                                holder.like_text.setText("On Fire!");
                                            }
                                            if (react.equals("laugh")) {
                                                holder.like_img.setImageResource(R.drawable.ic_laugh);
                                                holder.like_text.setText("LOL");
                                            }
                                            if (react.equals("wow")) {
                                                holder.like_img.setImageResource(R.drawable.ic_wow);
                                                holder.like_text.setText("Whoa!");
                                            }
                                            if (react.equals("sad")) {
                                                holder.like_img.setImageResource(R.drawable.ic_sad);
                                                holder.like_text.setText("Oh No!");
                                            }
                                            if (react.equals("angry")) {
                                                holder.like_img.setImageResource(R.drawable.ic_angry);
                                                holder.like_text.setText("WTH");
                                            }

                                        } else {
                                            FirebaseDatabase.getInstance().getReference().child("Likes")
                                                    .child(modelPosts.get(position).getpId())
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.exists()) {
                                                                holder.like_img.setImageResource(R.drawable.ic_thumb);
                                                                holder.like_text.setText("GG");
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
                            } else if (!snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                holder.like_img.setImageResource(R.drawable.ic_like);
                                holder.like_text.setText("React");
                            }
                            //QuickShow
                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("like")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.thumb.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.thumb.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("love")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.love.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.love.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("wow")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.wow.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.wow.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("angry")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.angry.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.angry.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("laugh")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.laugh.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.laugh.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            FirebaseDatabase.getInstance().getReference().child("Reaction")
                                    .child(modelPosts.get(position).getpId()).orderByChild("type").equalTo("sad")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.getChildrenCount() > 0) {
                                                holder.sad.setVisibility(View.VISIBLE);
                                            } else {
                                                holder.sad.setVisibility(View.GONE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                        } else {
                            holder.likeLayout.setVisibility(View.GONE);
                            holder.line.setVisibility(View.GONE);
                            holder.like_img.setImageResource(R.drawable.ic_like);
                            holder.like_text.setText("React");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //Like
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(new int[]{
                        R.drawable.ic_thumb,
                        R.drawable.ic_love,
                        R.drawable.ic_laugh,
                        R.drawable.ic_wow,
                        R.drawable.ic_sad,
                        R.drawable.ic_angry
                })
                .withPopupAlpha(1)
                .build();

        ReactionPopup popup = new ReactionPopup(context, config, (position1) -> {

            if (position1 == 0) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "like");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            } else if (position1 == 1) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "love");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            } else if (position1 == 2) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "laugh");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            } else if (position1 == 3) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "wow");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            } else if (position1 == 4) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "sad");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            } else if (position1 == 5) {
                FirebaseDatabase.getInstance().getReference().child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            addToHisNotification(modelPosts.get(position).getId(), "Reacted on post", modelPosts.get(position).getpId());
                            notify = true;
                            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ModelUser user = snapshot.getValue(ModelUser.class);
                                    if (notify) {
                                        //sendNotification(modelPosts.get(position).getId(), Objects.requireNonNull(user).getName(), "liked on your post");
                                    }
                                    notify = false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("type", "angry");
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().setValue(hashMap);
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                return true;
            }

            return true;
        });

        //LikeFunctions
        holder.likeButtonTwo.setOnTouchListener(popup);

        FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        holder.likeButtonTwo.setVisibility(View.GONE);
                        holder.likeButton.setVisibility(View.VISIBLE);
                    } else {
                        holder.likeButton.setVisibility(View.GONE);
                        holder.likeButtonTwo.setVisibility(View.VISIBLE);
                    }
                } else {
                    holder.likeButton.setVisibility(View.GONE);
                    holder.likeButtonTwo.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likeButton.setOnClickListener(v -> FirebaseDatabase.getInstance().getReference().child("Likes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(modelPosts.get(position).getpId()).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getRef().removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Reaction").child(modelPosts.get(position).getpId()).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }));

        //Share
        Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu sharePop = new PopupMenu(wrapper, holder.share);
        //sharePop.getMenu().add(Menu.NONE,0,0, "App");
        sharePop.getMenu().add(Menu.NONE, 0, 0, "Chat");
        sharePop.getMenu().add(Menu.NONE, 1, 1, "Club");
        sharePop.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == 0) {
                Intent intent = new Intent(holder.itemView.getContext(), SendToUserActivity.class);
                intent.putExtra("type", "post");
                intent.putExtra("uri", modelPosts.get(position).getpId());
                intent.putExtra("post_id", modelPosts.get(position).getId());
                intent.putExtra("win_post_id", modelPosts.get(position).getpId());
                intent.putExtra("win_type", "post");
                holder.itemView.getContext().startActivity(intent);
            }
            if (item.getItemId() == 1) {
                Intent intent = new Intent(holder.itemView.getContext(), SendToGroupActivity.class);
                intent.putExtra("type", "post");
                intent.putExtra("uri", modelPosts.get(position).getpId());
                intent.putExtra("creater_win_id", modelPosts.get(position).getId());
                intent.putExtra("win_post_id", modelPosts.get(position).getpId());
                intent.putExtra("win_type", "post");
                holder.itemView.getContext().startActivity(intent);
            }
            return false;
        });

        holder.share.setOnClickListener(v -> sharePop.show());

        //More
        Context moreWrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
        PopupMenu morePop = new PopupMenu(moreWrapper, holder.more);
        morePop.getMenu().add(Menu.NONE, 1, 1, "Save");
        morePop.getMenu().add(Menu.NONE, 2, 2, "Download");
        morePop.getMenu().add(Menu.NONE, 5, 5, "Report");
        morePop.getMenu().add(Menu.NONE, 9, 9, "Reacted by");
        if (modelPosts.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            morePop.getMenu().add(Menu.NONE, 7, 7, "Delete");
        }
        if (modelPosts.get(position).getType().equals("image")) {
            morePop.getMenu().add(Menu.NONE, 8, 8, "Fullscreen");
        }

        morePop.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == 1) {
                FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(modelPosts.get(position).getpId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(modelPosts.get(position).getpId()).removeValue();
                            Snackbar.make(holder.itemView, "Unsaved", Snackbar.LENGTH_LONG).show();
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(modelPosts.get(position).getpId()).setValue(true);
                            Snackbar.make(holder.itemView, "Saved", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar.make(holder.itemView, error.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });

            }
            if (item.getItemId() == 9) {
                Intent intent = new Intent(context, LikedActivity.class);
                intent.putExtra("id", modelPosts.get(position).getpId());
                context.startActivity(intent);
            }
            if (item.getItemId() == 2) {
                if (type.equals("text")) {
                    Snackbar.make(holder.itemView, "This type of post can't be downloaded", Snackbar.LENGTH_LONG).show();

                } else if (type.equals("image")) {
                    Snackbar.make(holder.itemView, "Please wait downloading", Snackbar.LENGTH_LONG).show();
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(modelPosts.get(position).getMeme()));
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalFilesDir(context, DIRECTORY_DOWNLOADS, String.valueOf(System.currentTimeMillis()) + ".png");
                    Objects.requireNonNull(downloadManager).enqueue(request);
                }
            } else if (item.getItemId() == 5) {
                FirebaseDatabase.getInstance().getReference().child("ReportPost").child(modelPosts.get(position).getpId()).setValue(true);
                Snackbar.make(holder.itemView, "Reported", Snackbar.LENGTH_LONG).show();
            } else if (item.getItemId() == 7) {
                if (type.equals("text")) {
                    FirebaseDatabase.getInstance().getReference().child("Posts").child(modelPosts.get(position).getpId()).getRef().removeValue();
                    Snackbar.make(holder.itemView, "Deleted", Snackbar.LENGTH_LONG).show();
                } else {
                    FirebaseStorage.getInstance().getReferenceFromUrl(modelPosts.get(position).getMeme()).delete().addOnCompleteListener(task -> {
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(modelPosts.get(position).getpId()).getRef().removeValue();
                        Snackbar.make(holder.itemView, "Deleted", Snackbar.LENGTH_LONG).show();
                    });
                }
            } else if (item.getItemId() == 8) {
                switch (modelPosts.get(position).getType()) {
                    case "image":

                        Intent intent = new Intent(context, MediaViewActivity.class);
                        intent.putExtra("type", "image");
                        intent.putExtra("uri", modelPosts.get(position).getMeme());
                        context.startActivity(intent);

                        break;
                }
            }
            return false;
        });

        holder.more.setOnClickListener(v -> morePop.show());

        holder.comment.setOnClickListener(v -> {

            Intent intent = new Intent(context, CommentActivity.class);
            //intent.putExtra("check_intent", "Post");
            intent.putExtra("postID", modelPosts.get(position).getpId());
            intent.putExtra("creatore_id", modelPosts.get(position).getId());
            context.startActivity(intent);

        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postID", modelPosts.get(position).getpId());
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return modelPosts.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder {

        final CircleImageView dp;
        final ImageView verified;
        final ImageView activity;
        final ImageView mediaView;
        final ImageView like_img;
        final ImageView more;
        final TextView name;
        final TextView username;
        final TextView time;
        final TextView feeling;
        final TextView location;
        final TextView like_text;
        final SocialTextView text;

        final LinearLayout likeLayout;
        final LinearLayout commentLayout;
        final LinearLayout viewsLayout;
        final LinearLayout layout;
        final LinearLayout share;
        final TextView noLikes;
        final TextView noComments;
        final TextView noViews;
        final ImageView thumb;
        final ImageView love;
        final ImageView laugh;
        final ImageView wow;
        final ImageView angry;
        final ImageView sad;
        final LinearLayout likeButton;
        final LinearLayout likeButtonTwo;
        final LinearLayout comment;
        final RelativeLayout line;
        final RelativeLayout ad;
        TextView dot;
        LinearLayout ll_feeling;
        ImageView admin;
        TextView txt_post_count, txt_fight_count;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            dp = itemView.findViewById(R.id.dp);
            verified = itemView.findViewById(R.id.verified);
            name = itemView.findViewById(R.id.name);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
            ad = itemView.findViewById(R.id.ad);
            time = itemView.findViewById(R.id.time);
            activity = itemView.findViewById(R.id.activity);
            feeling = itemView.findViewById(R.id.feeling);
            location = itemView.findViewById(R.id.location);
            text = itemView.findViewById(R.id.text);
            mediaView = itemView.findViewById(R.id.mediaView);
            share = itemView.findViewById(R.id.share);

            likeLayout = itemView.findViewById(R.id.likeLayout);
            commentLayout = itemView.findViewById(R.id.commentLayout);
            viewsLayout = itemView.findViewById(R.id.viewsLayout);
            layout = itemView.findViewById(R.id.layout);
            noLikes = itemView.findViewById(R.id.noLikes);
            noComments = itemView.findViewById(R.id.noComments);
            noViews = itemView.findViewById(R.id.noViews);
            like_text = itemView.findViewById(R.id.like_text);
            like_img = itemView.findViewById(R.id.like_img);
            thumb = itemView.findViewById(R.id.thumb);
            love = itemView.findViewById(R.id.love);
            laugh = itemView.findViewById(R.id.laugh);
            wow = itemView.findViewById(R.id.wow);
            angry = itemView.findViewById(R.id.angry);
            likeButton = itemView.findViewById(R.id.likeButton);
            sad = itemView.findViewById(R.id.sad);
            likeButtonTwo = itemView.findViewById(R.id.likeButtonTwo);
            line = itemView.findViewById(R.id.line);
            more = itemView.findViewById(R.id.more);
            dot = itemView.findViewById(R.id.dot);
            ll_feeling = itemView.findViewById(R.id.ll_feeling);
            admin = itemView.findViewById(R.id.admin);
            txt_post_count = itemView.findViewById(R.id.txt_post_count);
            txt_fight_count = itemView.findViewById(R.id.txt_fight_count);

            MobileAds.initialize(itemView.getContext(), initializationStatus -> {

            });
            AdLoader.Builder builder = new AdLoader.Builder(itemView.getContext(), itemView.getContext().getString(R.string.native_ad_unit_id));
            builder.forUnifiedNativeAd(unifiedNativeAd -> {
                TemplateView templateView = itemView.findViewById(R.id.my_template);
                templateView.setNativeAd(unifiedNativeAd);
            });

            AdLoader adLoader = builder.build();
            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAd(adRequest);

        }

    }

    private void addToHisNotification(String hisUid, String message, String post) {
        String timestamp = "" + System.currentTimeMillis();
        HashMap<Object, String> hashMap = new HashMap<>();
        hashMap.put("pId", post);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisUid);
        hashMap.put("notification", message);
        hashMap.put("sUid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Notifications").child(timestamp).setValue(hashMap);
        FirebaseDatabase.getInstance().getReference("Users").child(hisUid).child("Count").child(timestamp).setValue(true);
    }

}
