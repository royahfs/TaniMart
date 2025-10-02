package com.example.tanimart.ui.kasir.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tanimart.R;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.PembayaranTunaiActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class PembayaranActivity extends AppCompatActivity {

    ImageView btnBack;
    TextView tvTotalTagihanLabel, tvTotalTagihan, tunaiPay, transferPay, qrisPay, debitPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        // init UI
        btnBack = findViewById(R.id.btnBack);
        tvTotalTagihanLabel = findViewById(R.id.tvTotalTagihanLabel);
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        tunaiPay = findViewById(R.id.tunaiPay);
        transferPay = findViewById(R.id.transferPay);
        qrisPay = findViewById(R.id.qrisPay);
        debitPay = findViewById(R.id.debitPay);

        // ambil total tagihan dari Intent
        double total = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);

// Format ke Rupiah dengan titik pemisah
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        String formattedTotal = formatter.format(total);

        tvTotalTagihan.setText("Rp" + formattedTotal);


        // tombol back
        btnBack.setOnClickListener(v -> onBackPressed());

        // tombol pembayaran tunai
        tunaiPay.setOnClickListener(v -> {
            Intent intent = new Intent(PembayaranActivity.this, PembayaranTunaiActivity.class);
            intent.putExtra("TOTAL_TAGIHAN", total);
            startActivity(intent);
        });
    }
}
