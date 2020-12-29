package com.hasanin.hossam.techplanet;

import android.app.Activity;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class notificationRecAdapter extends RecyclerView.Adapter<notificationRecAdapter.NotificationHolder> {


    Activity activity;
    ArrayList<Notification> notifications;
    FirebaseFirestore firebaseFirestore;

    public notificationRecAdapter(ArrayList<Notification> notifications , Activity activity) {
        this.activity = activity;
        this.notifications = notifications;
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int i) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        holder.notificationTitle.setText(notifications.get(i).getTitle());
        String time = DateFormat.format("EEEE MMM yyyy HH:MM:ss", notifications.get(i).getTimestamp().toDate()).toString();
        holder.notificationTimestamp.setText(time);
        firebaseFirestore.collection("users").document(notifications.get(i).getFrom()).addSnapshotListener(((documentSnapshot, e) -> {
            String userImage = documentSnapshot.getString("profileImage");
            if (userImage.equals("default")){
                Glide.with(activity).load(mAuth.getCurrentUser().getPhotoUrl()).into(holder.notificationImage);
            } else {
                Glide.with(activity).load(Uri.parse(userImage)).into(holder.notificationImage);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.notification_card, parent, false);
        return new NotificationHolder(view);
    }

    public static class NotificationHolder extends RecyclerView.ViewHolder {

        ConstraintLayout card;
        CircleImageView notificationImage;
        TextView notificationTitle;
        TextView notificationTimestamp;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

            notificationImage = itemView.findViewById(R.id.commet_user_image);
            notificationTitle = itemView.findViewById(R.id.comment_username);
            notificationTimestamp = itemView.findViewById(R.id.comment_content);
            card = itemView.findViewById(R.id.noti_card);

        }
    }

}
