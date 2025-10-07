package com.example.tanimart.ui.common.inventory;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProdukActivity extends AppCompatActivity {

    private ImageView backBtn, faveBtn, pic;
    // Saya sesuaikan nama variabel agar konsisten dengan layout detail_produk.xml Anda
    private TextView namaBarangDetail, priceTxt, descriptionTxt, stokBarangDetail;
    private Button addBtn;
    private DaftarProdukViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        // Inisialisasi semua view
        backBtn = findViewById(R.id.backBtn);
        faveBtn = findViewById(R.id.faveBtn);
        pic = findViewById(R.id.pic);
        namaBarangDetail = findViewById(R.id.namaBarangDetail);
        priceTxt = findViewById(R.id.priceTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        stokBarangDetail = findViewById(R.id.stokBarangDetail);
        addBtn = findViewById(R.id.addBtn);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);

        backBtn.setOnClickListener(v -> finish());

        // Ambil ID dan load data
        String produkId = getIntent().getStringExtra("id");

        if (produkId != null && !produkId.isEmpty()) {
            viewModel.getProdukDetail(produkId).observe(this, product -> {
                if (product != null) {
                    // Tampilkan data teks ke UI
                    namaBarangDetail.setText(product.getNamaProduk());
                    priceTxt.setText(formatRupiah(product.getHargaJual()));
                    stokBarangDetail.setText(String.valueOf(product.getStok()));
                    descriptionTxt.setText(product.getDeskripsi() != null ? product.getDeskripsi() : "Tidak ada deskripsi");

                    // =================== BLOK GAMBAR YANG SUDAH DIPERBAIKI ===================
                    // Cerdas memuat gambar: bisa dari URL (http) atau dari string Base64
                    String imageUrl = product.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        // Cek apakah ini Base64 (tidak dimulai dengan http) atau URL biasa
                        if (imageUrl.startsWith("http")) {
                            // Jika ini URL, muat seperti biasa
                            Glide.with(this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.upload) // Placeholder yang lebih sesuai
                                    .error(R.drawable.upload)      // Error image yang lebih sesuai
                                    .into(pic);
                        } else {
                            // Jika ini BUKAN URL, kita anggap ini adalah Base64
                            try {
                                byte[] imageBytes = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                                Glide.with(this)
                                        .asBitmap()
                                        .load(imageBytes)
                                        .placeholder(R.drawable.upload)
                                        .error(R.drawable.upload)
                                        .into(pic);
                            } catch (IllegalArgumentException e) {
                                // Ini terjadi jika string bukan Base64 yang valid
                                android.util.Log.e("DetailProdukActivity", "Gagal decode Base64: " + e.getMessage());
                                pic.setImageResource(R.drawable.upload); // Tampilkan gambar error
                            }
                        }
                    } else {
                        // Handle jika tidak ada gambar sama sekali
                        pic.setImageResource(R.drawable.upload);
                    }


                    addBtn.setOnClickListener(v ->
                            Toast.makeText(this, product.getNamaProduk() + " ditambahkan ke keranjang!", Toast.LENGTH_SHORT).show()
                    );

                } else {
                    Toast.makeText(this, "Produk tidak ditemukan.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "Error: ID Produk tidak ada.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);
        return formatRupiah.format(number).replace("Rp", "Rp ");
    }
}
