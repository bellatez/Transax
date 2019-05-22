package com.venomtech.bellatez.gnytransax.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.ShoppingList;
import com.venomtech.bellatez.gnytransax.R;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder> {
    private Context context;
    private List<ShoppingList> shoppingLists;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView quantity;
        public TextView item;

        public MyViewHolder(View view) {
            super(view);
            quantity = view.findViewById(R.id.qtyview);
            item = view.findViewById(R.id.itemview);
        }
    }

    public ShoppingListAdapter(Context context, List<ShoppingList> shoppingLists) {
        this.context = context;
        this.shoppingLists = shoppingLists;
    }

    @NonNull
    @Override
    public ShoppingListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        ShoppingList listItem = shoppingLists.get(position);
        holder.item.setText(listItem.getItem());
        holder.quantity.setText(Integer.toString(listItem.getQuantity()));

    }

    @Override
    public int getItemCount() {
        if (shoppingLists != null){
            return shoppingLists.size();
        }
        return 0;
    }
}
