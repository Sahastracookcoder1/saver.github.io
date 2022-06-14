package com.example.saver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.User;

public class ProfileSettingActivity extends AppCompatActivity {

    private EditText editText_name, editText_email, editText_monthly_inicome;
    private Button button_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        LinearLayout layout = findViewById(R.id.li_update);

        layout.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        });
        editText_name = findViewById(R.id.username_update);
        editText_email = findViewById(R.id.email_update);
        editText_monthly_inicome = findViewById(R.id.et_monthly_income_update);
        button_update = findViewById(R.id.btn_update_id);
        setData();

        editText_email.setFocusable(false);
        editText_email.setClickable(false);

        button_update.setOnClickListener(view -> {
            if (editText_name.getText().toString().length() > 1 && editText_monthly_inicome.getText().toString().length() > 0) {
                FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                    @Override
                    public void onResponse(Object... params) {
                        User user = (User) params[0];
                        float a = Float.parseFloat(editText_monthly_inicome.getText().toString());
                        user.setMonthly_income(String.valueOf(a));
                        user.setName(editText_name.getText().toString());
                        FirebaseCalls.setUser(ProfileSettingActivity.this, user, new ResponceInterface() {
                            @Override
                            public void onResponse(Object... params) {
                                if ((boolean) params[0]) {
                                    CurrentUser.setCurrentUser(ProfileSettingActivity.this, user);
                                    Toast.makeText(ProfileSettingActivity.this, "updated successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Log.d(TAG, error);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.d(TAG, error);
                    }
                });
            } else {
                Toast.makeText(this, "invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setData() {
        editText_name.setText(CurrentUser.getName(this));
        editText_email.setText(CurrentUser.getEmail(this));
        editText_monthly_inicome.setText(CurrentUser.getCurrentUserMonthlyIncome(this));
    }
}