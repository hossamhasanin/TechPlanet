package com.hasanin.hossam.techplanet;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AcountSettingsActivity extends AppCompatActivity {

    CircleImageView userImageHolder;
    EditText userNameHolder;
    EditText passwordHolder;
    ProgressBar progressBar;
    Button save;
    String userImage;
    String userEmail;
    String userName;
    String userPassword;

    int READING_CODE = 500;

    StorageReference storageReference;
    private GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    Uri defaultImage;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acount_settings);

        progressBar = findViewById(R.id.progressBar);
        userImageHolder = findViewById(R.id.userImage);
        userNameHolder = findViewById(R.id.username);
        passwordHolder = findViewById(R.id.password);
        save = findViewById(R.id.save);

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();
        userEmail = firebaseAuth.getCurrentUser().getEmail();
        defaultImage = firebaseAuth.getCurrentUser().getPhotoUrl();

        if (defaultImage != null){
            Glide.with(this).load(defaultImage).dontAnimate().into(userImageHolder);
        }

        // find user data if exists in database
        if (!getIntent().getExtras().getString("from").equals("LuncherActivity")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            progressBar.setVisibility(View.VISIBLE);
            firebaseFirestore.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    userName = task.getResult().get("name").toString();
                    userEmail = task.getResult().get("email").toString();
                    userImage = task.getResult().get("profileImage").toString();

                    passwordHolder.setVisibility(View.GONE);
                    if (userImage.equals("default")){
                        userImage = null;
                        Glide.with(AcountSettingsActivity.this).load(defaultImage).dontAnimate().into(userImageHolder);
                    } else {
                        Glide.with(AcountSettingsActivity.this).load(Uri.parse(userImage)).into(userImageHolder);
                    }
                    userNameHolder.setText(userName);
                } else {
                    Toast.makeText(AcountSettingsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            });
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        userImageHolder.setOnClickListener(c -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(AcountSettingsActivity.this , Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AcountSettingsActivity.this , new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READING_CODE);
                } else {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1 , 1)
                            .start(AcountSettingsActivity.this);
                }
            } else {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1 , 1)
                        .start(AcountSettingsActivity.this);
            }
        });

        save.setOnClickListener(c -> {
            if (userNameHolder.getText().toString() != null && passwordHolder.getText().toString() != null){
                progressBar.setVisibility(View.VISIBLE);

                userName = userNameHolder.getText().toString();
                userPassword = passwordHolder.getText().toString();

                if (userImage != null){
                    StorageReference imagePath = storageReference.child("profile_images").child(userId + ".jpg");
                    imagePath.putFile(Uri.parse(userImage)).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                imagePath.getDownloadUrl().addOnSuccessListener(uri -> {
                                    userImage = uri.toString();
                                    Toast.makeText(AcountSettingsActivity.this , "Saved successfully :)" , Toast.LENGTH_LONG).show();

                                    populateUserData(userImage , userId , userEmail);
                                });
                            } else {
                                progressBar.setVisibility(View.GONE);
                                String errorMess = task.getException().getMessage();
                                Toast.makeText(AcountSettingsActivity.this , "Error : "+errorMess , Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    populateUserData(null , userId , userEmail);
                }

            }
        });

    }

    public void populateUserData(String userImage , String userId , String userEmail){
        String popImage = "";
        if (userImage == null){
            popImage = "default";
        } else {
            popImage = userImage;
        }
        Map<String , String> userData = new HashMap<>();
        userData.put("name" , userName);
        userData.put("email" , userEmail);
        userData.put("profileImage" , popImage);

        if (passwordHolder.getVisibility() == View.VISIBLE){
            firebaseAuth.getCurrentUser().updatePassword(userPassword);
        }
        firebaseFirestore.collection("users").document(userId).set(userData).addOnCompleteListener(task1 -> {
            progressBar.setVisibility(View.GONE);
            if (task1.isSuccessful()){
                Intent intent = new Intent(AcountSettingsActivity.this , MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(AcountSettingsActivity.this , task1.getException().getMessage() , Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                userImage = resultUri.toString();
                defaultImage = null;
                Glide.with(this).load(resultUri).dontAnimate().into(userImageHolder);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


}
