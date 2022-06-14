package com.example.saver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.saver.Helper.Helper;
import com.example.saver.fragments.AccountFragment;
import com.example.saver.fragments.AddorRemoveFragment;
import com.example.saver.fragments.BillsFragment;
import com.example.saver.fragments.HomeFragment;
import com.example.saver.fragments.SavingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fs;
    Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseMessaging.getInstance().subscribeToTopic("all")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("MainActivity", "push notification running successfully");
                    } else {
                        Log.d("MainActivity", "failed");
                    }
                });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*if (Helper.isUserAvailable()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }*/

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.home);
        fragment = new HomeFragment();
        loadFragment(fragment);
        navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.home:
                        fragment = new HomeFragment();
                        loadFragment(fragment);
                        return true;
                    case R.id.add:
                        fragment = new AddorRemoveFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.saving:
                        fragment = new SavingsFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.bill:
                        fragment = new BillsFragment();
                        loadFragment(fragment);
                        return true;

                    case R.id.acc:
                        fragment = new AccountFragment();
                        loadFragment(fragment);
                        return true;
                }
                return false;
            }
        });
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}