package com.example.tanimart.ui.common.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
//import android.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.tanimart.R;
import com.example.tanimart.ui.common.profile.EditProfileActivity;
import com.example.tanimart.ui.common.SplashActivity;

public class InventoryActivity extends AppCompatActivity {
//    private InventoryViewModel viewModel;

    Button buttonDaftarProduk;
    Button buttonKategoriProduk;
    Button buttonKelolaProdukMasuk;
    Button buttonProdukKeluar;
    ImageButton floatingTambah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainInventory), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left,systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_inventory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // tampilkan tombil back
            getSupportActionBar().setTitle("Inventory"); // set judul
        }


        // back button override pakai OnBackPressedCallback
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // init button
        buttonDaftarProduk = findViewById(R.id.btnDaftarProduk);
        buttonKategoriProduk = findViewById(R.id.btnKategoriProduk);
        buttonKelolaProdukMasuk = findViewById(R.id.btnKelolaProdukMasuk);
        buttonProdukKeluar = findViewById(R.id.btnProdukKeluar);
        floatingTambah = findViewById(R.id.floatingTambah);

        // listener floatingTambah → pindah ke KelolaProdukMasukActivity
        floatingTambah.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, KelolaProdukMasukActivity.class);
            startActivity(intent);
        });

        // set listener button
        buttonDaftarProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, DaftarProdukActivity.class);
                startActivity(intent);
            }
        });

        buttonKategoriProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, KategoriProdukActivity.class);
                startActivity(intent);
            }
        });

        buttonKelolaProdukMasuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, KelolaProdukMasukActivity.class);
                startActivity(intent);
            }
        });

        buttonProdukKeluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryActivity.this, ProdukKeluarActivity.class);
                startActivity(intent);
            }
        });

    }

    //Outside onCreate

    // handle toolbar yang di sebelah kanan
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_inventory, menu);
        return true;
    }

    // handle toolbar back (arrow) dan tolbar yang di sebelah kanan
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            Intent intent = new Intent(InventoryActivity.this, SplashActivity.class);

            // 2. Tambahkan Flag untuk membersihkan semua Activity sebelumnya
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // 3. Mulai Activity baru
            startActivity(intent);

            // Tampilkan pesan bahwa logout berhasil
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();

            return true; // Tandakan event sudah ditangani
        }
        if (id == R.id.settings) {
            Intent intent = new Intent(InventoryActivity.this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
