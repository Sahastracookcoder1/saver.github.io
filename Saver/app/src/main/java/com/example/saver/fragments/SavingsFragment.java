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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saver.Adapters.TransactionAdapter;
import com.example.saver.Helper.CurrentUser;
import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Interfaces.Clicklistener;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Transaction;
import com.example.saver.R;
import com.example.saver.connectors.savingsAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SavingsFragment extends Fragment implements Clicklistener {
    private TextView date, current_savings;
    private EditText amount, description;
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> list_transactions;

    public SavingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);
        current_savings = view.findViewById(R.id.current_saving);
        current_savings.setText("$" + CurrentUser.getCurrentUserSavings(getContext()));
        recyclerView = view.findViewById(R.id.list_savings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list_transactions = new ArrayList<>();
        fillList();
        return view;
    }

    private void fillList() {
        FirebaseCalls.getAllSavinngs(getContext(), new ResponceInterface() {
            @Override
            public void onResponse(Object... params) {
                list_transactions = (ArrayList<Transaction>) params[0];
                transactionAdapter = new TransactionAdapter(getContext(), list_transactions, SavingsFragment.this);
                recyclerView.setAdapter(transactionAdapter);
                //Log.d(TAG, list_transactions.size() + " size is");
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