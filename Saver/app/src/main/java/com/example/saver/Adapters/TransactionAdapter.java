package com.example.saver.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saver.Interfaces.Clicklistener;
import com.example.saver.Models.Transaction;
import com.example.saver.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private Context context;
    private ArrayList<Transaction> list_transaction;
    private Clicklistener clicklistener;

    public TransactionAdapter(Context context, ArrayList<Transaction> list_transaction, Clicklistener clicklistener) {
        this.context = context;
        this.list_transaction = list_transaction;
        this.clicklistener = clicklistener;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.row_bill_design, parent, false), clicklistener);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        holder.textView_date.setText(list_transaction.get(position).getDate());
        holder.textView_description.setText(list_transaction.get(position).getDescription());
        holder.textView_amount.setText("$" + list_transaction.get(position).getAmount());
        holder.textView_type.setText(list_transaction.get(position).getType());

        holder.imageView_del.setImageResource(R.drawable.ic_baseline_screen_share_24);
        if (list_transaction.get(position).isAddition()) {
            holder.imageView_del.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else {
            holder.imageView_del.setBackgroundColor(context.getResources().getColor(R.color.red));
        }

        holder.imageView_del.setOnClickListener(view -> {
            Intent intent2 = new Intent();
            intent2.setType("text/plain");
            intent2.putExtra(Intent.EXTRA_TEXT, "Transaction of " + list_transaction.get(position).getAmount() + " is made on " + list_transaction.get(position).getDate() + "\n for the purpose of " + list_transaction.get(position).getDescription());
            context.startActivity(Intent.createChooser(intent2, "Share via"));
        });

        holder.textView_monthly.setText("");
    }

    @Override
    public int getItemCount() {
        return list_transaction.size();
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView_amount, textView_date, textView_type, textView_monthly, textView_description;
        ImageView imageView_del;
        Clicklistener clicklistener;

        public TransactionViewHolder(@NonNull View itemView, Clicklistener clicklistener) {
            super(itemView);
            textView_amount = itemView.findViewById(R.id.amount_bill_design);
            textView_date = itemView.findViewById(R.id.date_bill_design);
            textView_type = itemView.findViewById(R.id.type_bill_design);
            textView_monthly = itemView.findViewById(R.id.monthly_bill_design);
            textView_description = itemView.findViewById(R.id.des_bill_design);
            imageView_del = itemView.findViewById(R.id.del_bill);

            itemView.setOnClickListener(this);
            this.clicklistener = clicklistener;
        }

        @Override
        public void onClick(View view) {
            clicklistener.click(getAdapterPosition());
        }
    }
}
