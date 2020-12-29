package com.hasanin.hossam.techplanet;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentRecAdapter extends FirestoreRecyclerAdapter <Comment , commentRecAdapter.commentViewHolder> {


    Activity activity;
    FirebaseFirestore firebaseFirestore;
    String postId;
    public commentRecAdapter(@NonNull FirestoreRecyclerOptions<Comment> options , Activity activity , FirebaseFirestore firebaseFirestore , String postId) {
        super(options);
        this.firebaseFirestore = firebaseFirestore;
        this.postId = postId;
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull commentViewHolder holder, int i, @NonNull Comment comment) {
        firebaseFirestore.collection("users").document(comment.getUserId()).addSnapshotListener((documentSnapshot, e) -> {
           String userimage = documentSnapshot.getString("profileImage");
           String username = documentSnapshot.getString("name");
           Glide.with(activity).load(Uri.parse(userimage)).into(holder.userImage);
           holder.username.setText(username);
        });
        holder.content.setText(comment.getContent());
    }

    @NonNull
    @Override
    public commentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.comment_card , parent , false);
        return new commentViewHolder(view);
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView username;
        TextView content;

        public commentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.commet_user_image);
            username = itemView.findViewById(R.id.comment_username);
            content = itemView.findViewById(R.id.comment_content);
        }
    }

}
