package com.hasanin.hossam.techplanet;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("newPost");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        FloatingActionButton addPost = findViewById(R.id.add_post);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        addPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this , AddPostActivity.class);
            startActivity(intent);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.bottom_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment , new HomeFragment()).addToBackStack(null).commit();
                return true;
            } else if (id == R.id.bottom_notification) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment , new NotificationFragment()).addToBackStack(null).commit();
                return true;
            }
            return false;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment , new HomeFragment()).commit();


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
            mAuth.signOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this , task -> {
                Toast.makeText(MainActivity.this , "I will miss you :'( " , Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this , LuncherActivity.class);
                startActivity(intent);
            });
        } else if (id == R.id.account_settings) {
            Intent intent = new Intent(MainActivity.this , AcountSettingsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("from" , TAG);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
