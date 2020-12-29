package com.hasanin.hossam.techplanet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;
    commentRecAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String postId = getIntent().getStringExtra("postId");
        String postTitle = getIntent().getStringExtra("postTitle");
        getSupportActionBar().setTitle(postTitle);

        RecyclerView commentsList = findViewById(R.id.comment_list);
        ImageView send = findViewById(R.id.send_comment);
        EditText comment = findViewById(R.id.write_comment);

        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Query query = firebaseFirestore.collection("posts/"+postId+"/comments").orderBy("timestamp" , Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query , Comment.class)
                .build();

        adapter = new commentRecAdapter(options , this , firebaseFirestore , postId);
        commentsList.setAdapter(adapter);
        commentsList.setLayoutManager(new LinearLayoutManager(this));

        send.setOnClickListener(c -> {
            Map commentData = new HashMap<>();
            commentData.put("userId" , mAuth.getCurrentUser().getUid());
            commentData.put("content" , comment.getText().toString());
            commentData.put("timestamp" , FieldValue.serverTimestamp());
            firebaseFirestore.collection("posts/"+postId+"/comments").add(commentData).addOnSuccessListener(task -> {
                comment.setText("");
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
