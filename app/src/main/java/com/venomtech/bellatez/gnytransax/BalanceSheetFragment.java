package com.venomtech.bellatez.gnytransax;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.DailyTransaction;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class BalanceSheetFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private static final int Date_from = 0;
    private static final int Date_to = 1;

    EditText from;
    EditText to;
    TextView start_date;
    TextView end_date;
    TextView days;
    TextView income;
    TextView expense;
    TextView profitview;
    TextView percent_profitview;
    TextView lossview;
    CardView bsheet;
    Button downloadBtn;

    private DatabaseHelper db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_balance_sheeet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        start_date = view.findViewById(R.id.start);
        end_date = view.findViewById(R.id.end);
        days = view.findViewById(R.id.days);
        income = view.findViewById(R.id.total_income);
        expense = view.findViewById(R.id.total_expense);
        profitview = view.findViewById(R.id.profit);
        percent_profitview = view.findViewById(R.id.percent_profit);
        lossview = view.findViewById(R.id.loss);
        bsheet = view.findViewById(R.id.bsheet);
        downloadBtn = view.findViewById(R.id.downloadBtn);

        db = new DatabaseHelper(getActivity());

        FloatingActionButton createBtn = view.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(null);
            }
        });
    }

    private void showDateDialog(final DailyTransaction transaction) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity().getApplicationContext());
        final View view = layoutInflaterAndroid.inflate(R.layout.dialog_bsheet, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        from = view.findViewById(R.id.from);
        to = view.findViewById(R.id.to);

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

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
                if (TextUtils.isEmpty(to.getText().toString()) || TextUtils.isEmpty(from.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.validation, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    fetchData(from.getText().toString(), to.getText().toString());
                    bsheet.setVisibility(view.VISIBLE);
                    downloadBtn.setVisibility(view.VISIBLE);
                    Toast.makeText(getActivity(), R.string.save, Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void fetchData(String start, String end){


//        return format.format(new Date(time));
        Cursor c = db.bsheetData(start, end);
        int total_income = 0;
        int total_expense = 0;
        int days_count = 0;

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                total_income += c.getInt(1);
                total_expense += c.getInt(2);
                days_count +=1;
            } while (c.moveToNext());
        }
        loss_gain(total_income, total_expense);
        days.setText(String.format("%,d", days_count));

    }

    private void loss_gain(int total_income, int total_expense){
        int profit = 0;
        int loss = 0;
        if (total_income > total_expense){
            profit = total_income - total_expense;
        }
        else if(total_income < total_expense){
            loss = total_expense - total_income;
        }
        else {
        }
        income.setText(String.format("%,d",total_income)+" frs");
        expense.setText(String.format("%,d", total_expense)+" frs");
        profitview.setText(String.format("%,d", profit)+" frs");
        lossview.setText(String.format("%,d", loss)+" frs");

    }

    public void showDatePickerDialog(View v){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        if(v.getId() == R.id.from){
            datePickerDialog.getDatePicker().setTag(Date_from);
        } else{
            datePickerDialog.getDatePicker().setTag(Date_to);
        }
        datePickerDialog.show();

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String date = dayOfMonth+"-"+(month+1)+"-"+year;

        int tag = ((Integer)view.getTag());
        String new_from = "";
        String new_to = "";

        if(tag == Date_from){
            from.setText(date);
            Log.d("date", "onDateSet:"+ date);
            start_date.setText(date);
        } else if(tag == Date_to){
            to.setText(date);
            new_to = to.getText().toString();
            end_date.setText(date);
        }
    }
}
