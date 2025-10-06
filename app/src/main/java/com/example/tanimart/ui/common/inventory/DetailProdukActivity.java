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
// Pastikan import model Product sudah benar
import com.example.tanimart.data.model.Product;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailProdukActivity extends AppCompatActivity {

    private ImageView backBtn, faveBtn, pic;
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
                    // Tampilkan data ke UI
                    namaBarangDetail.setText(product.getNamaProduk());

                    // PERBAIKAN TIPE DATA HARGA
                    priceTxt.setText(formatRupiah(product.getHargaJual()));

                    stokBarangDetail.setText(String.valueOf(product.getStok()));
                    descriptionTxt.setText(product.getDeskripsi() != null ? product.getDeskripsi() : "Tidak ada deskripsi");


                    // Tampilkan gambar menggunakan Glide
                    if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                        Glide.with(this)
                                .load(product.getImageUrl())
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .into(pic);
                    } else {
                        pic.setImageResource(R.drawable.ic_launcher_background);
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

    // PERBAIKAN TIPE DATA DI SINI
    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);
        return formatRupiah.format(number).replace("Rp", "Rp ");
    }
}
