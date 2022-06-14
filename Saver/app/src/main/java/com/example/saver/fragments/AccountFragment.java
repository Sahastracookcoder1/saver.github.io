package com.example.saver.fragments;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.saver.GraphActivity;
import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.LoginActivity;
import com.example.saver.Models.User;
import com.example.saver.ProfileSettingActivity;
import com.example.saver.R;
import com.example.saver.WebviewActivity;

import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class AccountFragment extends Fragment {
    Button logout;
    TextView username;
    View view;
    ImageView imageView_add_prifile_pic, imageView_show;
    Button button_help, button_statics;
    LinearLayout layout_profile_settings;
    SpotsDialog spotsDialog;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        logout = view.findViewById(R.id.logout);
        spotsDialog = new SpotsDialog(getContext());
        button_help = view.findViewById(R.id.btn_help_id);
        username = view.findViewById(R.id.username_show);
        imageView_show = view.findViewById(R.id.iv_show_img);
        button_statics = view.findViewById(R.id.btn_statics_id);
        layout_profile_settings = view.findViewById(R.id.profile_id);
        imageView_add_prifile_pic = view.findViewById(R.id.im_add_img);
        Log.d(TAG, "name " + CurrentUser.getName(getContext()));
        Log.d(TAG, "email " + CurrentUser.getEmail(getContext()));
        username.setText(CurrentUser.getName(getContext()));
        logout.setOnClickListener(view -> {
            CurrentUser.signOut(getContext());
            startActivity(new Intent(getContext(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            getActivity().finish();
        });
        button_statics.setOnClickListener(view1 -> startActivity(new Intent(getContext(), GraphActivity.class)));

        if (!CurrentUser.getPic(getContext()).equals("")) {
            Glide.with(this)
                    .load(CurrentUser.getPic(getContext()))
                    .placeholder(R.drawable.user)
                    .into(imageView_show);
        }
        imageView_add_prifile_pic.setOnClickListener(view -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            someActivityResultLauncher.launch(photoPickerIntent);
        });
        button_help.setOnClickListener(view -> startActivity(new Intent(getContext(), WebviewActivity.class)));
        layout_profile_settings.setOnClickListener(view1 -> {
            startActivity(new Intent(getContext(), ProfileSettingActivity.class));
        });
        return view;
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                spotsDialog.show();
                //Activity.RESULT_OK
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    imageView_show.setImageURI(data.getData());

                    FirebaseCalls.upLoadImage(getContext(), data.getData(), FirebaseCalls.profile_name, FirebaseCalls.ref_profile_image, new ResponceInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            String img_dnld_uri = (String) params[0];
                            FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                                @Override
                                public void onResponse(Object... params) {
                                    User user = (User) params[0];
                                    user.setPic(img_dnld_uri);
                                    CurrentUser.setPic(getContext(), img_dnld_uri);
                                    FirebaseCalls.setUser(getContext(), user, new ResponceInterface() {
                                        @Override
                                        public void onResponse(Object... params) {
                                            if ((boolean) params[0]) {
                                                spotsDialog.dismiss();
                                                Toast.makeText(getContext(), "image uploaded successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            spotsDialog.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    spotsDialog.dismiss();
                                    Log.d(TAG, "error---get current user----------- " + error);
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            spotsDialog.dismiss();
                            Toast.makeText(getContext(), "image uploading failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
}