package com.venomtech.bellatez.gnytransax.Database.model;

public class Debt {

    public static final String TABLE_NAME = "debts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CONTACT = "contact";
    public static final String COLUMN_DUEDATE = "duedate";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String name;
    private String contact;
    private int amount;
    private int type;
    private String duedate;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_AMOUNT + " INTEGER,"
                    + COLUMN_CONTACT + " TEXT,"
                    + COLUMN_DUEDATE + " DATE,"
                    + COLUMN_TYPE + " int,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Debt(int id, String name, String contact, int amount, String duedate, String timestamp) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.amount = amount;
        this.duedate = duedate;
        this.timestamp = timestamp;
    }

    public Debt() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuedate() {
        return duedate;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
