package com.example.tanimart.data.model;

import java.util.List;

public class Transaksi {
    private String idTransaksi;
    private List<CartItem> items;
    private double totalHarga;
    private String tanggal;
    private String kasir;

    public Transaksi() { }

    public Transaksi(String idTransaksi, List<CartItem> items, double totalHarga, String tanggal, String kasir) {
        this.idTransaksi = idTransaksi;
        this.items = items;
        this.totalHarga = totalHarga;
        this.tanggal = tanggal;
        this.kasir = kasir;
    }

    public String getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(String idTransaksi) { this.idTransaksi = idTransaksi; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public double getTotalHarga() { return totalHarga; }
    public void setTotalHarga(double totalHarga) { this.totalHarga = totalHarga; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getKasir() { return kasir; }
    public void setKasir(String kasir) { this.kasir = kasir; }
}
