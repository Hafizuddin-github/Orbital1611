package com.budgebars.rotelle.gui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.budgebars.rotelle.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class startActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_user_id;

    private BottomNavigationView mainbottomNav;

   // private FloatingActionButton addPostBtn;

    private SocialFragment socialFragment;
    private HomeFragment homeFragment;
    private IpptFragment ipptFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        mainToolbar = (Toolbar) findViewById(R.id.mainbar);
        setSupportActionBar(mainToolbar);

        getSupportActionBar().setTitle("  ");

        if(mAuth.getCurrentUser() != null) {

            mainbottomNav = findViewById(R.id.main_btm_bar);

            // FRAGMENTS
            socialFragment = new SocialFragment();
            homeFragment = new HomeFragment();
            ipptFragment = new IpptFragment();

            initializeFragment();

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                    switch (item.getItemId()) {

                        case R.id.home:

                            replaceFragment(homeFragment);
                            getSupportActionBar().setTitle("  ");
                            return true;

                        case R.id.ippt:

                            replaceFragment(ipptFragment);
                            getSupportActionBar().setTitle("Fitness Plan - IPPT");
                            return true;

                        case R.id.social:

                            replaceFragment(socialFragment);
                            getSupportActionBar().setTitle("Fitness Plan - Social");
                            return true;

                        default:
                            return false;


                    }

                }
            });


            /*addPostBtn = findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(startActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);

                }
            }); */

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){
            sendToLogin();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(startActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(startActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                    }

                }
            });

        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(startActivity.this, SetupActivity.class);
                startActivity(settingsIntent);

                return true;
            case R.id.exercise_settings:

                Intent exerciseIntent = new Intent(startActivity.this, SettingPage.class);
                startActivity(exerciseIntent);

                return true;


            default:
                return false;


        }

    }

    private void logOut() {

        mAuth.signOut();
        sendToLogin();
    }
    private void sendToLogin() {

        Intent loginIntent = new Intent(startActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, homeFragment);
        fragmentTransaction.add(R.id.main_container, socialFragment);
        fragmentTransaction.add(R.id.main_container, ipptFragment);

        fragmentTransaction.hide(socialFragment);
        fragmentTransaction.hide(ipptFragment);

        fragmentTransaction.commit();

    }

    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == homeFragment){

            fragmentTransaction.hide(ipptFragment);
            fragmentTransaction.hide(socialFragment);

        }

        if(fragment == ipptFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(socialFragment);

        }

        if(fragment == socialFragment){

            fragmentTransaction.hide(homeFragment);
            fragmentTransaction.hide(ipptFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }




}
