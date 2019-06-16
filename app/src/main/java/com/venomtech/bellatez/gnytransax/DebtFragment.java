package com.venomtech.bellatez.gnytransax;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.venomtech.bellatez.gnytransax.Adapter.DebtAdapter;
import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.Debt;
import com.venomtech.bellatez.gnytransax.utils.MyDividerItemDecoration;
import com.venomtech.bellatez.gnytransax.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DebtFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private DebtAdapter debtAdapter;
    private List<Debt> debtList = new ArrayList<>();
    private DatabaseHelper db;

    private static final Integer REQUEST_CODE = 1;

    EditText debtor_name;
    EditText amnt;
    EditText contact_data;
    EditText duedate;
    Button msg_no_data;
    RecyclerView recyclerView;
    TextView dialogheading;
    TextView total_owed;
    FloatingActionButton createBtn;

    public DebtFragment() {
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
        return inflater.inflate(R.layout.fragment_debt, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);


        total_owed = v.findViewById(R.id.total_owed);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        debtAdapter = new DebtAdapter(getActivity(), debtList);
        recyclerView.setAdapter(debtAdapter);

//        we clear the list to avoid list duplication each time the back button is pressed.
//        the list cleared and reloaded
        debtList.clear();

        msg_no_data = v.findViewById(R.id.empty_data_view);
        db = new DatabaseHelper(getActivity());
        debtList.addAll(db.getAllDebts());


        if (Build.VERSION.SDK_INT >= 23 && !checkPermission()) {
            requestPermission();
        }


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                showActionsDialog(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        createBtn = v.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(false, null, -1);
            }
        });
        msg_no_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListDialog(false, null, -1);
            }
        });

        toggleEmptyList();
    }

    private void showListDialog(final boolean shouldUpdate, final Debt debt, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity().getApplicationContext());
        View v = layoutInflaterAndroid.inflate(R.layout.dialog_debts, null);

        debtor_name = v.findViewById(R.id.debtor_name);
        amnt = v.findViewById(R.id.amnt);
        contact_data = v.findViewById(R.id.contact_data);
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
                        TextUtils.isEmpty(amnt.getText().toString()) &&
                        TextUtils.isEmpty(contact_data.getText().toString()) &&
                        TextUtils.isEmpty(duedate.getText().toString())) {
                    Toast.makeText(getActivity(), R.string.validation, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }
                // check if user updating note
                if (shouldUpdate && debt != null) {
                    // update note by it's id
                    updateItem(debtor_name.getText().toString(),
                            Integer.parseInt(amnt.getText().toString()),
                            contact_data.getText().toString(),
                            duedate.getText().toString(), position);
                    Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                } else {
                    createItem(debtor_name.getText().toString(),
                            Integer.parseInt(amnt.getText().toString()),
                            contact_data.getText().toString(),
                            duedate.getText().toString());
                    Toast.makeText(getActivity(), R.string.save, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showActionsDialog(final int position) {

        CharSequence colors[] = new CharSequence[]{"Call", "Edit", "Paid", "Delete"};
        final String contact_number = debtList.get(position).getContact();
        final int amount = debtList.get(position).getAmount();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + contact_number));
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                            startActivity(callIntent);
                        } else {
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                startActivity(callIntent);
                            }
                        }
                    } catch (ActivityNotFoundException activityException) {
                        Toast.makeText(getActivity(), R.string.callFail, Toast.LENGTH_LONG).show();
                    }
                } else if (which == 1) {
                    showListDialog(true, debtList.get(position), position);
                } else if(which == 2){

                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.send_msg_request)
                            .setMessage(R.string.send_msg)
                            .setCancelable(true)
                            .setNegativeButton(android.R.string.cancel, null)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String sms = "Thank You for completing the payment of your debt of " + String.format("%,d", amount) + " XAF. Message sent from Transax mobile app";
                                    String phoneNum = contact_number;
                                    if (checkPermission()) {
                                        //Get the default SmsManager//
                                        SmsManager smsManager = SmsManager.getDefault();

                                        //Send the SMS//
                                        smsManager.sendTextMessage(phoneNum, null, sms, null, null);
                                    } else {
                                        Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
                                    }

                                    deleteItem(position);
                                    Toast.makeText(getActivity(), R.string.truncated, Toast.LENGTH_LONG).show();
                                }
                            })
                            .show();

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


    //CRUD OPERATIONS
    private void createItem(String name, int amount, String contact, String date) {
        long id = db.insertDebt(name, amount, contact, date);

        // get the newly inserted note from db
        Debt debt = db.getDebt(id);

        if (debt != null) {
            // adding new note to array list at 0 position
            debtList.add(0, debt);

            debtAdapter.notifyDataSetChanged();

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

        debtAdapter.notifyDataSetChanged();

        toggleEmptyList();
    }

    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteDebt(debtList.get(position));

        // removing the note from the list
        debtList.remove(position);
        debtAdapter.notifyItemRemoved(position);

        toggleEmptyList();
    }


    // permissions methods for phone call and text message
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity().getApplicationContext(), "permission granted", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE);
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);

    }


    //Date picker methods
    public void showDatePickerDialog() {
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

        String date = dayOfMonth + "-" + (month + 1) + "-" + year;

        duedate.setText(date);
    }

    @SuppressLint("RestrictedApi")
    private void toggleEmptyList() {

        if (db.getDebtCount() > 0) {
            msg_no_data.setVisibility(View.GONE);
            createBtn.setVisibility(View.VISIBLE);
            total_owed.setText(String.format("%,d", db.totalDebt()) + " XAF");
        } else {
            msg_no_data.setVisibility(View.VISIBLE);
            createBtn.setVisibility(View.GONE);
            total_owed.setText("0");
        }
    }

}
