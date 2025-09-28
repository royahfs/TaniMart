package com.example.tanimart.ui.kasir.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.tanimart.R;
import com.example.tanimart.ui.common.inventory.DaftarProdukActivity;
import com.example.tanimart.ui.kasir.transaksi.TransaksiActivity;
import com.example.tanimart.ui.common.laporan.LaporanActivity;
import com.example.tanimart.ui.common.profil.ProfilActivity;

public class KasirDashboardActivity extends AppCompatActivity {
    CardView transaksiMenu;
    CardView infoProdukMenu;
    CardView laporanMenu;
    CardView profilMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kasir);

        transaksiMenu = findViewById(R.id.cardTransaksi);
        infoProdukMenu = findViewById(R.id.cardInfoProduk);
        laporanMenu = findViewById(R.id.cardLaporan);
        profilMenu = findViewById(R.id.cardProfil);

        transaksiMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KasirDashboardActivity.this, TransaksiActivity.class);
                startActivity(intent);
            }
        });

        infoProdukMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KasirDashboardActivity.this, DaftarProdukActivity.class);
                startActivity(intent);
            }
        });

        laporanMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KasirDashboardActivity.this, LaporanActivity.class);
                startActivity(intent);
            }
        });

        profilMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KasirDashboardActivity.this, ProfilActivity.class);
                startActivity(intent);
            }
        });
    }
}
