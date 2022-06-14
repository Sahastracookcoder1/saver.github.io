package com.example.saver.Helper;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Bill;
import com.example.saver.Models.Transaction;
import com.example.saver.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class FirebaseCalls {

    private static final DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("Users");
    private static final DatabaseReference ref_bills = FirebaseDatabase.getInstance().getReference("Bills");
    private static final DatabaseReference ref_transaction = FirebaseDatabase.getInstance().getReference("Transaction");
    private static final DatabaseReference ref_savings = FirebaseDatabase.getInstance().getReference("Savings");

    public static final String profile_name = "profile_pic";
    public static final StorageReference ref_profile_image = FirebaseStorage.getInstance().getReference("Users").child(CurrentUser.getUserId());

    public static void setUser(Context context, User user, ResponceInterface responceInterface) {
        ref_user.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
            responceInterface.onResponse(task.isSuccessful());
            if (!task.isSuccessful()) {
                responceInterface.onError(task.getException().toString());
            }
        });
    }

    public static void getCurrentUser(String uid, ResponceInterface responceInterface) {
        ref_user.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    responceInterface.onResponse(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                responceInterface.onError(error.getMessage());
            }
        });
    }


    public static void upLoadImage(Context context, Uri img_uri, final String st_for_img_name_in_firebase, StorageReference reference, ResponceInterface responceInterface) {
        StorageTask storageTask;
        if (img_uri != null || st_for_img_name_in_firebase != null) {
            final StorageReference storage_Reference = reference.child(st_for_img_name_in_firebase + ".jpg");
            storageTask = storage_Reference.putFile(img_uri);
            storageTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storage_Reference.getDownloadUrl();
                }
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {

                if (task.isSuccessful()) {
                    responceInterface.onResponse(task.getResult().toString());
                } else {
                    responceInterface.onError(Objects.requireNonNull(task.getException()).toString());
                }
            });
        }

    }

    public static void setBill(Context context, Bill bill, ResponceInterface responceInterface) {
        if (bill.getKey() == null) {
            bill.setKey(ref_bills.push().getKey());
        }
        ref_bills.child(CurrentUser.getUserId()).child(bill.getKey()).setValue(bill).addOnCompleteListener(task -> {
            responceInterface.onResponse(task.isSuccessful(), bill.getKey());
            if (!task.isSuccessful()) {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(task.getException().getMessage());
            }
        });
    }

    public static void getAllBills(Context context, ResponceInterface responceInterface) {
        ArrayList<Bill> list_bills = new ArrayList<>();
        ref_bills.child(CurrentUser.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list_bills.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list_bills.add(dataSnapshot.getValue(Bill.class));
                    }
                }
                responceInterface.onResponse(list_bills);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(error.getMessage());
            }
        });
    }

    public static void delBill(Context context, String key) {
        ref_bills.child(CurrentUser.getUserId()).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Deleted!!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public static void getUpcomingBills(Context context, ResponceInterface responceInterface) {
        ArrayList<Bill> list_bills = new ArrayList<>();
        ArrayList<Bill> list_bills_have_to_pay = new ArrayList<>();
        ref_bills.child(CurrentUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            Bill bill;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_bills.clear();
                list_bills_have_to_pay.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        bill = null;
                        bill = dataSnapshot.getValue(Bill.class);
                        Log.d(TAG, "time diff is   " + getTimeDifference(Helper.getTimeForDiff(), dataSnapshot.getValue(Bill.class).getDate()));
                        float dif = getTimeDifference(Helper.getTimeForDiff(), bill.getDate());
                        if (dif >= 0) {
                            if (bill.isMonthly()) {
                                Log.d("YSH", "monthlyyyyyy");
                                if (!bill.isPaid()) {
                                    Log.d("YSH", "un paiddddd");
                                    list_bills.add(bill);
                                }
                            }
                        }
                        if (dif < 0) {
                            if (!bill.isPaid()) {
                                Log.d("tag", "167 paying add");
                                // todo: pay these bills now
                                //list_bills_have_to_pay.add(bill);
                                FirebaseCalls.getCurrentUser(CurrentUser.getUserId(), new ResponceInterface() {
                                    @Override
                                    public void onResponse(Object... params) {
                                        User user = (User) params[0];
                                        float pre_blnc = 0.0f;
                                        try {
                                            pre_blnc = Float.parseFloat(user.getCurrent_balance());
                                            pre_blnc = pre_blnc - Float.parseFloat(bill.getAmount());
                                            user.setCurrent_balance(String.valueOf(pre_blnc));
                                            CurrentUser.setBalance(context, user.getCurrent_balance());
                                            FirebaseCalls.setUser(context, user, new ResponceInterface() {
                                                @Override
                                                public void onResponse(Object... params) {
                                                    if ((boolean) params[0]) {
                                                        Transaction transaction = new Transaction(bill.getAmount(), "Auto bill paid", Helper.getTimeStamp(), "Bill pay");
                                                        transaction.setBill_id(bill.getKey());
                                                        transaction.setAddition(false);
                                                        FirebaseCalls.setTransaction(context, transaction, new ResponceInterface() {
                                                            @Override
                                                            public void onResponse(Object... params) {
                                                                if ((boolean) params[0]) {
                                                                    bill.setPaid(true);
                                                                    FirebaseCalls.setBill(context, bill, new ResponceInterface() {
                                                                        @Override
                                                                        public void onResponse(Object... params) {
                                                                            if ((boolean) params[0]) {
                                                                                Toast.makeText(context, "previous bill paid now!!", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onError(String error) {
                                                                            Toast.makeText(context, "previous bill paid failed!!", Toast.LENGTH_SHORT).show();
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
                                        } catch (Exception e) {
                                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onError(String error) {
                                        Log.d(TAG, error);
                                    }
                                });
                            }
                        }

                        if (bill.isMonthly()) {
                            if (bill.isPaid()) {
                                if (Helper.getMonthFromDate(bill.getDate()) < Helper.getMonthFromDate(Helper.getTimeStamp())) {
                                    // set as unpaid with new date of this month
                                    bill.setDate(Helper.getTimeByIncrementMonth(bill.getDate()));
                                    bill.setPaid(false);
                                    FirebaseCalls.setBill(context, bill, new ResponceInterface() {
                                        @Override
                                        public void onResponse(Object... params) {
                                            if ((boolean) params[0]) {
                                                Log.d(TAG, "set the bill as unpaid with new date " + bill.getDate());
                                            }
                                        }

                                        @Override
                                        public void onError(String error) {
                                            Log.d(TAG, "set the bill as unpaid exception" + error);
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
                responceInterface.onResponse(list_bills, list_bills_have_to_pay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(error.getMessage());
            }
        });
    }


    public static float getTimeDifference(String init, String finl) {
        try {
            //@SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

            Date startDate = simpleDateFormat.parse(init);
            Date endDate = simpleDateFormat.parse(finl);

            assert endDate != null;
            assert startDate != null;
            long difference = endDate.getTime() - startDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                assert dateMax != null;
                assert dateMin != null;
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
            float days = (int) (difference / (1000 * 60 * 60 * 24));
            float hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            //return Float.parseFloat(new DecimalFormat("##.##").format((difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60)));
            return days;

        } catch (ParseException e) {
            e.fillInStackTrace();
            return 0.0f;
        }
    }


    public static void setTransaction(Context context, Transaction transaction, ResponceInterface responceInterface) {
        transaction.setKey(ref_bills.push().getKey());
        ref_transaction.child(CurrentUser.getUserId()).child(transaction.getKey()).setValue(transaction).addOnCompleteListener(task -> {
            responceInterface.onResponse(task.isSuccessful());
            if (!task.isSuccessful()) {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(task.getException().getMessage());
            }
        });
    }


    public static void getAllTransactions(Context context, ResponceInterface responceInterface) {
        ArrayList<Transaction> list_bills = new ArrayList<>();
        ref_transaction.child(CurrentUser.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list_bills.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list_bills.add(dataSnapshot.getValue(Transaction.class));
                    }
                }
                responceInterface.onResponse(list_bills);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(error.getMessage());
            }
        });
    }


    public static void setSavings(Context context, Transaction transaction, ResponceInterface responceInterface) {
        transaction.setKey(ref_bills.push().getKey());
        ref_savings.child(CurrentUser.getUserId()).child(transaction.getKey()).setValue(transaction).addOnCompleteListener(task -> {
            responceInterface.onResponse(task.isSuccessful());
            if (!task.isSuccessful()) {
                Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(task.getException().getMessage());
            }
        });
    }


    public static void getAllSavinngs(Context context, ResponceInterface responceInterface) {
        ArrayList<Transaction> list_bills = new ArrayList<>();
        ref_savings.child(CurrentUser.getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list_bills.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        list_bills.add(dataSnapshot.getValue(Transaction.class));
                    }
                }
                responceInterface.onResponse(list_bills);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                responceInterface.onError(error.getMessage());
            }
        });
    }


}
