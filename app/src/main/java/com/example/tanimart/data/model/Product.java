package com.example.tanimart.data.model;

import android.os.Parcel;
import android.os.Parcelable;

//  Pastikan Anda sudah menambahkan 'implements Parcelable'
public class Product implements Parcelable {
    private String id;
    private String namaProduk;
    private double hargaJual;
    private String deskripsi;
    private String kategori;
    private String merek;
    private String imageUrl;
    private double stok;
    private String satuan;
    private String tanggal;

    // Variabel ini tidak ada di file Anda, tapi PENTING untuk keranjang belanja
    private int quantity = 0;


    public Product() { } // wajib untuk Firestore

    // Constructor lengkap Anda
    public Product(String id, String namaProduk, double hargaJual,String deskripsi, String kategori,
                   String merek, String imageUrl, double stok, String satuan, String tanggal) {
        this.id = id;
        this.namaProduk = namaProduk;
        this.hargaJual = hargaJual;
        this.deskripsi = deskripsi;
        this.kategori = kategori;
        this.merek = merek;
        this.imageUrl = imageUrl;
        this.stok = stok;
        this.satuan = satuan;
        this.tanggal = tanggal;
    }

    // 2. Tambahkan Constructor yang membaca dari Parcel
    protected Product(Parcel in) {
        id = in.readString();
        namaProduk = in.readString();
        hargaJual = in.readDouble();
        deskripsi = in.readString();
        kategori = in.readString();
        merek = in.readString();
        imageUrl = in.readString();
        stok = in.readDouble();
        satuan = in.readString();
        tanggal = in.readString();
        quantity = in.readInt(); // Jangan lupa 'quantity'
    }

    // 3. Tambahkan CREATOR
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // 4. Tambahkan metode writeToParcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(namaProduk);
        dest.writeDouble(hargaJual);
        dest.writeString(deskripsi);
        dest.writeString(kategori);
        dest.writeString(merek);
        dest.writeString(imageUrl);
        dest.writeDouble(stok);
        dest.writeString(satuan);
        dest.writeString(tanggal);
        dest.writeInt(quantity); // Jangan lupa 'quantity'
    }

    // =================== SELESAI KODE PARCELABLE ===================


    // =================== Getter & Setter ===================
    // Pastikan semua getter dan setter ada, terutama untuk 'quantity'

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

    // Getter & Setter untuk 'quantity'
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

}
