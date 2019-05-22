package com.venomtech.bellatez.gnytransax.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venomtech.bellatez.gnytransax.Database.model.DailyTransaction;
import com.venomtech.bellatez.gnytransax.R;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.util.List;

public class DailyTransactionAdapter extends RecyclerView.Adapter<DailyTransactionAdapter.MyViewHolder> {

    private Context context;
    private List<DailyTransaction> transactionList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView income_data;
        public TextView expense_data;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            income_data = view.findViewById(R.id.income_data);
            expense_data = view.findViewById(R.id.expense_data);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public DailyTransactionAdapter(Context context, List<DailyTransaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        DailyTransaction trans = transactionList.get(i);

        String income = String.format("%,d", trans.getIncome());
        String expense = String.format("%,d", trans.getExpense());

        holder.income_data.setText(income+" xaf");
        holder.expense_data.setText(expense+" xaf");
        formatDate date = new formatDate();
        holder.timestamp.setText(date.formatDate(trans.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        if (transactionList != null){
            return transactionList.size();
        }
        return 0;
    }
}
