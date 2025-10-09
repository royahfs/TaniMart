package com.example.tanimart.ui.common.inventory;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tanimart.R;

import com.example.tanimart.data.repository.UserRepository;
import com.example.tanimart.ui.common.SplashActivity;
import com.example.tanimart.ui.common.profile.EditProfileActivity;
import com.example.tanimart.utils.PrefsUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class InventoryActivity extends AppCompatActivity {
    Button buttonDaftarProduk, buttonKategoriProduk, buttonKelolaProdukMasuk, buttonProdukKeluar;
    ImageButton floatingTambah;
    // Hapus variabel optionsMenu, kita tidak membutuhkannya lagi
    // private Menu optionsMenu;
    private UserRepository userRepository;
    private static final String TAG = "InventoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        userRepository = new UserRepository();

        Toolbar toolbar = findViewById(R.id.toolbar_inventory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Inventory");
        }

        initViewsAndListeners();
    }

    private void initViewsAndListeners() {
        buttonDaftarProduk = findViewById(R.id.btnDaftarProduk);
        buttonKategoriProduk = findViewById(R.id.btnKategoriProduk);
        buttonKelolaProdukMasuk = findViewById(R.id.btnKelolaProdukMasuk);
        buttonProdukKeluar = findViewById(R.id.btnProdukKeluar);
        floatingTambah = findViewById(R.id.floatingTambah);

        floatingTambah.setOnClickListener(v -> startActivity(new Intent(InventoryActivity.this, KelolaProdukMasukActivity.class)));
        buttonDaftarProduk.setOnClickListener(v -> startActivity(new Intent(InventoryActivity.this, DaftarProdukActivity.class)));
        buttonKategoriProduk.setOnClickListener(v -> startActivity(new Intent(InventoryActivity.this, KategoriProdukActivity.class)));
        buttonKelolaProdukMasuk.setOnClickListener(v -> startActivity(new Intent(InventoryActivity.this, KelolaProdukMasukActivity.class)));
        buttonProdukKeluar.setOnClickListener(v -> startActivity(new Intent(InventoryActivity.this, ProdukKeluarActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_inventory, menu);
        return true;
    }

    // HAPUS METODE onResume() DARI SINI

    // ======================================================================================
    // == INI CARA PALING TEPAT: Gunakan onPrepareOptionsMenu
    // ======================================================================================
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu called. Preparing to update profile icon.");

        MenuItem profileMenuItem = menu.findItem(R.id.profile_menu_kelola_produk);

        if (profileMenuItem != null) {
            // Panggil fungsi untuk memuat gambar ke item yang benar
            fetchProfileImageAndSetIcon(profileMenuItem);
        } else {
            // Log ini seharusnya tidak akan muncul lagi
            Log.e(TAG, "MenuItem R.id.profile_menu_kelola_produk NOT FOUND in onPrepareOptionsMenu");
        }
        return super.onPrepareOptionsMenu(menu);
    }


    private void fetchProfileImageAndSetIcon(MenuItem menuItem) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String localPath = PrefsUtil.getPhotoPath(this, uid);

            if (localPath != null && new File(localPath).exists()) {
                Log.d(TAG, "Local image path found: " + localPath);
                // Langsung load gambar ke MenuItem yang diberikan
                Glide.with(this)
                        .load(new File(localPath))
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.profile_1)
                        .error(R.drawable.profile_1)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                menuItem.setIcon(resource);
                                Log.d(TAG, "Profile icon updated successfully in onPrepareOptionsMenu.");
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {}
                        });
            } else {
                Log.w(TAG, "Local image path not found. Using default icon.");
                menuItem.setIcon(R.drawable.profile_1); // Set ke default jika tidak ada
            }
        } else {
            Log.w(TAG, "Current user is null. Using default icon.");
            menuItem.setIcon(R.drawable.profile_1); // Set ke default jika user null
        }
    }

    // Fungsi updateMenuIconWithLocalFile dan fetchProfileImageFromLocal sudah tidak diperlukan lagi
    // karena logikanya sudah digabung ke fetchProfileImageAndSetIcon

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            startActivity(new Intent(InventoryActivity.this, EditProfileActivity.class));
            return true;
        }
        if (id == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(InventoryActivity.this, SplashActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
