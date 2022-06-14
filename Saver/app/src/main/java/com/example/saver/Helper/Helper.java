package com.example.saver.Helper;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    public static String getTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        return s.format(new Date());
    }

    public static String getTimeForDiff() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        return s.format(new Date());
    }

    public static String getCurrentMonth() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("MM");
        return s.format(new Date());
    }

    public static float getMonthFromDate(String date) {
        float the_month = 0f;
        try {
            Log.d(TAG, "date is   " + date);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = simpleDateFormat.parse(date);
            String a = (String) DateFormat.format("MM", date1);
            the_month = Float.parseFloat(a);
            Log.d(TAG, "c   " + the_month);
        } catch (Exception e) {
            Log.d(TAG, "pars date exception  " + e.getMessage());
        }
        return the_month;
    }

    public static String getTimeByIncrementMonth(String date) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        Date date1;
        try {
            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(date);
            calendar.setTime(date1);
            calendar.add(Calendar.MONTH, 1);
            Date newDate = calendar.getTime();
            return s.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    public static boolean isUserAvailable() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static float getDayFromDate(String date) {
        float the_day = 1;
        try {
            Log.d(TAG, "date is   " + date);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = simpleDateFormat.parse(date);
            String a = (String) DateFormat.format("dd", date1);
            the_day = Float.parseFloat(a);
            Log.d(TAG, "c   " + the_day);

        } catch (Exception e) {
            Log.d(TAG, "pars date exception  " + e.getMessage());
        }

        return the_day;
    }
}
