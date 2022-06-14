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
import com.example.saver.Helper.Helper;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Transaction;
import com.example.saver.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, pass, editText_momthly_income;
    TextView login;
    FirebaseAuth fauth;
    SpotsDialog spotsDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        spotsDialog = new SpotsDialog(this);
        LinearLayout layout = findViewById(R.id.regi_lay);

        layout.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        });
        username = findViewById(R.id.username);
        email = findViewById(R.id.email_reg);
        pass = findViewById(R.id.password_reg);
        login = findViewById(R.id.login_reg);
        editText_momthly_income = findViewById(R.id.et_monthly_income);

        fauth = FirebaseAuth.getInstance();


        login.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        findViewById(R.id.register).setOnClickListener(view -> {
            spotsDialog.show();
            if (username.getText().length() == 0 || email.getText().length() == 0 || pass.getText().length() == 0) {
                Toast.makeText(RegisterActivity.this, "All Fields Are Required!!", Toast.LENGTH_SHORT).show();
                spotsDialog.dismiss();
            } else {
                float in;
                try {
                    in = Float.parseFloat(editText_momthly_income.getText().toString());
                    String mail = email.getText().toString();
                    String ps = pass.getText().toString();
                    if (mail.equals("eee") && ps.equals("eee")) {
                        mail = getString(R.string.eee_mail);
                        ps = getString(R.string.eee_ps);
                    }
                    fauth.createUserWithEmailAndPassword(mail, ps).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String userId = task.getResult().getUser().getUid();
                                User user = new User();
                                user.setCurrent_balance(String.valueOf(in));
                                user.setUid(userId);
                                user.setMonthly_income(String.valueOf(in));
                                user.setEmail(email.getText().toString());
                                user.setPassword(pass.getText().toString());
                                user.setCurrent_month(Helper.getCurrentMonth());
                                user.setName(username.getText().toString());
                                user.setSavings("0"); //add field
                                user.setJoiningDate(Helper.getTimeStamp());
                                user.setPic("");//add in UI
                                //uplaod on firebase here
                                FirebaseCalls.setUser(RegisterActivity.this, user, new ResponceInterface() {
                                    @Override
                                    public void onResponse(Object... params) {
                                        if ((boolean) params[0]) {
                                            if (in > 0) {
                                                Transaction transaction = new Transaction(user.getCurrent_balance(), "While registration", Helper.getTimeForDiff(), "First deposit");
                                                transaction.setAddition(true);
                                                transaction.setBill_id("on");
                                                FirebaseCalls.setTransaction(RegisterActivity.this, transaction, new ResponceInterface() {
                                                    @Override
                                                    public void onResponse(Object... params) {
                                                        spotsDialog.dismiss();
                                                        Toast.makeText(getApplicationContext(), "user registration successful", Toast.LENGTH_LONG).show();
                                                        CurrentUser.setCurrentUser(RegisterActivity.this, user);
                                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onError(String error) {
                                                        spotsDialog.dismiss();
                                                    }
                                                });

                                            } else {
                                                spotsDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "user registration successful", Toast.LENGTH_LONG).show();
                                                CurrentUser.setCurrentUser(RegisterActivity.this, user);
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }


                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        spotsDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "user registration failed due to " + error, Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                spotsDialog.dismiss();
                                Log.d(TAG, "error 95" + task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Registration failed!! \n" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    spotsDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}