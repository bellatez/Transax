package com.venomtech.bellatez.gnytransax.Database.model;

public class ShoppingList {

    public static final String TABLE_NAME = "list";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ITEM = "item";
    public static final String COLUMN_QUANTITY = "quantity";

    private int id;
    private String item;
    private int quantity;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ITEM + " TEXT,"
                    + COLUMN_QUANTITY + " INTEGER"
                    + ")";

    public ShoppingList() {

    }

    public ShoppingList(int id, String item, int quantity) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
