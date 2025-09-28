package com.example.tanimart.ui.kasir.transaksi;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.databinding.ActivityTransaksiBinding;
import com.example.tanimart.ui.adapter.ProductAdapter;
import com.example.tanimart.ui.adapter.TagihanAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private RecyclerView rvProduk;
    private ProductAdapter productAdapter;
    private TransaksiViewModel transaksiViewModel;
    private EditText searchProduk;
    ActivityTransaksiBinding binding;

    private List<Product> semuaProduk = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);
        binding = ActivityTransaksiBinding.inflate(getLayoutInflater());

        // drawer layout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_transaksi);
        ImageView btnMenu = findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        // daftar produk recycler
        rvProduk = findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), produk -> {
            transaksiViewModel.tambahKeTagihan(produk);
        });
        rvProduk.setAdapter(productAdapter);


        // pencarian produk
        searchProduk = findViewById(R.id.etCariProduk);
        searchProduk.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProduk(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // observe produk
        transaksiViewModel.getProdukList().observe(this, produkList -> {
            semuaProduk.clear();
            semuaProduk.addAll(produkList);
            productAdapter.setProductList(produkList);
        });


        //bottom sheets
        binding.bottomSheetConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        RecyclerView recyclerView= dialog.findViewById(R.id.recyclerCart);
        TextView totalItem = dialog.findViewById(R.id.totalItem);
        TextView angkaTotal = dialog.findViewById(R.id.angkaTotal);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        Button btnBayar = dialog.findViewById(R.id.btnBayar);
    }

    private void filterProduk(String keyword) {
        List<Product> filtered = new ArrayList<>();
        for (Product produk : semuaProduk) {
            if (produk.getNamaProduk() != null &&
                    produk.getNamaProduk().toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(produk);
            }
        }
        productAdapter.setProductList(filtered);
    }
}
