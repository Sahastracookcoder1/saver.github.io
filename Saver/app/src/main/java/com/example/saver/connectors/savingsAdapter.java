package com.example.saver.connectors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.saver.Models.Transaction;
import com.example.saver.R;

import java.util.ArrayList;

public class savingsAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Transaction> arrayList;
    private TextView t1, t2, t3, t4;

    public savingsAdapter(Context context, ArrayList<Transaction> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.individual_bills, parent, false);
        t1 = convertView.findViewById(R.id.amount);
        t2 = convertView.findViewById(R.id.des);
        t3 = convertView.findViewById(R.id.date);
        t4 = convertView.findViewById(R.id.type);
        t1.setText(arrayList.get(position).getAmount());
        t2.setText(arrayList.get(position).getDescription());
        t3.setText(arrayList.get(position).getDate());
        t4.setText(arrayList.get(position).getType());
        return convertView;
    }
}