package com.example.saver.Adapters;

import static android.content.ContentValues.TAG;
import static android.content.Context.INPUT_METHOD_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.Clicklistener;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Bill;
import com.example.saver.Models.Transaction;
import com.example.saver.Models.User;
import com.example.saver.R;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.BillsViewHolder> {


    ArrayList<Bill> list_bills;
    Context context;
    Clicklistener clicklisener;

    public BillsAdapter(ArrayList<Bill> list_bills, Context context, Clicklistener clicklisener) {
        this.list_bills = list_bills;
        this.context = context;
        this.clicklisener = clicklisener;
    }

    @NonNull
    @Override
    public BillsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BillsViewHolder(LayoutInflater.from(context).inflate(R.layout.row_bill_design, parent, false), clicklisener);
    }

    @Override
    public void onBindViewHolder(@NonNull BillsViewHolder holder, int position) {
        holder.textView_date.setText(list_bills.get(position).getDate());
        holder.textView_description.setText(list_bills.get(position).getDescription());
        holder.amount.setText("$" + list_bills.get(position).getAmount());
        holder.bil_type.setText(list_bills.get(position).getType());

        if (list_bills.get(position).isMonthly()) {
            holder.textView_monthly.setText("Monthly");
        } else {
            holder.textView_monthly.setText("Once");
        }

        holder.imageView_del.setOnClickListener(view -> {
            FirebaseCalls.delBill(context, list_bills.get(position).getKey());
        });

        holder.itemView.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_design_bill, null);
            builder.setView(customLayout);
            AlertDialog dialog = builder.create();
            dialog.show();
            EditText editText_amount, editText_description, editText_type;
            TextView textView_date;
            Button button_add;
            CheckBox checkBox_paid, checkBox_monthly;

            editText_type = customLayout.findViewById(R.id.type_bill_add_doalog);
            checkBox_monthly = customLayout.findViewById(R.id.chcx_monthly_bill_add_daolog);
            checkBox_paid = customLayout.findViewById(R.id.chcx_paid_bill_add_daolog);
            textView_date = customLayout.findViewById(R.id.date_bill_add_doalog);
            editText_amount = customLayout.findViewById(R.id.amount_bill_add_doalog);
            editText_description = customLayout.findViewById(R.id.descri_bill_add_doalog);
            button_add = customLayout.findViewById(R.id.btn_bill_add_doalog);

            editText_type.setText(list_bills.get(position).getType());
            textView_date.setText(list_bills.get(position).getDate());
            editText_amount.setText(list_bills.get(position).getAmount());
            editText_description.setText(list_bills.get(position).getDescription());
            checkBox_monthly.setChecked(list_bills.get(position).isMonthly());
            checkBox_paid.setChecked(list_bills.get(position).isPaid());


            textView_date.setOnClickListener(view2 -> {
                final Calendar c = Calendar.getInstance();
                int mYear, mMonth, mDay;
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textView_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            });
            button_add.setOnClickListener(view2 -> {
                if (editText_amount.getText().length() == 0 || editText_description.getText().length() == 0 || textView_date.getText().length() == 0 || editText_type.getText().toString().length() == 0) {
                    Toast.makeText(context, "All Fields Are Required!!", Toast.LENGTH_SHORT).show();
                } else {
                    Bill bill = new Bill(editText_amount.getText().toString(), editText_description.getText().toString(), textView_date.getText().toString(), editText_type.getText().toString(), checkBox_monthly.isChecked(), checkBox_paid.isChecked());
                    bill.setKey(list_bills.get(position).getKey());
                    boolean paid = list_bills.get(position).isPaid();
                    int pos = position;

                    if (checkBox_paid.isChecked()) {
                        if (!paid) {
                            //paying bill here
                            FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                                @Override
                                public void onResponse(Object... params) {
                                    User user = (User) params[0];
                                    float pre_blnc = 0.0f;
                                    try {
                                        pre_blnc = Float.parseFloat(user.getCurrent_balance());
                                        pre_blnc = pre_blnc - Float.parseFloat(editText_amount.getText().toString());
                                        user.setCurrent_balance(String.valueOf(pre_blnc));
                                        CurrentUser.setBalance(context, user.getCurrent_balance());
                                        FirebaseCalls.setUser(context, user, new ResponceInterface() {
                                            @Override
                                            public void onResponse(Object... params) {
                                                if ((boolean) params[0]) {
                                                    FirebaseCalls.setBill(context, bill, new ResponceInterface() {
                                                        @Override
                                                        public void onResponse(Object... params) {
                                                            if ((boolean) params[0]) {
                                                                String bill_key = (String) params[1];
                                                                Transaction transaction = new Transaction(editText_amount.getText().toString(), editText_description.getText().toString(), textView_date.getText().toString(), editText_type.getText().toString());
                                                                transaction.setBill_id(bill_key);
                                                                transaction.setAddition(false);
                                                                FirebaseCalls.setTransaction(context, transaction, new ResponceInterface() {

                                                                    @Override
                                                                    public void onResponse(Object... params) {
                                                                        if ((boolean) params[0]) {
                                                                            Log.d(TAG, "context name is here " + clicklisener.getClass().toString());
                                                                            if (clicklisener.getClass().toString().contains("HomeFragment")) {
                                                                                list_bills.remove(pos);
                                                                                notifyDataSetChanged();
                                                                                clicklisener.click(pos);
                                                                            }
                                                                            Toast.makeText(context, "Added!!", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                        dialog.dismiss();
                                                                    }

                                                                    @Override
                                                                    public void onError(String error) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                                Toast.makeText(context, "Added!!", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        }

                                                        @Override
                                                        public void onError(String error) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onError(String error) {
                                                dialog.dismiss();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } catch (Exception e) {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Balance format is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onError(String error) {
                                    dialog.dismiss();
                                    Log.d(TAG, error);
                                }
                            });
                        } else {
                            updateBill(bill);
                            dialog.dismiss();
                        }
                    } else {
                        updateBill(bill);
                        dialog.dismiss();
                    }
                }
            });
        });
    }

    private void updateBill(Bill bill) {
        FirebaseCalls.setBill(context, bill, new ResponceInterface() {
            @Override
            public void onResponse(Object... params) {
               /* if ((boolean) params[0]) {

                    String bill_key = (String) params[1];
                    Transaction transaction = new Transaction(editText_amount.getText().toString(), editText_description.getText().toString(), textView_date.getText().toString(), editText_type.getText().toString());
                    transaction.setBill_id(bill_key);
                    transaction.setAddition(false);
                    FirebaseCalls.setTransaction(context, transaction, new ResponceInterface() {
                        @Override
                        public void onResponse(Object... params) {
                            if ((boolean) params[0]) {
                                Toast.makeText(context, "Added!!", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(String error) {
                            dialog.dismiss();
                        }
                    });
                    Toast.makeText(context, "Added!!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }*/
            }

            @Override
            public void onError(String error) {
                //dialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list_bills.size();
    }

    public class BillsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView bil_type, amount, textView_description, textView_date, textView_monthly;
        Clicklistener clicklisener;
        ImageView imageView_del;

        public BillsViewHolder(@NonNull View itemView, Clicklistener clicklisener) {
            super(itemView);
            bil_type = itemView.findViewById(R.id.type_bill_design);
            amount = itemView.findViewById(R.id.amount_bill_design);
            imageView_del = itemView.findViewById(R.id.del_bill);
            textView_description = itemView.findViewById(R.id.des_bill_design);
            textView_date = itemView.findViewById(R.id.date_bill_design);
            textView_monthly = itemView.findViewById(R.id.monthly_bill_design);
            this.clicklisener = clicklisener;
            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clicklisener.click(getAdapterPosition());
        }
    }

}
