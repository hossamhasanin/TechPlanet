package com.hasanin.hossam.techplanet;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    postsRecAdapter adapter;
    FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        RecyclerView postsRec = view.findViewById(R.id.posts_list);
        TextView emptyMess = view.findViewById(R.id.posts_empty_mess);


        Query query = firebaseFirestore.collection("posts").orderBy("timestamp" , Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Posts> options = new FirestoreRecyclerOptions.Builder<Posts>()
                .setQuery(query , snapshot -> {
                    String userId = snapshot.getString("userId");
                    String postId = snapshot.getId();
                    String title = snapshot.getString("title");
                    String image = snapshot.getString("image");
                    String description = snapshot.getString("description");
                    Timestamp timestamp = (Timestamp) snapshot.get("timestamp");
                    Posts posts = new Posts(userId , postId , image , title , description , timestamp);

                    return posts;
                })
                .build();
        adapter = null;
        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
           if (queryDocumentSnapshots.getDocuments().isEmpty()){
               postsRec.setVisibility(View.GONE);
               emptyMess.setText("There is no posts yet");
               emptyMess.setVisibility(View.VISIBLE);
           } else {
               if (emptyMess.getVisibility() == View.VISIBLE)
                   emptyMess.setVisibility(View.GONE);
               postsRec.setVisibility(View.VISIBLE);
               adapter = new postsRecAdapter(options , getActivity() , mAuth.getCurrentUser().getUid() , firebaseFirestore);
               postsRec.setAdapter(adapter);
               postsRec.setLayoutManager(new LinearLayoutManager(getActivity()));

               adapter.startListening();
           }
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
