package com.example.tanimart.ui.common.inventory;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.ui.adapter.ProdukKeluarAdapter;


public class ProdukKeluarActivity extends AppCompatActivity {

    private ProdukKeluarViewModel viewModel;
    private ProdukKeluarAdapter adapter;
    private TextView txtTotalProdukKeluar;
    private ImageView btnSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_keluar);

        // Inisialisasi UI
        LinearLayout customToolbar = findViewById(R.id.toolbarProdukKeluar);
        ImageView btnBack = customToolbar.findViewById(R.id.btnBack);
        RecyclerView recycler = findViewById(R.id.recyclerProdukKeluar);
        txtTotalProdukKeluar = findViewById(R.id.txtTotalProdukKeluar);
        btnSort = findViewById(R.id.btnSort); // <-- 2. Inisialisasi tombol sort dari layout

        // Setup RecyclerView
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukKeluarAdapter();
        recycler.setAdapter(adapter);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(ProdukKeluarViewModel.class);
        viewModel.getProdukKeluarList().observe(this, produkKeluarList -> {
            adapter.updateData(produkKeluarList);
            int total = (produkKeluarList != null) ? produkKeluarList.size() : 0;
            txtTotalProdukKeluar.setText("Total Produk Keluar: " + total);
        });

        // Setup Listeners
        btnBack.setOnClickListener(v -> onBackPressed());

        // --- 3. TAMBAHKAN LISTENER UNTUK TOMBOL SORT ---
        btnSort.setOnClickListener(v -> showSortDialog());
    }

    /**
     * Metode untuk menampilkan dialog pilihan pengurutan.
     */
    private void showSortDialog() {
        // Daftar opsi yang akan ditampilkan
        final String[] sortOptions = {
                "Tanggal Terbaru (Default)",
                "Tanggal Terlama",
                "Nama Produk (A-Z)",
                "Nama Produk (Z-A)"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Urutkan Berdasarkan");
        builder.setItems(sortOptions, (dialog, which) -> {
            switch (which) {
                case 0: // Tanggal Terbaru
                    viewModel.sortData(ProdukKeluarViewModel.SortType.BY_DATE_DESC);
                    break;
                case 1: // Tanggal Terlama
                    viewModel.sortData(ProdukKeluarViewModel.SortType.BY_DATE_ASC);
                    break;
                case 2: // Nama Produk (A-Z)
                    viewModel.sortData(ProdukKeluarViewModel.SortType.BY_NAME_ASC);
                    break;
                case 3: // Nama Produk (Z-A)
                    viewModel.sortData(ProdukKeluarViewModel.SortType.BY_NAME_DESC);
                    break;
            }
        });

        // Tampilkan dialog ke layar
        builder.create().show();
    }
}
