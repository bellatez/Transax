package com.venomtech.bellatez.gnytransax.Database.model;

public class Savings {
    public static final String TABLE_NAME = "savings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private int amount;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_AMOUNT + " INTEGER,"
                    + COLUMN_TIMESTAMP + " TEXT"
                    + ")";

    public Savings(int id, int amount, String timestamp) {
        this.id = id;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Savings(){

    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
