package com.venomtech.bellatez.gnytransax;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.venomtech.bellatez.gnytransax.Adapter.DebtAdapter;
import com.venomtech.bellatez.gnytransax.Adapter.LoanAdapter;
import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.Debt;
import com.venomtech.bellatez.gnytransax.utils.MyDividerItemDecoration;
import com.venomtech.bellatez.gnytransax.utils.RecyclerTouchListener;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class LoanFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private LoanAdapter LoanAdapter;
    private List<Debt> debtList = new ArrayList<>();
    private DatabaseHelper db;

    private static final Integer REQUEST_CODE = 1;


    EditText debtor_name;
    EditText amnt;
    EditText contact_data;
    EditText duedate;
    TextView msg_no_data;
    RecyclerView recyclerView;
    TextView dialogheading;

    public LoanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loan, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        LoanAdapter = new LoanAdapter(getActivity(), debtList);
        recyclerView.setAdapter(LoanAdapter);

        msg_no_data = v.findViewById(R.id.empty_data_view);
        db = new DatabaseHelper(getActivity());
        debtList.addAll(db.getAllLoans());



        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showActionsDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        FloatingActionButton createBtn = v.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(false, null, -1);
            }
        });

        toggleEmptyList();
    }

    private void showListDialog(final boolean shouldUpdate, final Debt debt, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity().getApplicationContext());
        View v = layoutInflaterAndroid.inflate(R.layout.dialog_loan, null);

        debtor_name = v.findViewById(R.id.debtor_name);
        amnt = v.findViewById(R.id.amnt);
        contact_data=v.findViewById(R.id.contact_data);
        duedate = v.findViewById(R.id.duedate);
        dialogheading = v.findViewById(R.id.dialog_title);
        dialogheading.setText(!shouldUpdate ? getString(R.string.new_list_title) : getString(R.string.edit_list_title));

        duedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(v);

        if (shouldUpdate && debt != null) {
            debtor_name.setText(debt.getName());
            amnt.setText(Integer.toString(debt.getAmount()));
            contact_data.setText(debt.getContact());
            duedate.setText(debt.getDuedate());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
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
                if (TextUtils.isEmpty(debtor_name.getText().toString()) &&
                    TextUtils.isEmpty(amnt.getText().toString())&&
                    TextUtils.isEmpty(contact_data.getText().toString())&&
                    TextUtils.isEmpty(duedate.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.validation, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                // check if user updating note
                if (shouldUpdate && debt != null) {
                    // update note by it's id
                    updateItem(debtor_name.getText().toString() ,
                            Integer.parseInt(amnt.getText().toString()),
                            contact_data.getText().toString(),
                            duedate.getText().toString(), position);
                    Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                } else {
                    createItem(debtor_name.getText().toString() ,
                            Integer.parseInt(amnt.getText().toString()),
                            contact_data.getText().toString(),
                            duedate.getText().toString());
                    Toast.makeText(getActivity(), R.string.save, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[]{"Call", "Edit", "Paid"};
        final String contact_number = debtList.get(position).getContact();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                 try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:"+ contact_number));
                     if (ActivityCompat.checkSelfPermission( getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                         startActivity(callIntent);
                     }
                     else {
                         ActivityCompat.requestPermissions(
                                 getActivity(),
                                 new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                     }
                    }
                    catch (ActivityNotFoundException activityException) {
                        Toast.makeText(getActivity(), R.string.callFail, Toast.LENGTH_LONG).show();
                    }
                } else if (which == 1) {
                    showListDialog(true, debtList.get(position), position);
                } else {
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
            }
        });
        builder.show();
    }

    private void createItem(String name, int amount, String contact, String date) {
        long id = db.insertLoan(name, amount, contact, date);

        // get the newly inserted note from db
        Debt debt = db.getDebt(id);

        if (debt != null) {
            // adding new note to array list at 0 position
            debtList.add(0, debt);

            LoanAdapter.notifyDataSetChanged();

            toggleEmptyList();
        }
    }

    private void updateItem(String name, int amount, String contact, String date, int position) {
        Debt debt = debtList.get(position);

        debt.setName(name);
        debt.setAmount(amount);
        debt.setContact(contact);
        debt.setDuedate(date);

        db.updateDebt(debt);

        debtList.set(position, debt);

        LoanAdapter.notifyDataSetChanged();

        toggleEmptyList();
    }

    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteDebt(debtList.get(position));

        // removing the note from the list
        debtList.remove(position);
        LoanAdapter.notifyItemRemoved(position);

        toggleEmptyList();
    }

    private void toggleEmptyList() {

        if (db.getDebtCount() > 0) {
            msg_no_data.setVisibility(View.GONE);
        } else {
            msg_no_data.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        String date = dayOfMonth + "/" + month + "/" + year;

        duedate.setText(date);
    }


}
