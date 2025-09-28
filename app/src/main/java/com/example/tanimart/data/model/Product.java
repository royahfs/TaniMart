package com.example.tanimart.data.model;

public class Product {
    private String id;
    private String namaProduk;
    private double hargaJual;
    private String kategori;
    private String merek;
    private String imageUrl;
    private double stok;
    private String satuan;
    private String tanggal;

    public Product() { } // wajib untuk Firestore

    public Product(String id, String namaProduk, double hargaJual, String kategori,
                   String merek, String imageUrl, double stok, String satuan, String tanggal) {
        this.id = id;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
        this.kategori = kategori;
        this.merek = merek;
        this.imageUrl = imageUrl;
        this.stok = stok;
        this.satuan = satuan;
        this.tanggal = tanggal;
    }

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNamaProduk() { return namaProduk; }
    public void setNamaProduk(String namaProduk) { this.namaProduk = namaProduk; }

    public double getHargaJual() { return hargaJual; }
    public void setHargaJual(double hargaJual) { this.hargaJual = hargaJual; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getMerek() { return merek; }
    public void setMerek(String merek) { this.merek = merek; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getStok() { return stok; }
    public void setStok(double stok) { this.stok = stok; }

    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }
}
