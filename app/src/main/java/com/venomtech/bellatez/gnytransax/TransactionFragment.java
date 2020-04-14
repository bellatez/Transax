package com.venomtech.bellatez.gnytransax;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.venomtech.bellatez.gnytransax.Adapter.DailyTransactionAdapter;
import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.DailyTransaction;
import com.venomtech.bellatez.gnytransax.utils.MyDividerItemDecoration;
import com.venomtech.bellatez.gnytransax.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;


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
    Button msg_no_data;
    Button generateSheet;
    RecyclerView recyclerView;
    FloatingActionButton createBtn;
    private AdView mAdView;
    final String PREFS_NAME = "MyPrefsFile";
    SharedPreferences settings;


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
        settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        transactionAdapter = new DailyTransactionAdapter(getActivity(), transactionList);
        recyclerView.setAdapter(transactionAdapter);
        transactionList.clear();

        income_data=v.findViewById(R.id.income_data);
        expense_data = v.findViewById(R.id.expense_data);
        msg_no_data = v.findViewById(R.id.empty_data_view);
        timestamp = v.findViewById(R.id.timestamp);
        generateSheet = v.findViewById(R.id.generateSheet);

//        initialize ads from admob
        mAdView = v.findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


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

//        from to the generate sheet fragment
        generateSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BalanceSheetFragment nextFrag= new BalanceSheetFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });


        createBtn = v.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(null);
            }
        });

        msg_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(null);
            }
        });

//        if (db.getTransactionCount() == 0){
//            ShowIntro("WELCOME TO TRANSAX APP", "press the button to create new sales", 1, msg_no_data);
//        }
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

    @SuppressLint("RestrictedApi")
    private void toggleEmptyList() {

        if (db.getTransactionCount() > 0) {
            msg_no_data.setVisibility(View.GONE);
            generateSheet.setVisibility(View.VISIBLE);
            createBtn.setVisibility(View.VISIBLE);
//            if(db.getTransactionCount() == 1) {
//                ShowIntro("Register another sales", "press the button to create new sales", 1, createBtn);
//            }else if (db.getTransactionCount() == 2){
//                ShowIntro("Get balance sheet", "press the button to generate balance sheet", 3, generateSheet);
//            }
        } else {
            msg_no_data.setVisibility(View.VISIBLE);
            generateSheet.setVisibility(View.GONE);
            createBtn.setVisibility(View.GONE);
        }

    }

//    Add the showcaseView to teach the user how to use the application the first time a user opens the app

//    private void ShowIntro(String title, String text, final int type, final View v) {
//
//        //check if its the first time the application is being launched
//        if (settings.getBoolean("my_first_time", true)) {
//
//            //show the tutorials if its the first time the application is being launched
//            new GuideView.Builder(getActivity())
//                    .setTitle(title)
//                    .setContentText(text)
//                    .setTargetView(v)
//                    .setContentTextSize(14)//optional
//                    .setTitleTextSize(18)//optional
//                    .setDismissType(DismissType.outside)
//                    .setGuideListener(new GuideListener() {
//                        @Override
//                        public void onDismiss(View view) {
//                            if (type == 1) {
//                                showListDialog(null);
//                            }
//                            if (type == 3) {
//                                BalanceSheetFragment nextFrag= new BalanceSheetFragment();
//                                getActivity().getSupportFragmentManager().beginTransaction()
//                                        .replace(R.id.fragment_holder, nextFrag, "findThisFragment")
//                                        .addToBackStack(null)
//                                        .commit();
//
//                                // record the fact that the app has been started at least once
//                                settings.edit().putBoolean("my_first_time", false).commit();
//                            }
//                        }
//                    })
//                    .build()
//                    .show();
//
//
//        }
//    }
}
