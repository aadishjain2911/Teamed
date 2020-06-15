package com.example.myblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth ;

    private FirebaseFirestore firebaseFirestore ;

    private String currentUid ;

    private BottomNavigationView bottomNavigationView ;

    private HomeFragment homeFragment = new HomeFragment() ;
    private NotifFragment notifFragment = new NotifFragment();
    private BookmarksFragment bookmarksFragment = new BookmarksFragment() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseFirestore = FirebaseFirestore.getInstance() ;

        mAuth = FirebaseAuth.getInstance() ;

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation) ;

        if (mAuth.getCurrentUser() != null) {

            replaceFragment(homeFragment);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.bottom_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_notif:
                            replaceFragment(notifFragment);
                            return true;

                        case R.id.bottom_bookmarks:
                            replaceFragment(bookmarksFragment);
                            return true;

                        default:
                            return false;

                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if (currentUser==null) {

            sendToLogin() ;

        }

        else {

            currentUid = currentUser.getUid() ;

            firebaseFirestore.collection("Users").document(currentUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (!task.getResult().exists()) {
                            Intent detailsintent = new Intent(MainActivity.this,DetailsActivity.class) ;
                            startActivity(detailsintent) ;
                        }
                    }
                    else {

                        String error = task.getException().getMessage() ;
                        Toast.makeText(MainActivity.this,"Error : " + error,Toast.LENGTH_SHORT).show();
                        
                    }
                }
            });

        }
    }

    private void sendToLogin() {

        Intent login = new Intent(MainActivity.this,LoginActivity.class) ;
        startActivity(login) ;
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.add_activity) {

            startActivity(new Intent(MainActivity.this,PostActivity.class)) ;

        }

        else if (item.getItemId() == R.id.action_logout) {

            mAuth.signOut();
            sendToLogin();

        }

        else if (item.getItemId() == R.id.action_settings) {

            Intent sendToDetailsActivity = new Intent(MainActivity.this,DetailsActivity.class) ;
            startActivity(sendToDetailsActivity) ;

        }

        else if (item.getItemId() == R.id.action_search) {

            startActivity(new Intent(MainActivity.this,SearchingActivity.class)) ;

        }

        return true ;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction() ;
        fragmentTransaction.replace(R.id.main_container,fragment) ;
        fragmentTransaction.commit();

    }
}
