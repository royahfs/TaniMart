package com.example.tanimart.ui.karyawan.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.tanimart.R;
import com.example.tanimart.ui.common.inventory.DaftarProdukActivity;
import com.example.tanimart.ui.common.inventory.InventoryActivity;
import com.example.tanimart.ui.common.kalkulator.KalkulatorActivity;
import com.example.tanimart.ui.common.profile.ProfileActivity;

public class KaryawanDashboardActivity extends AppCompatActivity {
    CardView inventoryMenu;
    CardView infoProdukMenu;
    CardView kalkulatorMenu;
    CardView profilMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karyawan);
        EdgeToEdge.enable(this);

        inventoryMenu = findViewById(R.id.cardInventory);
        infoProdukMenu = findViewById(R.id.cardInfoProduk);
        kalkulatorMenu = findViewById(R.id.cardKalkulator);
        profilMenu = findViewById(R.id.cardLaporan);


        inventoryMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanDashboardActivity.this, InventoryActivity.class);
                startActivity(intent);
            }

        });

        infoProdukMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanDashboardActivity.this, DaftarProdukActivity.class);
                startActivity(intent);
            }

        });
        kalkulatorMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanDashboardActivity.this, KalkulatorActivity.class);
                startActivity(intent);
            }

        });
        profilMenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(KaryawanDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            }

        });
    }

}
