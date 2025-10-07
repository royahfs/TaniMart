package com.example.tanimart.ui.admin.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.tanimart.R;
import com.example.tanimart.ui.admin.usermanagement.PegawaiActivity;
import com.example.tanimart.ui.common.inventory.DaftarProdukActivity;
import com.example.tanimart.ui.common.inventory.InventoryActivity;
import com.example.tanimart.ui.common.profile.ProfileActivity;
import com.example.tanimart.ui.common.profile.ProfileFragment;
import com.example.tanimart.ui.kasir.laporan.LaporanActivity;
import com.example.tanimart.ui.kasir.transaksi.TransaksiActivity;
;

public class AdminDashboardActivity extends AppCompatActivity {
    // private AdminDashboardViewModel viewModel;

    CardView inventoryMenu;
    CardView infoProdukMenu;
    CardView laporanMenu;
    CardView transaksiMenu;
    CardView pegawaiMenu;
    CardView profilMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_admin);

        inventoryMenu = findViewById(R.id.inventoryCard);
        infoProdukMenu = findViewById(R.id.infoProdukCard);
        laporanMenu = findViewById(R.id.laporanCard);
        transaksiMenu  = findViewById(R.id.transaksiCard);
        pegawaiMenu = findViewById(R.id.pegawaiCard);
        profilMenu = findViewById(R.id.profilCard);

        inventoryMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        infoProdukMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, DaftarProdukActivity.class);
                startActivity(intent);
            }
        });

        laporanMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, LaporanActivity.class);
                startActivity(intent);
            }
        });

        transaksiMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            // transaksi yang ada di kasir biar gk capek buatnya
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, TransaksiActivity.class);
                startActivity(intent);
            }
        });

        pegawaiMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, PegawaiActivity.class);
                startActivity(intent);
            }
        });

        profilMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

}
