package com.example.saver.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saver.Adapters.TransactionAdapter;
import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.Clicklistener;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Bill;
import com.example.saver.Models.Transaction;
import com.example.saver.Models.User;
import com.example.saver.R;

import java.util.ArrayList;
import java.util.Calendar;

import dmax.dialog.SpotsDialog;

public class AddorRemoveFragment extends Fragment implements Clicklistener {

    private ImageView imageView_add_transaction, imageView_remove_transaction;
    private RecyclerView recyclerView_transactions;
    private ArrayList<Transaction> list_transaction;
    private TransactionAdapter transactionAdapter;
    private SpotsDialog spotsDialog;

    public AddorRemoveFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_addor_remove, container, false);
        imageView_add_transaction = view.findViewById(R.id.add_transaction);
        imageView_remove_transaction = view.findViewById(R.id.remove_transaction);
        recyclerView_transactions = view.findViewById(R.id.recycler_view_transactions);
        recyclerView_transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        spotsDialog = new SpotsDialog(getContext());

        imageView_add_transaction.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_design_bill, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialog.show();
            EditText editText_amount, editText_description, editText_type;
            TextView textView_date, textView_title;
            Button button_add;
            CheckBox checkBox_paid, checkBox_monthly;
            textView_title = customLayout.findViewById(R.id.tv_dialog_title);
            textView_title.setText("Transaction to add income");
            editText_type = customLayout.findViewById(R.id.type_bill_add_doalog);
            checkBox_monthly = customLayout.findViewById(R.id.chcx_monthly_bill_add_daolog);
            checkBox_paid = customLayout.findViewById(R.id.chcx_paid_bill_add_daolog);
            textView_date = customLayout.findViewById(R.id.date_bill_add_doalog);
            checkBox_paid.setVisibility(View.GONE);
            checkBox_monthly.setVisibility(View.GONE);
            editText_amount = customLayout.findViewById(R.id.amount_bill_add_doalog);
            editText_description = customLayout.findViewById(R.id.descri_bill_add_doalog);
            button_add = customLayout.findViewById(R.id.btn_bill_add_doalog);
            textView_date.setOnClickListener(view22 -> {
                final Calendar c = Calendar.getInstance();
                int mYear, mMonth, mDay;
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (view221, year, monthOfYear, dayOfMonth) -> textView_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
                datePickerDialog.show();
            });
            button_add.setOnClickListener(view2 -> {
                if (editText_amount.getText().length() == 0 || editText_description.getText().length() == 0 || textView_date.getText().length() == 0 || editText_type.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "All Fields Are Required!!", Toast.LENGTH_SHORT).show();
                } else {
                    spotsDialog.show();
                    FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            User user = (User) params[0];
                            float pre_blnc = 0.0f;
                            try {
                                pre_blnc = Float.parseFloat(user.getCurrent_balance());
                                pre_blnc = pre_blnc + Float.parseFloat(editText_amount.getText().toString());
                                user.setCurrent_balance(String.valueOf(pre_blnc));
                                CurrentUser.setBalance(getContext(), user.getCurrent_balance());
                                FirebaseCalls.setUser(getContext(), user, new ResponceInterface() {
                                    @Override
                                    public void onResponse(Object... params) {
                                        if ((boolean) params[0]) {
                                            Transaction transaction = new Transaction(editText_amount.getText().toString(), editText_description.getText().toString(), textView_date.getText().toString(), editText_type.getText().toString());
                                            transaction.setBill_id("no");
                                            transaction.setAddition(true);
                                            FirebaseCalls.setTransaction(getContext(), transaction, new ResponceInterface() {
                                                @Override
                                                public void onResponse(Object... params) {
                                                    if ((boolean) params[0]) {
                                                        Toast.makeText(getContext(), "Added!!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        spotsDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    spotsDialog.dismiss();
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        spotsDialog.dismiss();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                spotsDialog.dismiss();
                                Toast.makeText(getContext(), "Balance format is incorrect", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(String error) {
                            spotsDialog.dismiss();
                            Log.d(TAG, error);
                        }
                    });
                }
            });
        });
        imageView_remove_transaction.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final View customLayout = getLayoutInflater().inflate(R.layout.dialog_design_bill, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialog.show();
            EditText editText_amount, editText_description, editText_type;
            TextView textView_date, textView_title;
            Button button_add;
            CheckBox checkBox_paid, checkBox_monthly;
            textView_title = customLayout.findViewById(R.id.tv_dialog_title);
            textView_title.setText("Transaction for paying");
            editText_type = customLayout.findViewById(R.id.type_bill_add_doalog);
            checkBox_monthly = customLayout.findViewById(R.id.chcx_monthly_bill_add_daolog);
            checkBox_paid = customLayout.findViewById(R.id.chcx_paid_bill_add_daolog);
            textView_date = customLayout.findViewById(R.id.date_bill_add_doalog);
            checkBox_paid.setVisibility(View.GONE);
            checkBox_monthly.setVisibility(View.GONE);
            editText_amount = customLayout.findViewById(R.id.amount_bill_add_doalog);
            editText_description = customLayout.findViewById(R.id.descri_bill_add_doalog);
            button_add = customLayout.findViewById(R.id.btn_bill_add_doalog);
            textView_date.setOnClickListener(view22 -> {
                final Calendar c = Calendar.getInstance();
                int mYear, mMonth, mDay;
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (view221, year, monthOfYear, dayOfMonth) -> textView_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);
                datePickerDialog.show();
            });
            button_add.setOnClickListener(view2 -> {
                if (editText_amount.getText().length() == 0 || editText_description.getText().length() == 0 || textView_date.getText().length() == 0 || editText_type.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "All Fields Are Required!!", Toast.LENGTH_SHORT).show();
                } else {
                    spotsDialog.show();
                    FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            User user = (User) params[0];
                            float pre_blnc = 0.0f;
                            try {
                                pre_blnc = Float.parseFloat(user.getCurrent_balance());
                                pre_blnc = pre_blnc - Float.parseFloat(editText_amount.getText().toString());
                                user.setCurrent_balance(String.valueOf(pre_blnc));
                                CurrentUser.setBalance(getContext(), user.getCurrent_balance());
                                FirebaseCalls.setUser(getContext(), user, new ResponceInterface() {
                                    @Override
                                    public void onResponse(Object... params) {
                                        if ((boolean) params[0]) {
                                            Transaction transaction = new Transaction(editText_amount.getText().toString(), editText_description.getText().toString(), textView_date.getText().toString(), editText_type.getText().toString());
                                            transaction.setBill_id("no");
                                            transaction.setAddition(false);
                                            FirebaseCalls.setTransaction(getContext(), transaction, new ResponceInterface() {
                                                @Override
                                                public void onResponse(Object... params) {
                                                    if ((boolean) params[0]) {
                                                        Toast.makeText(getContext(), "Added!!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        spotsDialog.dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onError(String error) {
                                                    spotsDialog.dismiss();
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onError(String error) {
                                        spotsDialog.dismiss();
                                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (Exception e) {
                                spotsDialog.dismiss();
                                Toast.makeText(getContext(), "Balance format is incorrect", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(String error) {
                            spotsDialog.dismiss();
                            Log.d(TAG, error);
                        }
                    });
                }
            });
        });
        fillList();
        return view;
    }

    private void fillList() {
        FirebaseCalls.getAllTransactions(getContext(), new ResponceInterface() {
            @Override
            public void onResponse(Object... params) {
                list_transaction = (ArrayList<Transaction>) params[0];
                transactionAdapter = new TransactionAdapter(getContext(), list_transaction, AddorRemoveFragment.this);
                recyclerView_transactions.setAdapter(transactionAdapter);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void click(int position) {

    }

    @Override
    public void longClick(int position) {

    }
}