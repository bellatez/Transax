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
import com.venomtech.bellatez.gnytransax.Database.model.Savings;
import com.venomtech.bellatez.gnytransax.R;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.util.List;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.MyViewHolder> {

    private Context context;
    private List<Savings> savingsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView savings_amount;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            savings_amount = view.findViewById(R.id.savings_amount);
            timestamp = view.findViewById(R.id.timestamp);
        }
    }

    public SavingsAdapter(Context context, List<Savings> savingsList) {
        this.context = context;
        this.savingsList = savingsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.savings_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Savings trans = savingsList.get(i);

        String income = String.format("%,d", trans.getAmount());

        holder.savings_amount.setText(income+" xaf");
        formatDate date = new formatDate();
        holder.timestamp.setText(date.formatDate(trans.getTimestamp()));

    }

    @Override
    public int getItemCount() {
        if (savingsList != null){
            return savingsList.size();
        }
        return 0;
    }
}
