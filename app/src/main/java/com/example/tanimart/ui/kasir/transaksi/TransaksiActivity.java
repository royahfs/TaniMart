package com.example.tanimart.ui.kasir.transaksi;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
// =================== IMPORT YANG DIPERBAIKI ===================
// Ganti import yang salah dengan import kelas Product dari model Anda
import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.data.model.Product; // Pastikan path ini sesuai
import com.example.tanimart.ui.adapter.ProductAdapter;
import com.example.tanimart.ui.adapter.TransaksiAdapter;
import com.example.tanimart.utils.CurrencyHelper;

import java.util.ArrayList;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;
    private TransaksiViewModel transaksiViewModel;
    private EditText searchProduk;
    private ImageView btnMenu, btnCart, bottomSheetConnect;
    private TextView tvTagih;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_transaksi);
        tvTagih = findViewById(R.id.tvTagih);
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

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        RecyclerView rvProduk = findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), produk -> {
            transaksiViewModel.tambahKeCart(produk);
        });
        rvProduk.setAdapter(productAdapter);

        searchProduk = findViewById(R.id.etCariProduk);
        searchProduk.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                transaksiViewModel.cariProduk(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        transaksiViewModel.getProdukList().observe(this, produkList -> {
            productAdapter.setProductList(produkList);
        });

        // Format total tagihan pakai CurrencyHelper
        transaksiViewModel.getTotalTagihan().observe(this, total -> {
            // Inisialisasi TextView di sini untuk menghindari NullPointerException jika dipanggil sebelum onResume
            TextView tvTagih = findViewById(R.id.tvTagih);
            tvTagih.setText("Tagih = " + CurrencyHelper.formatRupiah(total));
        });

        tvTagih.setOnClickListener(v -> showBottomSheet());
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

        // tombol cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // recycler view cart
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        TransaksiAdapter cartAdapter = new TransaksiAdapter(new ArrayList<>(), transaksiViewModel);
        recyclerView.setAdapter(cartAdapter);

        // total item
        transaksiViewModel.getCartList().observe(this, cart -> {
            cartAdapter.setCartList(cart);
            totalItem.setText("Total (" + cart.size() + ")");
        });

        // total harga pakai CurrencyHelper
        transaksiViewModel.getTotalTagihan().observe(this, total -> {
            angkaTotal.setText(CurrencyHelper.formatRupiah(total));
        });


        btnBayar.setOnClickListener(v -> {
            // 1. Ambil nilai LiveData SAAT INI.
            Double total = transaksiViewModel.getTotalTagihan().getValue();
            // INI ADALAH TIPE DATA YANG BENAR DARI VIEWMODEL ANDA
            List<CartItem> cartItemList = transaksiViewModel.getCartList().getValue();

            // 2. Lakukan pengecekan untuk memastikan data tidak null dan keranjang tidak kosong
            if (total != null && cartItemList != null && !cartItemList.isEmpty()) {

                // 3. KONVERSI DARI List<CartItem> ke ArrayList<Product>
                ArrayList<Product> productListToSend = new ArrayList<>();
                for (com.example.tanimart.data.model.CartItem item : cartItemList) {
                    Product product = item.getProduct(); // Asumsikan ada getter bernama getProduct()
                    product.setQuantity(item.getQuantity()); // PENTING: Salin kuantitasnya!
                    productListToSend.add(product);
                }

                Intent intent = new Intent(TransaksiActivity.this, com.example.tanimart.ui.kasir.transaksi.pembayaran.PembayaranTunaiActivity.class);

                // 4. Kirim total tagihan
                intent.putExtra("TOTAL_TAGIHAN", total);

                // 5. KIRIM DAFTAR PRODUK YANG SUDAH DIKONVERSI
                intent.putParcelableArrayListExtra("CART_LIST", productListToSend);

                startActivity(intent);
                dialog.dismiss(); // Tutup bottom sheet setelah berpindah halaman
            }
        });
        // =================== AKHIR BLOK TOMBOL BAYAR ===================

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
