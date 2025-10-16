package com.example.tanimart.ui.common.inventory;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat; // <-- IMPORT BARU
import java.util.Locale;      // <-- IMPORT BARU

public class EditProdukActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";
    private Product productToEdit;

    // Deklarasi komponen UI
    private Toolbar toolbar;
    private TextInputEditText editNamaProduk, editHarga, editKategori, editDiskonPersen, editDiskonNominal;
    private TextView textStok;
    private Button btnUpdateProduk;
    private TextView textHargaSetelahDiskon;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        if (getIntent().hasExtra(EXTRA_PRODUCT)) {
            productToEdit = getIntent().getParcelableExtra(EXTRA_PRODUCT);
        }

        if (productToEdit == null) {
            Toast.makeText(this, "Produk tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();
        initViews();
        setupToolbar();
        populateData();
        setupListeners();

        calculateAndUpdateFinalPrice(); // <-- PANGGILAN AWAL
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarEditProduct);
        editNamaProduk = findViewById(R.id.editNamaProduk);
        editHarga = findViewById(R.id.editHarga);
        editKategori = findViewById(R.id.editKategori);
        textStok = findViewById(R.id.textStok);
        editDiskonPersen = findViewById(R.id.editDiskonPersen);
        editDiskonNominal = findViewById(R.id.editDiskonNominal);
        btnUpdateProduk = findViewById(R.id.btnUpdateProduk);
        textHargaSetelahDiskon = findViewById(R.id.textHargaSetelahDiskon); // <-- INISIALISASI BARU
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateData() {
        editNamaProduk.setText(productToEdit.getNamaProduk());
        editHarga.setText(String.valueOf(productToEdit.getHargaJual()));
        editKategori.setText(productToEdit.getKategori());
        textStok.setText("Stok saat ini: " + productToEdit.getStok());

        if (productToEdit.getDiskonPersen() > 0) {
            editDiskonPersen.setText(String.valueOf(productToEdit.getDiskonPersen()));
        }
        if (productToEdit.getDiskonNominal() > 0) {
            editDiskonNominal.setText(String.valueOf(productToEdit.getDiskonNominal()));
        }
    }

    private void calculateAndUpdateFinalPrice() {
        String hargaStr = editHarga.getText().toString();
        String diskonPersenStr = editDiskonPersen.getText().toString();
        String diskonNominalStr = editDiskonNominal.getText().toString();

        double hargaAwal = hargaStr.isEmpty() ? 0.0 : Double.parseDouble(hargaStr);
        double diskonPersen = diskonPersenStr.isEmpty() ? 0.0 : Double.parseDouble(diskonPersenStr);
        double diskonNominal = diskonNominalStr.isEmpty() ? 0.0 : Double.parseDouble(diskonNominalStr);

        double hargaAkhir = hargaAwal;

        // Terapkan diskon
        if (diskonPersen > 0) {
            hargaAkhir = hargaAwal - (hargaAwal * diskonPersen / 100);
        } else if (diskonNominal > 0) {
            hargaAkhir = hargaAwal - diskonNominal;
        }

        // Pastikan harga tidak menjadi minus
        if (hargaAkhir < 0) {
            hargaAkhir = 0;
        }

        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        textHargaSetelahDiskon.setText("Harga Akhir: " + formatRupiah.format(hargaAkhir));
    }

    private void setupListeners() {
        // Buat satu TextWatcher untuk digunakan bersama
        TextWatcher priceCalculatorWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateAndUpdateFinalPrice();
            }
        };

        editHarga.addTextChangedListener(priceCalculatorWatcher);
        editDiskonPersen.addTextChangedListener(priceCalculatorWatcher);
        editDiskonNominal.addTextChangedListener(priceCalculatorWatcher);

        // Logika agar hanya salah satu field diskon yang bisa diisi
        editDiskonPersen.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    editDiskonNominal.setEnabled(false);
                } else {
                    editDiskonNominal.setEnabled(true);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        editDiskonNominal.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    editDiskonPersen.setEnabled(false);
                } else {
                    editDiskonPersen.setEnabled(true);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnUpdateProduk.setOnClickListener(v -> updateProductData());
    }

    private void updateProductData() {
        String namaProduk = editNamaProduk.getText().toString().trim();
        String hargaStr = editHarga.getText().toString().trim();
        String kategori = editKategori.getText().toString().trim();
        String diskonPersenStr = editDiskonPersen.getText().toString().trim();
        String diskonNominalStr = editDiskonNominal.getText().toString().trim();

        if (namaProduk.isEmpty() || hargaStr.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Nama, harga, dan kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        productToEdit.setNamaProduk(namaProduk);
        productToEdit.setHargaJual(Double.parseDouble(hargaStr));
        productToEdit.setKategori(kategori);
        productToEdit.setDiskonPersen(diskonPersenStr.isEmpty() ? 0.0 : Double.parseDouble(diskonPersenStr));
        productToEdit.setDiskonNominal(diskonNominalStr.isEmpty() ? 0.0 : Double.parseDouble(diskonNominalStr));

        Toast.makeText(this, "Mengupdate produk...", Toast.LENGTH_SHORT).show();

        db.collection("inventory").document(productToEdit.getId())
                .set(productToEdit)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProdukActivity.this, "Produk berhasil diupdate", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProdukActivity.this, "Gagal mengupdate produk: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("EditProdukActivity", "Error updating document", e);
                });
    }
}
