package com.example.tanimart.data.model;

public class PrintItem {
    private String name;
    private String qtyPrice; // contoh: "120.000 x 1"
    private String price;    // contoh: "120.000"

    public PrintItem(String name, String qtyPrice, String price) {
        this.name = name;
        this.qtyPrice = qtyPrice;
        this.price = price;
    }

    public String getName() { return name; }
    public String getQtyPrice() { return qtyPrice; }
    public String getPrice() { return price; }
}
