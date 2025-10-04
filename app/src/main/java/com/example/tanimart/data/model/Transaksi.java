package com.example.tanimart.data.model;

import com.google.firebase.Timestamp;

public class Transaksi {
    private String idTransaksi;
    private int kembalian;
    private String metode;
    private Timestamp tanggal;
    private double total;
    private double uangDiterima;

    public Transaksi() {} // penting untuk Firestore

    public String getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(String idTransaksi) { this.idTransaksi = idTransaksi; }

    public int getKembalian() { return kembalian; }
    public void setKembalian(int kembalian) { this.kembalian = kembalian; }

    public String getMetode() { return metode; }
    public void setMetode(String metode) { this.metode = metode; }

    public Timestamp getTanggal() { return tanggal; }
    public void setTanggal(Timestamp tanggal) { this.tanggal = tanggal; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public double getUangDiterima() { return uangDiterima; }
    public void setUangDiterima(double uangDiterima) { this.uangDiterima = uangDiterima; }
}
