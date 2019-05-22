package com.venomtech.bellatez.gnytransax;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.venomtech.bellatez.gnytransax.Adapter.DailyTransactionAdapter;
import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.DailyTransaction;
import com.venomtech.bellatez.gnytransax.utils.MyDividerItemDecoration;
import com.venomtech.bellatez.gnytransax.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;


public class TransactionFragment extends Fragment {

    private DailyTransactionAdapter transactionAdapter;
    private List<DailyTransaction> transactionList = new ArrayList<>();
    private DatabaseHelper db;


    EditText income;
    EditText expense;
    TextView income_data;
    TextView expense_data;
    TextView timestamp;
    TextView dialogheading;
    TextView msg_no_data;
    RecyclerView recyclerView;


    public TransactionFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        transactionAdapter = new DailyTransactionAdapter(getActivity(), transactionList);
        recyclerView.setAdapter(transactionAdapter);

        income_data=v.findViewById(R.id.income_data);
        expense_data = v.findViewById(R.id.expense_data);
        msg_no_data = v.findViewById(R.id.empty_data_view);
        timestamp = v.findViewById(R.id.timestamp);


        db = new DatabaseHelper(getActivity());
        transactionList.addAll(db.getAllTransactions());

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showDeleteDialog(position);
            }
        }));


        FloatingActionButton createBtn = v.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(null);
            }
        });

        toggleEmptyList();
    }

    private void showListDialog(final DailyTransaction transaction) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity().getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_transactions, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        income = view.findViewById(R.id.income);
        expense = view.findViewById(R.id.expense);
        dialogheading = view.findViewById(R.id.dialog_heading);
        dialogheading.setText(getString(R.string.new_list_title));

        alertDialogBuilderUserInput
            .setCancelable(false)
            .setPositiveButton("save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogBox, int id) {

                }
            })
            .setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(income.getText().toString()) || TextUtils.isEmpty(expense.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.validation, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    createItem(Integer.parseInt(income.getText().toString()), Integer.parseInt(expense.getText().toString()));
                    Toast.makeText(getActivity(), R.string.save, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void showDeleteDialog(final int position) {

        new AlertDialog.Builder(getActivity())
            .setTitle(R.string.deleteRequest)
            .setMessage(R.string.warningAlert)
            .setCancelable(true)
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(position);
                    Toast.makeText(getActivity(), R.string.truncated, Toast.LENGTH_LONG).show();
                }
            })
            .show();
    }

    private void createItem(int income, int expense) {
        long id = db.insertTransaction(income, expense);

        // get the newly inserted note from db
        DailyTransaction transaction = db.getTransaction(id);

        if (transaction != null) {
            // adding new note to array list at 0 position
            transactionList.add(0, transaction);

            transactionAdapter.notifyDataSetChanged();

            toggleEmptyList();
        }
    }

    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteTransaction(transactionList.get(position));

        // removing the note from the list
        transactionList.remove(position);
        transactionAdapter.notifyItemRemoved(position);

        toggleEmptyList();
    }

    private void toggleEmptyList() {

        if (db.getTransactionCount() > 0) {
            msg_no_data.setVisibility(View.GONE);
        } else {
            msg_no_data.setVisibility(View.VISIBLE);
        }
    }
}
