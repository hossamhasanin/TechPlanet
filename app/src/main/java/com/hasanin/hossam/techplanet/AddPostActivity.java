package com.hasanin.hossam.techplanet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class AddPostActivity extends AppCompatActivity {

    private static final int READING_CODE = 400;
    private static final String TAG = "AddPostActivity";
    ImageView postImageHolder;
    ProgressBar progressBar;
    Button uploadImage;
    EditText postTitleHolder;
    EditText postDescHolder;
    Button post;

    String postImage;
    String postTitle;
    String postDesc;

    StorageReference storageReference;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        postImageHolder = findViewById(R.id.post_image);
        progressBar = findViewById(R.id.postProgressBar);
        uploadImage = findViewById(R.id.upload_post_image);
        postTitleHolder = findViewById(R.id.post_title);
        postDescHolder = findViewById(R.id.post_desc);
        post = findViewById(R.id.post);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        userId = firebaseAuth.getCurrentUser().getUid();

        uploadImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(AddPostActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AddPostActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READING_CODE);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1 , 1)
                            .start(AddPostActivity.this);
                }
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1 , 1)
                        .start(AddPostActivity.this);
            }
        });

        post.setOnClickListener(c -> {
            postTitle = postTitleHolder.getText().toString();
            postDesc = postDescHolder.getText().toString();
            if (postTitle != null && postDesc != null && postImage != null) {
                progressBar.setVisibility(View.VISIBLE);
                post.setClickable(false);
                post.setBackground(getResources().getDrawable(R.color.button_disable));
                try {
                    Bitmap compressedImage = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75).compressToBitmap(new File(Uri.parse(postImage).getPath()));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bitmapData = baos.toByteArray();
                    String randomName = UUID.randomUUID().toString();
                    StorageReference imagePath = storageReference.child("postsImages").child(randomName + ".jpg");
                    UploadTask uploadTask = imagePath.putBytes(bitmapData);
                    uploadTask.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            imagePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                progressBar.setVisibility(View.GONE);
                                post.setClickable(true);
                                post.setBackground(getResources().getDrawable(R.color.red));
                                postImage = uri.toString();

                                populatePostData(postImage, postTitle, postDesc);
                                populateNotificationData("New post added" , postTitle , userId , "public" , "posts");
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            post.setClickable(true);
                            post.setBackground(getResources().getDrawable(R.color.red));

                            Toast.makeText(AddPostActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(AddPostActivity.this, "you should fill the post data first ! ...", Toast.LENGTH_LONG).show();
            }
        });

    }


    private void populatePostData(String postImage, String postTitle, String postDesc) {
        Map postData = new HashMap<>();
        postData.put("userId" , userId);
        postData.put("title" , postTitle);
        postData.put("description" , postDesc);
        postData.put("image" , postImage);
        postData.put("timestamp" , FieldValue.serverTimestamp());

        firebaseFirestore.collection("posts").add(postData).addOnCompleteListener(task ->{
           if (task.isSuccessful()){
               Toast.makeText(AddPostActivity.this , "The post saved successfully :)" , Toast.LENGTH_LONG).show();
               onBackPressed();
           } else {
               Toast.makeText(AddPostActivity.this , task.getException().getMessage() , Toast.LENGTH_LONG).show();
           }
        });

    }

    private void populateNotificationData(String title, String body, String from , String to , String collectionName) {
        Map notificationData = new HashMap<>();
        notificationData.put("title" , title);
        notificationData.put("body" , body);
        notificationData.put("from" , from);
        notificationData.put("to" , to);
        notificationData.put("collection" , collectionName);
        notificationData.put("timestamp" , FieldValue.serverTimestamp());

        firebaseFirestore.collection("notifications").add(notificationData);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                postImage = resultUri.toString();
                Glide.with(this).load(resultUri).into(postImageHolder);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout){
            firebaseAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this , task -> {
                Toast.makeText(AddPostActivity.this , "I will miss you :'( " , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddPostActivity.this , LuncherActivity.class);
                startActivity(intent);
            });
        } else if (id == R.id.account_settings) {
            Intent intent = new Intent(AddPostActivity.this , AcountSettingsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("from" , TAG);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
