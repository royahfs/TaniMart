package com.example.tanimart.ui.common.inventory;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager; // Jika ingin grid

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.adapter.DaftarProdukAdapter;

import java.util.ArrayList;

public class PilihProdukEditActivity extends AppCompatActivity {

    private PilihProdukViewModel viewModel;
    private DaftarProdukAdapter adapter;
    private RecyclerView recyclerPilihProduk;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_produk_edit);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPilihProduk);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        recyclerPilihProduk = findViewById(R.id.recyclerPilihProduk);
        recyclerPilihProduk.setLayoutManager(new GridLayoutManager(this, 2)); // Atau LinearLayoutManager

        // Setup Adapter dengan Aksi Klik yang Baru
        adapter = new DaftarProdukAdapter(new ArrayList<>(), product -> {
            // SAAT ITEM DIKLIK, BUKA EditProdukActivity
            Intent intent = new Intent(PilihProdukEditActivity.this, EditProdukActivity.class);

            // Kirim seluruh objek Product (yang sudah Parcelable)
            intent.putExtra(EditProdukActivity.EXTRA_PRODUCT, product);

            startActivity(intent);
        });
        recyclerPilihProduk.setAdapter(adapter);

        // Setup ViewModel untuk mengambil data
        viewModel = new ViewModelProvider(this).get(PilihProdukViewModel.class);
        viewModel.getProducts().observe(this, productList -> {
            if (productList != null) {
                adapter.updateData(productList);
            }
        });
    }
}
