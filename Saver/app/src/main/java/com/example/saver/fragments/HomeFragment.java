package com.example.saver.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saver.Adapters.BillsAdapter;
import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Helper.Helper;
import com.example.saver.Interfaces.Clicklistener;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Bill;
import com.example.saver.Models.Transaction;
import com.example.saver.Models.User;
import com.example.saver.R;

import java.util.ArrayList;
import java.util.Calendar;

public class HomeFragment extends Fragment implements Clicklistener {

    private TextView textView_current_balance;
    private RecyclerView recyclerView_upcoming_bills;
    private ArrayList<Bill> list_upcoming_bills;
    private BillsAdapter billsAdapter;
    private CardView cardView_title;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        cardView_title = view.findViewById(R.id.car_home_title);
        textView_current_balance = view.findViewById(R.id.tv_current_balance_id);
        recyclerView_upcoming_bills = view.findViewById(R.id.rc_vw_upcoming_id);
        recyclerView_upcoming_bills.setLayoutManager(new LinearLayoutManager(getContext()));
        list_upcoming_bills = new ArrayList<>();
//        if (Helper.getTimeByIncrementMonth(Helper.getTimeStamp()) != null)
//            Log.d(TAG, "current date with next month   " + Helper.getTimeByIncrementMonth(Helper.getTimeStamp()));

        textView_current_balance.setText("$" + CurrentUser.getBalance(getContext()));
        float bl = Float.parseFloat(CurrentUser.getBalance(getContext()));
        try {
            if (bl < 500) {
                cardView_title.setBackgroundColor(getResources().getColor(R.color.red));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            int cur_mnt = Integer.parseInt(Helper.getCurrentMonth());
            int usr_mnt = Integer.parseInt(CurrentUser.getCurrentMonth(getContext()));
            int dif = cur_mnt - usr_mnt;
            if (dif > 0) {
                //save savings
                FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                    @Override
                    public void onResponse(Object... params) {
                        User user = (User) params[0];
                        user.setCurrent_month(Helper.getCurrentMonth());
                        float saving = 0f;
                        try {
                            saving = Float.parseFloat(user.getSavings());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        saving = saving + Float.parseFloat(user.getCurrent_balance());
                        user.setSavings(String.valueOf(saving));
                        user.setCurrent_balance(CurrentUser.getCurrentUserMonthlyIncome(getContext()));
                        //todo: transaction for balance transfer
                        Transaction transaction = new Transaction(user.getCurrent_balance(), "Monthly income", Helper.getTimeStamp(), "Monthly added");
                        transaction.setAddition(true);
                        transaction.setBill_id("no");
                        CurrentUser.setCurrentUser(getContext(), user);
                        FirebaseCalls.setUser(getContext(), user, new ResponceInterface() {
                            @Override
                            public void onResponse(Object... params) {
                                if ((boolean) params[0]) {
                                    FirebaseCalls.setTransaction(getContext(), transaction, new ResponceInterface() {
                                        @Override
                                        public void onResponse(Object... params) {
                                            if ((boolean) params[0]) {
                                                //set savings transaction
                                                Transaction transaction_savings = new Transaction(user.getSavings(), "Savings", Helper.getTimeStamp(), "Saving");
                                                transaction_savings.setAddition(true);
                                                FirebaseCalls.setSavings(getContext(), transaction_savings, new ResponceInterface() {
                                                    @Override
                                                    public void onResponse(Object... params) {
                                                        if ((boolean) params[0]) {
                                                            Toast.makeText(getContext(), "Savings added!!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onError(String error) {

                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fillList();


        return view;
    }


    private void fillList() {
        FirebaseCalls.getUpcomingBills(getContext(), new ResponceInterface() {
            @Override
            public void onResponse(Object... params) {
                list_upcoming_bills = (ArrayList<Bill>) params[0];
                billsAdapter = new BillsAdapter(list_upcoming_bills, getContext(), HomeFragment.this);
                recyclerView_upcoming_bills.setAdapter(billsAdapter);
            }

            @Override
            public void onError(String error) {
                Log.d(TAG, error);
            }
        });
    }

    @Override
    public void click(int position) {
        Log.d(TAG, "position clicked   ");
        textView_current_balance.setText("$"+CurrentUser.getBalance(getContext()));
    }

    @Override
    public void longClick(int position) {

    }
}