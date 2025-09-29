package com.example.tanimart.ui.kasir.transaksi;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.tanimart.ui.adapter.ProductAdapter;

import java.util.ArrayList;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private RecyclerView rvProduk;
    private ProductAdapter productAdapter;
    private TransaksiViewModel transaksiViewModel;
    private EditText searchProduk;
    private ImageView btnMenu, btnCart, bottomSheetConnect;

    private List<Product> semuaProduk = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        // DrawerLayout
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_transaksi);
        btnMenu = findViewById(R.id.btnMenu);
        btnCart = findViewById(R.id.btnCart);
        bottomSheetConnect = findViewById(R.id.bottomSheetConnect);

        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // ViewModel
        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        // RecyclerView produk utama
        rvProduk = findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), produk -> {
            transaksiViewModel.tambahKeTagihan(produk);
        });
        rvProduk.setAdapter(productAdapter);

        // Search produk
        searchProduk = findViewById(R.id.etCariProduk);
        searchProduk.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProduk(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Observe produk dari ViewModel
        transaksiViewModel.getProdukList().observe(this, produkList -> {
            semuaProduk.clear();
            semuaProduk.addAll(produkList);
            productAdapter.setProductList(produkList);
        });

        // BottomSheet
        bottomSheetConnect.setOnClickListener(v -> showBottomSheet());
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerCart);
        TextView totalItem = dialog.findViewById(R.id.totalItem);
        TextView angkaTotal = dialog.findViewById(R.id.angkaTotal);
        Button btnSimpan = dialog.findViewById(R.id.btnSimpan);
        Button btnBayar = dialog.findViewById(R.id.btnBayar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
