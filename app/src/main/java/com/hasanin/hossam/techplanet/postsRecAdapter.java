package com.hasanin.hossam.techplanet;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class postsRecAdapter extends FirestoreRecyclerAdapter<Posts , postsRecAdapter.postsHolder> {

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Activity activity;
    String currentUserId;
    FirebaseFirestore firebaseFirestore;
    public postsRecAdapter(@NonNull FirestoreRecyclerOptions<Posts> options , Activity activity , String currentUserId , FirebaseFirestore firebaseFirestore) {
        super(options);
        this.activity = activity;
        this.currentUserId = currentUserId;
        this.firebaseFirestore = firebaseFirestore;
    }

    @Override
    protected void onBindViewHolder(@NonNull postsHolder holder, int i, @NonNull Posts posts) {
        if (posts.getUserId() != null) {
            Task<DocumentSnapshot> find = firebaseFirestore.collection("users").document(posts.getUserId()).get();
            find.addOnSuccessListener(userData -> {
                String username = userData.getString("name");
                String userimage = userData.getString("profileImage");
                holder.userName.setText(username);
                if (userimage.equals("default")){
                    Glide.with(activity).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).into(holder.userImage);
                } else {
                    Glide.with(activity).load(Uri.parse(userimage)).into(holder.userImage);
                }
            });

            String time = DateFormat.format("EEEE MMM yyyy HH:MM:ss" , posts.getTimestamp().toDate()).toString();
            holder.timestamp.setText(time);
            Glide.with(activity).load(Uri.parse(posts.getImage())).centerCrop().into(holder.postImage);
            holder.postTitle.setText(posts.getTitle());

            firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").document(currentUserId).addSnapshotListener(((queryDocumentSnapshots, e) -> {
                if(queryDocumentSnapshots.exists()){
                    holder.likeBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like_red));
                } else {
                    holder.likeBtn.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_like));
                }
            }));

            firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").addSnapshotListener((queryDocumentSnapshots, e) -> {
               if (!queryDocumentSnapshots.isEmpty()){
                   holder.likesNum.setText(queryDocumentSnapshots.size() + " Likes");
               } else {
                   holder.likesNum.setText("0 Likes");
               }
            });

            firebaseFirestore.collection("posts/" + posts.getPostId() + "/comments").addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (!queryDocumentSnapshots.isEmpty()){
                    holder.commentsNum.setText(queryDocumentSnapshots.size() + " Comments");
                } else {
                    holder.likesNum.setText("0 Comments");
                }
            });


            firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (!queryDocumentSnapshots.isEmpty()){
                    holder.likesNum.setText(queryDocumentSnapshots.size() + " Likes");
                } else {
                    holder.likesNum.setText("0 Likes");
                }
            });

            holder.likeBtn.setOnClickListener(c -> {
                firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").document(currentUserId).get().addOnCompleteListener(t -> {
                   if (t.getResult().exists()){
                       firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").document(currentUserId).delete();
                   } else {
                       Map likesMap = new HashMap();
                       likesMap.put("timestamp" , FieldValue.serverTimestamp());
                       firebaseFirestore.collection("posts/" + posts.getPostId() + "/likes").document(currentUserId).set(likesMap);
                       populateNotificationData(firebaseFirestore , "New like on your post" , posts.getTitle() , currentUserId , posts.userId , "likes");
                   }
                });
            });

            holder.commetBtn.setOnClickListener(c -> {
                Intent intent = new Intent(activity , CommentsActivity.class);
                intent.putExtra("postId" , posts.getPostId());
                intent.putExtra("postTitle" , posts.getTitle());
                activity.startActivity(intent);
            });

        }
    }

    private void populateNotificationData(FirebaseFirestore firebaseFirestore , String title, String body, String from , String to , String collectionName) {
        Map notificationData = new HashMap<>();
        notificationData.put("title" , title);
        notificationData.put("body" , body);
        notificationData.put("from" , from);
        notificationData.put("to" , to);
        notificationData.put("collection" , collectionName);
        notificationData.put("timestamp" , FieldValue.serverTimestamp());

        firebaseFirestore.collection("notifications").add(notificationData);

    }

    @NonNull
    @Override
    public postsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.posts_card , parent , false);
        return new postsHolder(view);
    }

    public static class postsHolder extends RecyclerView.ViewHolder{

        CircleImageView userImage;
        TextView userName;
        TextView timestamp;
        ImageView postImage;
        TextView postTitle;
        ImageView likeBtn;
        TextView likesNum;
        TextView commentsNum;
        ImageView commetBtn;
        public postsHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_post_image);
            userName = itemView.findViewById(R.id.post_username);
            timestamp = itemView.findViewById(R.id.post_timestamp);
            postImage = itemView.findViewById(R.id.post_show_image);
            postTitle = itemView.findViewById(R.id.post_show_title);
            likeBtn = itemView.findViewById(R.id.like_btn);
            likesNum = itemView.findViewById(R.id.likes_num);
            commetBtn = itemView.findViewById(R.id.comment_btn);
            commentsNum = itemView.findViewById(R.id.comment_num);
        }
    }
}
