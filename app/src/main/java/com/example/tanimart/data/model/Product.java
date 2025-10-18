package com.example.tanimart.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Product implements Parcelable {
    // TIDAK DIUBAH, tetap 'id'
    private String id;
    private String namaProduk;
    private double hargaJual;
    private String deskripsi;
    private String kategori;
    private String merek;
    private String imageUrl;
    private int stok;
    private String satuan;
    private Date tanggal;

    private double diskonPersen;
    private double diskonNominal;

    private int quantity = 0;

    public Product() { } // Wajib untuk Firestore

    // Constructor lengkap
    public Product(String id, String namaProduk, double hargaJual, String deskripsi, String kategori,
                   String merek, String imageUrl, int stok, String satuan, Date tanggal) {
        this.id = id;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.merek = merek;
        this.imageUrl = imageUrl;
        this.stok = stok; // Diubah ke int
        this.satuan = satuan;
        this.tanggal = tanggal;
    }

    protected Product(Parcel in) {
        id = in.readString();
        namaProduk = in.readString();
        hargaJual = in.readDouble();
        deskripsi = in.readString();
        kategori = in.readString();
        merek = in.readString();
        imageUrl = in.readString();
        stok = in.readInt();
        satuan = in.readString();
        long tmpTanggal = in.readLong();
        this.tanggal = tmpTanggal == -1 ? null : new Date(tmpTanggal);
        quantity = in.readInt();
        diskonPersen = in.readDouble();
        diskonNominal = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(namaProduk);
        dest.writeDouble(hargaJual);
        dest.writeString(deskripsi);
        dest.writeString(kategori);
        dest.writeString(merek);
        dest.writeString(imageUrl);
        dest.writeInt(stok); // Diubah menjadi writeInt()
        dest.writeString(satuan);
        dest.writeLong(tanggal != null ? tanggal.getTime() : -1);
        dest.writeInt(quantity);
        dest.writeDouble(diskonPersen);
        dest.writeDouble(diskonNominal);
    }

    // CREATOR dan describeContents() tidak perlu diubah...
    @Override
    public int describeContents() { return 0; }
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) { return new Product(in); }
        @Override
        public Product[] newArray(int size) { return new Product[size]; }
    };


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // ... getter & setter lain yang sudah ada ...
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
    public String getSatuan() { return satuan; }
    public void setSatuan(String satuan) { this.satuan = satuan; }
    public Date getTanggal() { return tanggal; }
    public void setTanggal(Date tanggal) { this.tanggal = tanggal; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public int getStok() { return stok; }
    public void setStok(int stok) { this.stok = stok; }
    public double getDiskonPersen() { return diskonPersen; }
    public void setDiskonPersen(double diskonPersen) { this.diskonPersen = diskonPersen; }
    public double getDiskonNominal() { return diskonNominal; }
    public void setDiskonNominal(double diskonNominal) { this.diskonNominal = diskonNominal; }
}

