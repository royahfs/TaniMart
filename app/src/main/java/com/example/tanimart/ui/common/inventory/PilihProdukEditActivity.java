package com.example.tanimart.ui.common.inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager; // Menggunakan LinearLayoutManager

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.adapter.DaftarProdukAdapter;
import com.example.tanimart.ui.kasir.transaksi.TransaksiViewModel; // **PERUBAHAN 1**

import java.util.ArrayList;

public class PilihProdukEditActivity extends AppCompatActivity {

    private SearchView searchViewProduk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_produk_edit);

        // Setup Toolbar - Logika Anda sudah benar
        Toolbar toolbar = findViewById(R.id.toolbarPilihProduk);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Pilih Produk untuk Diedit");
        }
        toolbar.setNavigationOnClickListener(v -> finish()); // finish() sedikit lebih baik dari onBackPressed()

        // Setup RecyclerView
        RecyclerView recyclerPilihProduk = findViewById(R.id.recyclerPilihProduk);
        recyclerPilihProduk.setLayoutManager(new LinearLayoutManager(this));

        // Setup Adapter - Logika klik Anda dipertahankan, hanya ditambah argumen ke-3
        DaftarProdukAdapter adapter = new DaftarProdukAdapter(
                new ArrayList<>(),
                product -> {
                    // Logika Utama Anda: SAAT ITEM DIKLIK, BUKA EditProdukActivity
                    Intent intent = new Intent(PilihProdukEditActivity.this, EditProdukActivity.class);
                    // Kirim seluruh objek Product
                    intent.putExtra(EditProdukActivity.EXTRA_PRODUCT, product);
                    startActivity(intent);
                },
                DaftarProdukAdapter.TIPE_TRANSAKSI // **PERUBAHAN 2**
        );
        recyclerPilihProduk.setAdapter(adapter);

        // Setup ViewModel untuk mengambil data
        // Gunakan ViewModel terpusat yang sudah ada
        TransaksiViewModel viewModel = new ViewModelProvider(this).get(TransaksiViewModel.class); // **PERUBAHAN 1**
        viewModel.getProdukList().observe(this, productList -> { // observe dari getProdukList()
            if (productList != null) {
                adapter.setProductList(productList); // **PERUBAHAN 3**
            }
        });

        searchViewProduk = findViewById(R.id.searchViewProduk);
        searchViewProduk.clearFocus(); // Biar keyboard tidak langsung muncul
        searchViewProduk.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
