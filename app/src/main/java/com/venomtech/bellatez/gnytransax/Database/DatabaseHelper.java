package com.venomtech.bellatez.gnytransax.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.venomtech.bellatez.gnytransax.Database.model.DailyTransaction;
import com.venomtech.bellatez.gnytransax.Database.model.Debt;
import com.venomtech.bellatez.gnytransax.Database.model.ShoppingList;
import com.venomtech.bellatez.gnytransax.utils.formatDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "gny_transax";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        create the tables
        db.execSQL(Debt.CREATE_TABLE);
        db.execSQL(ShoppingList.CREATE_TABLE);
        db.execSQL(DailyTransaction.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DailyTransaction.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Debt.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ShoppingList.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Daily Transactions CRUD

    //create transactions
    public long insertTransaction(int income, int expense) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DailyTransaction.COLUMN_INCOME, income);
        values.put(DailyTransaction.COLUMN_EXPENSE, expense);
        values.put(DailyTransaction.COLUMN_TIMESTAMP, getDateTime());

        long id = db.insert(DailyTransaction.TABLE_NAME, null, values);

        db.close();
        return id;
    }

    //get single transaction
    public DailyTransaction getTransaction(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DailyTransaction.TABLE_NAME + " WHERE " + DailyTransaction.COLUMN_ID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DailyTransaction trans = new DailyTransaction(
                cursor.getInt(cursor.getColumnIndex(DailyTransaction.COLUMN_ID)),
                cursor.getInt(cursor.getColumnIndex(DailyTransaction.COLUMN_INCOME)),
                cursor.getInt(cursor.getColumnIndex(DailyTransaction.COLUMN_EXPENSE)),
                cursor.getString(cursor.getColumnIndex(DailyTransaction.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return trans;
    }

    /**
     * getting all transactions
     */
    public List<DailyTransaction> getAllTransactions() {
        List<DailyTransaction> trans = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + DailyTransaction.TABLE_NAME + " ORDER BY " +
                DailyTransaction.COLUMN_TIMESTAMP + " DESC";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DailyTransaction td = new DailyTransaction();
                td.setId(c.getInt((c.getColumnIndex(DailyTransaction.COLUMN_ID))));
                td.setIncome((c.getInt(c.getColumnIndex(DailyTransaction.COLUMN_INCOME))));
                td.setExpense((c.getInt(c.getColumnIndex(DailyTransaction.COLUMN_EXPENSE))));
                td.setTimestamp(c.getString(c.getColumnIndex(DailyTransaction.COLUMN_TIMESTAMP)));

                trans.add(td);
            } while (c.moveToNext());
        }
        db.close();
        return trans;
    }


    //count the number of transactions
    public int getTransactionCount() {
        String countQuery = "SELECT  * FROM " + DailyTransaction.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //delete transactions
    public void deleteTransaction(DailyTransaction trans) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DailyTransaction.TABLE_NAME, DailyTransaction.COLUMN_ID + " = ?",
                new String[]{String.valueOf(trans.getId())});
        db.close();
    }

    // DEBTS CRUD

    //create debt
    public long insertDebt(String name, int amount, String contact, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Debt.COLUMN_NAME, name);
        values.put(Debt.COLUMN_AMOUNT, amount);
        values.put(Debt.COLUMN_CONTACT, contact);
        values.put(Debt.COLUMN_TYPE, 0);
        values.put(Debt.COLUMN_DUEDATE, date);

        long id = db.insert(Debt.TABLE_NAME, null, values);

        db.close();
        return id;
    }

    //get single debt
    public Debt getDebt(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_ID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        Debt trans = new Debt(
                cursor.getInt(cursor.getColumnIndex(Debt.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Debt.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Debt.COLUMN_CONTACT)),
                cursor.getInt(cursor.getColumnIndex(Debt.COLUMN_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(Debt.COLUMN_DUEDATE)),
                cursor.getString(cursor.getColumnIndex(Debt.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return trans;
    }

    /**
     * getting all debts
     */
    public List<Debt> getAllDebts() {
        List<Debt> trans = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 0 + " ORDER BY " +
                Debt.COLUMN_TIMESTAMP + " DESC";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Debt td = new Debt();
                td.setId(c.getInt(c.getColumnIndex(Debt.COLUMN_ID)));
                td.setName(c.getString(c.getColumnIndex(Debt.COLUMN_NAME)));
                td.setContact(c.getString(c.getColumnIndex(Debt.COLUMN_CONTACT)));
                td.setAmount((c.getInt(c.getColumnIndex(Debt.COLUMN_AMOUNT))));
                td.setDuedate(c.getString(c.getColumnIndex(Debt.COLUMN_DUEDATE)));
                td.setTimestamp(c.getString(c.getColumnIndex(Debt.COLUMN_TIMESTAMP)));

                trans.add(td);
            } while (c.moveToNext());
        }
        db.close();
        return trans;
    }

    public int updateDebt(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Debt.COLUMN_NAME, debt.getName());
        values.put(Debt.COLUMN_AMOUNT, debt.getAmount());
        values.put(Debt.COLUMN_CONTACT, debt.getContact());
        values.put(Debt.COLUMN_DUEDATE, debt.getDuedate());

        // updating row
        return db.update(Debt.TABLE_NAME, values, Debt.COLUMN_ID + " = ?",
                new String[]{String.valueOf(debt.getId())});
    }

    public int updateAmount(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Debt.COLUMN_AMOUNT, debt.getAmount());

        // updating row
        return db.update(Debt.TABLE_NAME, values, Debt.COLUMN_ID + " = ?",
                new String[]{String.valueOf(debt.getId())});
    }

    //count the number of debts
    public int getDebtCount() {
        String countQuery = "SELECT  * FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //delete debt
    public void deleteDebt(Debt debt) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Debt.TABLE_NAME, Debt.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(debt.getId())});
        db.close();
    }

//    LOAN CRUD

    //create debt
    public long insertLoan(String name, int amount, String contact, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Debt.COLUMN_NAME, name);
        values.put(Debt.COLUMN_AMOUNT, amount);
        values.put(Debt.COLUMN_CONTACT, contact);
        values.put(Debt.COLUMN_TYPE, 1);
        values.put(Debt.COLUMN_DUEDATE, date);

        long id = db.insert(Debt.TABLE_NAME, null, values);

        db.close();
        return id;
    }

    /**
     * getting all debts
     */
    public List<Debt> getAllLoans() {
        List<Debt> trans = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 1 + " ORDER BY " +
                Debt.COLUMN_TIMESTAMP + " DESC";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Debt td = new Debt();
                td.setId(c.getInt(c.getColumnIndex(Debt.COLUMN_ID)));
                td.setName(c.getString(c.getColumnIndex(Debt.COLUMN_NAME)));
                td.setContact(c.getString(c.getColumnIndex(Debt.COLUMN_CONTACT)));
                td.setAmount((c.getInt(c.getColumnIndex(Debt.COLUMN_AMOUNT))));
                td.setDuedate(c.getString(c.getColumnIndex(Debt.COLUMN_DUEDATE)));
                td.setTimestamp(c.getString(c.getColumnIndex(Debt.COLUMN_TIMESTAMP)));

                trans.add(td);
            } while (c.moveToNext());
        }
        db.close();
        return trans;
    }

    //count the number of loans
    public int getLoanCount() {
        String countQuery = "SELECT  * FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 1;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    // ShoppingList CRUD

    //create list item
    public long insertListItem(String item, String quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ShoppingList.COLUMN_ITEM, item);
        values.put(ShoppingList.COLUMN_QUANTITY, quantity);

        long id = db.insert(ShoppingList.TABLE_NAME, null, values);

        db.close();
        return id;
    }

    //get single list item
    public ShoppingList getListItem(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + ShoppingList.TABLE_NAME + " WHERE " + ShoppingList.COLUMN_ID + "=" + id;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        ShoppingList listItem = new ShoppingList(
                cursor.getInt(cursor.getColumnIndex(ShoppingList.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(ShoppingList.COLUMN_ITEM)),
                cursor.getInt(cursor.getColumnIndex(ShoppingList.COLUMN_QUANTITY)));

        // close the db connection
        cursor.close();

        return listItem;
    }

    /**
     * getting all list items
     */
    public List<ShoppingList> getAllListItems() {
        List<ShoppingList> listItems = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ShoppingList.TABLE_NAME;


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                ShoppingList td = new ShoppingList();
                td.setId(c.getInt((c.getColumnIndex(ShoppingList.COLUMN_ID))));
                td.setItem((c.getString(c.getColumnIndex(ShoppingList.COLUMN_ITEM))));
                td.setQuantity((c.getInt(c.getColumnIndex(ShoppingList.COLUMN_QUANTITY))));

                listItems.add(td);
            } while (c.moveToNext());
        }
        db.close();
        return listItems;
    }

    public int updateList(ShoppingList listItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ShoppingList.COLUMN_ITEM, listItem.getItem());
        values.put(ShoppingList.COLUMN_QUANTITY, listItem.getQuantity());

        // updating row
        return db.update(ShoppingList.TABLE_NAME, values, ShoppingList.COLUMN_ID + " = ?",
                new String[]{String.valueOf(listItem.getId())});
    }

    //delete list item
    public void deleteListItem(ShoppingList list) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ShoppingList.TABLE_NAME, Debt.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(list.getId())});
        db.close();
    }

    //count the number of list items
    public int getListCount() {
        String countQuery = "SELECT  * FROM " + ShoppingList.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public Cursor bsheetData(String from, String to) {

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + DailyTransaction.TABLE_NAME + " WHERE " + DailyTransaction.COLUMN_TIMESTAMP + " BETWEEN '" + from + " 00:00:00' AND '" + to + " 23:59:59'";
        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public int totalDebt() {

        SQLiteDatabase db = this.getReadableDatabase();
        int total_debt = 0;
        String selectQuery = "SELECT " + Debt.COLUMN_AMOUNT + " FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 0;

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                total_debt += c.getInt(0);
            } while (c.moveToNext());
        }

        return total_debt;
    }

    public int totalLoan() {

        SQLiteDatabase db = this.getReadableDatabase();
        int total_loan = 0;
        String selectQuery = "SELECT " + Debt.COLUMN_AMOUNT + " FROM " + Debt.TABLE_NAME + " WHERE " + Debt.COLUMN_TYPE + "=" + 1;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                total_loan += c.getInt(0);
            } while (c.moveToNext());
        }
        return total_loan;
    }


    // method to convert the transaction date from the datepicker to full datetime to store in transactions table
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


}
