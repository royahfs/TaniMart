package com.example.tanimart.data.model;

public class Inventory {
    private String id;          // id dari Firestore
    private String namaProduk;
    private double hargaJual;
    private String merek;
    private String kategori;
    private double stok;
    private String satuan;
    private String tanggal;
    private String imageUrl;    // URL gambar dari Firebase Storage
    private String deskripsi;

    // konstruktor kosong (dibutuhkan Firebase)
    public Inventory() {}

    public Inventory(String id, String namaProduk, double hargaJual,
                     String merek, String kategori, double stok, String satuan, String tanggal, String imageUrl, String deskripsi) {
        this.id = id;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
        this.merek = merek;
        this.kategori = kategori;
        this.stok = stok;
        this.satuan = satuan;
        this.tanggal = tanggal;
        this.imageUrl = imageUrl;
        this.deskripsi = deskripsi;
    }

    // getter & setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public String getMerek() { return merek; }
    public void setMerek(String merek) { this.merek = merek; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public double getStok() {return stok;}

    public void setStok(double stok) {this.stok = stok;}

    public String getSatuan() {return satuan;}

    public void setSatuan(String satuan) {this.satuan = satuan;}

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}
