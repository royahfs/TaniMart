package com.example.tanimart.data.model;

import java.util.List;

public class PrintData {
    private String storeName;
    private String cashier;
    private String date;
    private String invoice;
    private String payment;
    private List<PrintItem> items;
    private String subtotal;
    private String total;
    private String pay;
    private String change;

    public PrintData(String storeName, String cashier, String date, String invoice,
                     String payment, List<PrintItem> items,
                     String subtotal, String total, String pay, String change) {
        this.storeName = storeName;
        this.cashier = cashier;
        this.date = date;
        this.invoice = invoice;
        this.payment = payment;
        this.items = items;
        this.subtotal = subtotal;
        this.total = total;
        this.pay = pay;
        this.change = change;
    }

    public String getStoreName() { return storeName; }
    public String getCashier() { return cashier; }
    public String getDate() { return date; }
    public String getInvoice() { return invoice; }
    public String getPayment() { return payment; }
    public List<PrintItem> getItems() { return items; }
    public String getSubtotal() { return subtotal; }
    public String getTotal() { return total; }
    public String getPay() { return pay; }
    public String getChange() { return change; }
}
