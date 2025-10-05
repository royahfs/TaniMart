package com.example.tanimart.data.model;

import com.google.firebase.Timestamp;

public class ProdukKeluar {
    private String idProduk;
    private String namaProduk;
    private int jumlahKeluar;
    private Timestamp tanggalKeluar;
    private String idTransaksi;

    public ProdukKeluar() {}

    public ProdukKeluar(String idProduk, String namaProduk, int jumlahKeluar, Timestamp tanggalKeluar, String idTransaksi) {
        this.idProduk = idProduk;
        this.namaProduk = namaProduk;
        this.jumlahKeluar = jumlahKeluar;
        this.tanggalKeluar = tanggalKeluar;
        this.idTransaksi = idTransaksi;
    }

    public String getIdProduk() { return idProduk; }
    public void setIdProduk(String idProduk) { this.idProduk = idProduk; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public int getJumlahKeluar() { return jumlahKeluar; }
    public void setJumlahKeluar(int jumlahKeluar) { this.jumlahKeluar = jumlahKeluar; }

    public Timestamp getTanggalKeluar() { return tanggalKeluar; }
    public void setTanggalKeluar(Timestamp tanggalKeluar) { this.tanggalKeluar = tanggalKeluar; }

    public String getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(String idTransaksi) { this.idTransaksi = idTransaksi; }
}
