package com.venomtech.bellatez.gnytransax;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Paint;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.venomtech.bellatez.gnytransax.Adapter.ShoppingListAdapter;
import com.venomtech.bellatez.gnytransax.Database.DatabaseHelper;
import com.venomtech.bellatez.gnytransax.Database.model.ShoppingList;
import com.venomtech.bellatez.gnytransax.utils.RecyclerTouchListener;
import com.venomtech.bellatez.gnytransax.utils.MyDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ShoppingListAdapter listAdapter;
    private List<ShoppingList> shoppingLists = new ArrayList<>();
    private DatabaseHelper db;

    EditText item;
    EditText quantity;
    TextView dialogheading;
    Button msg_no_data;
    RecyclerView recyclerView;
    FloatingActionButton createBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL, 16));

        listAdapter = new ShoppingListAdapter(getActivity(), shoppingLists);
        recyclerView.setAdapter(listAdapter);

        db = new DatabaseHelper(getActivity());
        shoppingLists.addAll(db.getAllListItems());
        msg_no_data = v.findViewById(R.id.empty_data_view);

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */

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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(),recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));

//        if (dot.isChecked()){
//            itemview.setPaintFlags(itemview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            qtyview.setPaintFlags(qtyview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//        }

        toggleEmptyList();

    }

    private void showListDialog(final boolean shouldUpdate, final ShoppingList list, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity().getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.dialog_list, null);


        quantity = view.findViewById(R.id.quantity);
        item = view.findViewById(R.id.item);
        dialogheading = view.findViewById(R.id.dialog_heading);
        dialogheading.setText(!shouldUpdate ? getString(R.string.new_list_title) : getString(R.string.edit_list_title));

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(getActivity());
        alertDialogBuilderUserInput.setView(view);

        if (shouldUpdate && list != null) {
            quantity.setText(Integer.toString(list.getQuantity()));
            item.setText(list.getItem());
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
                if (TextUtils.isEmpty(item.getText().toString()) && TextUtils.isEmpty(quantity.getText().toString())) {
                    Toast.makeText(getActivity(), "Enter all fields!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && list != null) {
                    // update note by it's id
                    updateItem(item.getText().toString(), Integer.parseInt(quantity.getText().toString()), position);
                    Toast.makeText(getActivity(), R.string.updated, Toast.LENGTH_SHORT).show();
                } else {
                    // create new note
                    createItem(item.getText().toString(), quantity.getText().toString());
                    Toast.makeText(getActivity(), R.string.save, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showListDialog(true, shoppingLists.get(position), position);
                } else {
                    deleteItem(position);
                }
            }
        });
        builder.show();
    }

    private void createItem(String item, String qty) {
        long id = db.insertListItem(item, qty);

        // get the newly inserted note from db
        ShoppingList list = db.getListItem(id);
        Log.e("data", "onViewCreated: "+list);

        if (list != null) {
            // adding new note to array list at 0 position
            shoppingLists.add(0, list);

            listAdapter.notifyDataSetChanged();

            toggleEmptyList();
        }
    }

    private void updateItem(String item, int qty, int position) {

        ShoppingList list = shoppingLists.get(position);

        list.setItem(item);
        list.setQuantity(qty);

        db.updateList(list);

        shoppingLists.set(position, list);

        listAdapter.notifyDataSetChanged();

        toggleEmptyList();
    }

    private void deleteItem(int position) {
        // deleting the note from db
        db.deleteListItem(shoppingLists.get(position));

        // removing the note from the list
        shoppingLists.remove(position);
        listAdapter.notifyItemRemoved(position);

        toggleEmptyList();
    }

    @SuppressLint("RestrictedApi")
    private void toggleEmptyList() {

        if (db.getListCount() > 0) {
            msg_no_data.setVisibility(View.GONE);
            createBtn.setVisibility(View.VISIBLE);
        } else {
            msg_no_data.setVisibility(View.VISIBLE);
            createBtn.setVisibility(View.GONE);
        }
    }

}