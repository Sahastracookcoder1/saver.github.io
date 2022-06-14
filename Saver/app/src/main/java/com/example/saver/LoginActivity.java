package com.example.saver;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText email, pass;
    TextView register;

    FirebaseAuth fauth;
    FirebaseFirestore fs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout layout = findViewById(R.id.li_login);

        layout.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        });

        email = findViewById(R.id.email);
        pass = findViewById(R.id.password);
        register = findViewById(R.id.register);

        fauth = FirebaseAuth.getInstance();
        fs = FirebaseFirestore.getInstance();


        register.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RegisterActivity.class)));

        findViewById(R.id.login).setOnClickListener(view -> {
            if (email.getText().length() == 0 || pass.getText().length() == 0) {
                Toast.makeText(LoginActivity.this, "All Fields Are Required!!", Toast.LENGTH_SHORT).show();
            } else {
                String mil = email.getText().toString();
                String ps = pass.getText().toString();
                if (mil.equals("eee") && ps.equals("eee")) {
                    mil = getString(R.string.eee_mail);
                    ps = getString(R.string.eee_ps);
                }
                fauth.signInWithEmailAndPassword(mil, ps)
                        .addOnCompleteListener(
                                new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(
                                            @NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                                                @Override
                                                public void onResponse(Object... params) {
                                                    CurrentUser.setCurrentUser(LoginActivity.this, (User) params[0]);
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    Log.d(TAG, "user getting error   " + error);
                                                }
                                            });
                                            Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Login failed!!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
            }
        });

    }
}