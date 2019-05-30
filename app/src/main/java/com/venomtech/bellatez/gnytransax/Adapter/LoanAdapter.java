package com.venomtech.bellatez.gnytransax.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venomtech.bellatez.gnytransax.Database.model.Debt;
import com.venomtech.bellatez.gnytransax.R;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.util.List;

public class LoanAdapter extends RecyclerView.Adapter<LoanAdapter.MyViewHolder> {
    private Context context;
    private List<Debt> debtsList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timestamp;
        public TextView name;
        public TextView amount;
        public TextView contact;
        public TextView duedate;

        public MyViewHolder(View view) {
            super(view);
            timestamp = view.findViewById(R.id.timestamp);
            name = view.findViewById(R.id.name);
            amount = view.findViewById(R.id.amount);
            contact=view.findViewById(R.id.contact);
            duedate = view.findViewById(R.id.duedate);
        }
    }

    public LoanAdapter(Context context, List<Debt> debtsList) {
        this.context = context;
        this.debtsList = debtsList;
    }

    @NonNull
    @Override
    public LoanAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.debts_card, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanAdapter.MyViewHolder holder, int i) {
        Debt debt = debtsList.get(i);
        formatDate date = new formatDate();

        String amnt = String.format("%,d", debt.getAmount());
        String number = PhoneNumberUtils.formatNumber(debt.getContact());

        holder.timestamp.setText(debt.getTimestamp());
        holder.name.setText( debt.getName());
        holder.contact.setText(number);
        holder.amount.setText(amnt+" xaf");
        holder.duedate.setText(debt.getDuedate());

    }

    @Override
    public int getItemCount() {
        if( debtsList != null){
            return debtsList.size();
        }
        return 0;
    }
}
