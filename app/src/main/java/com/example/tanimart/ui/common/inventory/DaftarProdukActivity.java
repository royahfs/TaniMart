package com.example.tanimart.ui.common.inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.adapter.DaftarProdukAdapter;

import java.util.ArrayList;
import java.util.List;

public class DaftarProdukActivity extends AppCompatActivity {

    private RecyclerView recyclerDaftarProduk;
    private DaftarProdukAdapter daftarProdukAdapter;
    private DaftarProdukViewModel daftarProdukViewModel;
    private List<Product> semuaProduk = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_produk);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_daftarProduk);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // Setup ViewModel
        daftarProdukViewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);


        //  Setup RecyclerView
        recyclerDaftarProduk = findViewById(R.id.recyclerDaftarProduk);
        // ini klo Grid
        recyclerDaftarProduk.setLayoutManager(new GridLayoutManager(this, 2));


        daftarProdukAdapter = new DaftarProdukAdapter(
                new ArrayList<>(),
                product -> { // <-- Gunakan lambda yang simpel karena hanya ada satu metode
                    // Aksi klik item -> buka DetailProdukActivity
                    Intent intent = new Intent(DaftarProdukActivity.this, DetailProdukActivity.class);
                    intent.putExtra("id", product.getId()); // kirim hanya ID produk
                    startActivity(intent);
                },
                DaftarProdukAdapter.TIPE_DAFTAR_PRODUK // Argumen ke-3
        );


        recyclerDaftarProduk.setAdapter(daftarProdukAdapter);

        // Observe
        daftarProdukViewModel.getProdukList().observe(this, productList -> {
            semuaProduk.clear();
            semuaProduk.addAll(productList);
            daftarProdukAdapter.setProductList(productList);
        });

        // Load data dari ViewModel
//        daftarProdukViewModel.loadDaftarProduk();
    }

}
