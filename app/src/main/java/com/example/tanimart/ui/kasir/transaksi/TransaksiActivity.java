package com.example.tanimart.ui.kasir.transaksi;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tanimart.R;
// =================== IMPORT YANG DIPERBAIKI ===================
// Ganti import yang salah dengan import kelas Product dari model Anda
import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.data.model.Product; // Pastikan path ini sesuai
import com.example.tanimart.data.repository.UserRepository;
import com.example.tanimart.ui.adapter.ProductAdapter;
import com.example.tanimart.ui.adapter.TransaksiAdapter;
import com.example.tanimart.ui.common.SplashActivity;
import com.example.tanimart.ui.common.kalkulator.KalkulatorActivity;
import com.example.tanimart.ui.common.profile.ProfileActivity;
import com.example.tanimart.utils.CurrencyHelper;
import com.example.tanimart.utils.PrefsUtil;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TransaksiActivity extends AppCompatActivity {

    private TabLayout tabLayout;
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

        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

        // Ambil elemen dari nav_header
        ImageView imgProfileHeader = headerView.findViewById(R.id.imgProfileHeader);
        TextView tvNameHeader = headerView.findViewById(R.id.tvNameHeader);
        TextView tvEmailHeader = headerView.findViewById(R.id.tvEmailHeader);

        // Load user dari Firestore
        UserRepository userRepo = new UserRepository();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userRepo.getUser(uid, user -> {
            if (user != null) {
                tvNameHeader.setText(user.getName());
                tvEmailHeader.setText(user.getEmail());

                // Coba ambil foto dari penyimpanan lokal (PrefsUtil)
                String localPath = PrefsUtil.getPhotoPath(this, uid);
                if (localPath != null && new File(localPath).exists()) {
                    Glide.with(this)
                            .load(new File(localPath))
                            .centerCrop()
                            .placeholder(R.drawable.profile_1)
                            .into(imgProfileHeader);
                } else {
                    imgProfileHeader.setImageResource(R.drawable.profile_1);
                }
            }
        });

        btnMenu.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            } else {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId(); // Dapatkan ID dari item yang di-klik


            if (id == R.id.nav_home) {
                finish();
            } else if (id == R.id.nav_profil) {
                Intent intent = new Intent(TransaksiActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(TransaksiActivity.this, SplashActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent); // <-- Jangan lupa panggil startActivity

                Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
            }

            // Penting: Tutup drawer setelah item di-klik
            drawerLayout.closeDrawer(GravityCompat.START);
            return true; // Tandakan bahwa event klik sudah ditangani
        });

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);

        RecyclerView rvProduk = findViewById(R.id.rvProduk);
        rvProduk.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(new ArrayList<>(), produk -> {
            transaksiViewModel.tambahKeCart(produk);
        });
        rvProduk.setAdapter(productAdapter);


        tabLayout = findViewById(R.id.tabLayout);

        // Menambahkan listener untuk mendeteksi klik pada tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Dipanggil ketika sebuah tab dipilih
                if (tab.getPosition() == 1) { // Posisi 1 adalah tab "Manual" (dimulai dari 0)
                    // Buat Intent untuk membuka KalkulatorActivity
                    Intent intent = new Intent(TransaksiActivity.this, KalkulatorActivity.class);
                    startActivity(intent);

                    // finish();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Dipanggil ketika sebuah tab tidak lagi dipilih
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Dipanggil ketika sebuah tab yang sudah dipilih, dipilih kembali
                // Jika user menekan tab "Manual" lagi saat sudah di KalkulatorActivity
                // atau tab "Produk" lagi saat sudah di sini.
            }
        });


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


        // Misal di dalam onCreate TransaksiActivity.java
        btnBayar.setOnClickListener(v -> {
            Double total = transaksiViewModel.getTotalTagihan().getValue();
            List<CartItem> cartItemList = transaksiViewModel.getCartList().getValue();

            if (total != null && cartItemList != null && !cartItemList.isEmpty()) {

                ArrayList<Product> productListToSend = new ArrayList<>();
                for (CartItem cartItem : cartItemList) {
                    Product product = cartItem.getProduct();
                    if (product != null) {
                        product.setQuantity(cartItem.getQuantity()); // penting biar quantity ikut
                        productListToSend.add(product);
                    }
                }

                Intent intent = new Intent(TransaksiActivity.this, PembayaranActivity.class);
                intent.putExtra("TOTAL_TAGIHAN", total);
                intent.putParcelableArrayListExtra("CART_LIST", productListToSend);
                startActivity(intent);

                dialog.dismiss();
            }
        });

        // button simpan
        btnSimpan.setOnClickListener(v -> {
            Intent intent = new Intent(TransaksiActivity.this, CartActivity.class);
            startActivityForResult(intent, 100);
            dialog.dismiss();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String cartId = data.getStringExtra("SELECTED_CART_ID");
            // buka kembali bottom sheet sesuai cart ID
            showBottomSheet();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tabLayout != null) {
            tabLayout.getTabAt(0).select();
        }

        // Panggil ulang data user setiap kali activity kembali aktif
        NavigationView navView = findViewById(R.id.nav_view);
        View headerView = navView.getHeaderView(0);

        ImageView imgProfileHeader = headerView.findViewById(R.id.imgProfileHeader);
        TextView tvNameHeader = headerView.findViewById(R.id.tvNameHeader);
        TextView tvEmailHeader = headerView.findViewById(R.id.tvEmailHeader);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserRepository repo = new UserRepository();

        repo.getUser(uid, user -> {
            if (user != null) {
                tvNameHeader.setText(user.getName());
                tvEmailHeader.setText(user.getEmail());

                String localPath = PrefsUtil.getPhotoPath(this, uid);
                if (localPath != null && new File(localPath).exists()) {
                    Glide.with(this).load(new File(localPath)).into(imgProfileHeader);
                } else {
                    imgProfileHeader.setImageResource(R.drawable.profile_1);
                }
            }
        });
    }

}
