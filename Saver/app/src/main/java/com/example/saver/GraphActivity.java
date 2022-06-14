package com.example.saver;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.example.saver.Helper.FirebaseCalls;
import com.example.saver.Helper.Helper;
import com.example.saver.Interfaces.ResponceInterface;
import com.example.saver.Models.Transaction;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    private ArrayList<BarEntry> list_entries;
    private ArrayList<Transaction> list_transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        list_transaction = new ArrayList<>();
        list_entries = new ArrayList<>();

        BarChart barChart = findViewById(R.id.bar_chat_id);

        //list_entries.add(new BarEntry(2012, ));

        FirebaseCalls.getAllTransactions(this, new ResponceInterface() {
            @Override
            public void onResponse(Object... params) {
                list_transaction = (ArrayList<Transaction>) params[0];

                for (int i = 0; i < list_transaction.size(); i++) {
                    float day = Helper.getDayFromDate(list_transaction.get(i).getDate());
                    float amount = Float.parseFloat(list_transaction.get(i).getAmount());
                    Log.d(TAG, "day   " + day);
                    Log.d(TAG, "amount   " + amount);
                    list_entries.add(new BarEntry(day, amount));
                }
                BarDataSet barDataSet = new BarDataSet(list_entries, "versions");
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);

                BarData barData = new BarData(barDataSet);
                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("test description");
                barChart.animateY(200);


            }

            @Override
            public void onError(String error) {
                Toast.makeText(GraphActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });


    }
}