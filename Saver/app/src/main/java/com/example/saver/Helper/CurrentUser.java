package com.example.saver.Helper;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.saver.Models.User;
import com.google.firebase.auth.FirebaseAuth;

public class CurrentUser {

    //User local database

    private static final String CURRENT_USER_EMAIL = "CurrentUserEmail";
    private static final String CURRENT_USER_NAME = "CurrentUserName";
    private static final String CURRENT_USER_JOINING_DATE = "CurrentUserJoiningDate";
    private static final String CURRENT_MONTH = "CurrentMonth";
    private static final String CURRENT_USER_SAVINGS = "CurrentUserSaving";
    public static final String CURRENT_USER_BALANCE = "CurrentUserBalance";
    private static final String CURRENT_USER_PIC = "CurrentUserPic";
    private static final String CURRENT_USER_MONTHLY_INCOME = "CurrentMonthlyIncome";
    /////////////////////////////////////////////////////////////////////////////////
    private static final String UserEmail = "UEmail";
    private static final String UserName = "UName";
    private static final String UserJoiningDate = "jdate";
    private static final String UserCurrentMonth = "cMonth";
    private static final String UserCurrentSavings = "cSavings";
    public static final String UserBalance = "uBlanace";
    private static final String UserPic = "uPic";
    private static final String UserMonthlyIncome = "mIncome";


    public static void setName(Context context, String name) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_NAME,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserName, name);
        sharedPref.apply();
    }

    public static String getName(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_NAME,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserName, "");
    }

    public static void setEmail(Context context, String email) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_EMAIL,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserEmail, email);
        sharedPref.apply();
    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_EMAIL,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserEmail, "");
    }

    public static void setCurrentUserJoiningDate(Context context, String joiningDate) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_JOINING_DATE,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserJoiningDate, joiningDate);
        sharedPref.apply();
    }

    public static String getCurrentUserJoiningDate(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_JOINING_DATE,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserJoiningDate, "");
    }

    public static void setCurrentMonth(Context context, String month) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_MONTH,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserCurrentMonth, month);
        sharedPref.apply();
    }

    public static String getCurrentMonth(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_MONTH,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserCurrentMonth, "");
    }

    public static void setCurrentUserSavings(Context context, String saving) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_SAVINGS,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserCurrentSavings, saving);
        sharedPref.apply();
    }

    public static String getCurrentUserSavings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_SAVINGS,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserCurrentSavings, "0");
    }

    public static void setBalance(Context context, String balance) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_BALANCE,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserBalance, balance);
        sharedPref.apply();
    }

    public static String getBalance(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_BALANCE,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserBalance, "0");
    }

    public static String getUserId() {
        if (Helper.isUserAvailable()) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        return "null";
    }

    public static void signOut(Context context) {
        if (Helper.isUserAvailable()) {
            setBalance(context, "0");
            setCurrentUserMonthlyIncome(context, "");
            setCurrentUserJoiningDate(context, "");
            setPic(context, "");
            setCurrentMonth(context, "");
            setEmail(context, "");
            setName(context, "");
            setCurrentUserSavings(context, "0");

            FirebaseAuth.getInstance().signOut();
        }
    }

    public static void setPic(Context context, String pic) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_PIC,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserPic, pic);
        sharedPref.apply();
    }

    public static String getPic(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_PIC,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserPic, "");
    }

    public static void setCurrentUserMonthlyIncome(Context context, String mnthly) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(CURRENT_USER_MONTHLY_INCOME,
                Context.MODE_PRIVATE).edit();
        sharedPref.putString(UserMonthlyIncome, mnthly);
        sharedPref.apply();
    }

    public static String getCurrentUserMonthlyIncome(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(CURRENT_USER_MONTHLY_INCOME,
                Context.MODE_PRIVATE);
        return sharedPref.getString(UserMonthlyIncome, "0");
    }

  /*  public static ArrayList<VehicleData> getVehicleCategories(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(RIDE_CATEGORIES,
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPref.getString(rideCategories, null);
        Type type = new TypeToken<ArrayList<VehicleData>>() {}.getType();
        System.out.println("vehicle data is her");
        System.out.println(json);
        return gson.fromJson(json, type);               //Ride.class
    }
*/

    /*public static void setVehicleCategories(Context context, ArrayList<VehicleData> list_vehicle) {
        SharedPreferences.Editor sharedPref = context.getSharedPreferences(RIDE_CATEGORIES,
                Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(list_vehicle);
        System.out.println("vehicle data is saving here");
        sharedPref.putString(rideCategories, json);
        sharedPref.apply();
    }*/


    public static void setCurrentUser(Context context, User user) {
        CurrentUser.setCurrentUserSavings(context, user.getSavings());
        CurrentUser.setCurrentUserJoiningDate(context, user.getJoiningDate());
        CurrentUser.setBalance(context, user.getCurrent_balance());
        CurrentUser.setCurrentMonth(context, user.getCurrent_month());
        CurrentUser.setName(context, user.getName());
        CurrentUser.setEmail(context, user.getEmail());
        CurrentUser.setPic(context, user.getPic());
        CurrentUser.setCurrentUserMonthlyIncome(context, user.getMonthly_income());
    }

}
