package com.example.saver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Helper.Helper;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.User;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {

    private Runnable runnable;
    private Handler handler;
    private CardView imageView_logo;
    private final static int SPLASH_MAIN_DURATION = 1000;

    @Override
    protected void onStart() {
        super.onStart();
        if (handler != null && runnable != null) {
            handler.postDelayed(runnable, SPLASH_MAIN_DURATION);
        }
        ObjectAnimator animation = ObjectAnimator.ofFloat(imageView_logo, "translationY", 200f);
        animation.setDuration(1000);
        animation.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        imageView_logo = findViewById(R.id.iv_splash_logo);

        handler = new Handler();
        runnable = () -> {
            //if (isSMSPermesionGranted) {
            if (Helper.isInternetAvailable(SplashActivity.this)) {
                Intent intent1;
                if (Helper.isUserAvailable()) {
                    FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            CurrentUser.setCurrentUser(SplashActivity.this, (User) params[0]);
                            Intent intent;
                            String nm = CurrentUser.getName(SplashActivity.this);
                            Log.d(TAG, "user name is   " + nm);
                            try {
                                if (!nm.equalsIgnoreCase("")) {
                                    intent = new Intent(SplashActivity.this, MainActivity.class);
                                } else {
                                    intent = new Intent(SplashActivity.this, RegisterActivity.class);
                                    Log.d("check null state", "login or not");
                                }
                            } catch (Exception e) {
                                intent = new Intent(SplashActivity.this, RegisterActivity.class);
                                Log.d("Splash 107", "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=--=-=-=-=-=-=" + e.getMessage());
                            }
                            startActivity(intent);
                            stopHandler();
                            finish();
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(SplashActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    intent1 = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent1);
                    stopHandler();
                    finish();
                    Toast.makeText(this, "Login please", Toast.LENGTH_SHORT).show();
                }

            } else {
                handler.postDelayed(runnable, SPLASH_MAIN_DURATION);
                Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        };
        handler.postDelayed(runnable, SPLASH_MAIN_DURATION);

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if (handler != null)
        // handler.postDelayed(runnable, 1000);
    }

    private void stopHandler() {
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(null);
        handler.removeMessages(0);
    }

}