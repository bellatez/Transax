package com.venomtech.bellatez.gnytransax.Database.model;

public class DailyTransaction {
    public static final String TABLE_NAME = "daily_transactions";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_INCOME = "income";
    public static final String COLUMN_EXPENSE = "expense";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private int income;
    private int expense;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_INCOME + " INTEGER,"
                    + COLUMN_EXPENSE + " INTEGER,"
                    + COLUMN_TIMESTAMP + " TEXT"
                    + ")";

    public DailyTransaction(int id, int income, int expense, String timestamp) {
        this.id = id;
        this.income = income;
        this.expense = expense;
        this.timestamp = timestamp;
    }

    public DailyTransaction(){

    }


    public Integer getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getExpense() {
        return expense;
    }

    public void setExpense(int expense) {
        this.expense = expense;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
