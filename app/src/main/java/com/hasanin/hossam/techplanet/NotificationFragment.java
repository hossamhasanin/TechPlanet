package com.hasanin.hossam.techplanet;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    notificationRecAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        RecyclerView notificationRec = view.findViewById(R.id.notification_list);
        TextView emptyMess = view.findViewById(R.id.empty_mess);

        Query query = firebaseFirestore.collection("notifications").orderBy("timestamp" , Query.Direction.DESCENDING).limit(10);

        adapter = null;
        ArrayList<Notification> notifications = new ArrayList<>();
        query.addSnapshotListener((queryDocumentSnapshots , e) -> {
            if (queryDocumentSnapshots.getDocuments().isEmpty()){
                emptyMess.setText("No notifications");
                notificationRec.setVisibility(View.GONE);
                emptyMess.setVisibility(View.VISIBLE);
            } else {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    Notification notification = documentSnapshot.toObject(Notification.class);
                    if (!notification.getFrom().equals(mAuth.getCurrentUser().getUid())) {
                        notifications.add(notification);
                    }
                }
                if (notifications.size() == 0){
                    emptyMess.setText("No notifications");
                    notificationRec.setVisibility(View.GONE);
                    emptyMess.setVisibility(View.VISIBLE);
                } else {
                    adapter = new notificationRecAdapter(notifications, getActivity());
                    if (emptyMess.getVisibility() == View.VISIBLE)
                        emptyMess.setVisibility(View.GONE);
                    if (notificationRec.getVisibility() == View.GONE)
                        notificationRec.setVisibility(View.VISIBLE);
                    notificationRec.setAdapter(adapter);
                    notificationRec.setLayoutManager(new LinearLayoutManager(getActivity()));
                }
            }
        });


        return view;
    }

}
