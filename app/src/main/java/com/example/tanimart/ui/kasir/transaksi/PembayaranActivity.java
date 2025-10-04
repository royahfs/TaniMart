package com.example.tanimart.ui.kasir.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.appcompat.app.AppCompatActivity;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.PembayaranTunaiActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PembayaranActivity extends AppCompatActivity {

    private ArrayList<Product> productList;
    private double totalTagihan;
    ImageView btnBack;
    TextView tvTotalTagihan, tunaiPay, transferPay, qrisPay, debitPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        // --- Langkah 1: Ambil data dari Intent ---
        Intent intent = getIntent();
        totalTagihan = intent.getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        // Pastikan Anda menggunakan ParcelableArrayListExtra untuk mengambil ArrayList<Product>
        productList = intent.getParcelableArrayListExtra("CART_LIST");

        // Validasi: Pastikan keranjang tidak kosong sebelum melanjutkan
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(this, "Keranjang belanja kosong!", Toast.LENGTH_SHORT).show();
            // Kembali ke activity sebelumnya karena tidak ada yang bisa diproses
            finish();
            return; // Hentikan eksekusi lebih lanjut
        }

        // --- Langkah 2: Inisialisasi Komponen UI ---
        btnBack = findViewById(R.id.btnBack);
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        tunaiPay = findViewById(R.id.tunaiPay);
        transferPay = findViewById(R.id.transferPay);
        qrisPay = findViewById(R.id.qrisPay);
        debitPay = findViewById(R.id.debitPay);

        // --- Langkah 3: Tampilkan Total Tagihan ---
        // Format ke Rupiah dengan titik pemisah
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        String formattedTotal = formatter.format(totalTagihan);
        tvTotalTagihan.setText("Rp" + formattedTotal);

        // --- Langkah 4: Atur Listener untuk Tombol ---
        // Tombol back
        btnBack.setOnClickListener(v -> onBackPressed());

        // Tombol pembayaran tunai
        tunaiPay.setOnClickListener(v -> {
            // Panggil metode untuk memulai aktivitas pembayaran
            // dengan membawa semua data yang diperlukan
            bukaPembayaranTunai();
        });

        // (Opsional) Tambahkan listener untuk metode pembayaran lain jika diperlukan
        // Contoh:
        // transferPay.setOnClickListener(v -> { ... });
        // qrisPay.setOnClickListener(v -> { ... });
        // debitPay.setOnClickListener(v -> { ... });
    }

    /**
     * Metode ini bertanggung jawab untuk membuat Intent,
     * melampirkan data penting (total tagihan & daftar produk),
     * dan memulai PembayaranTunaiActivity.
     */
    private void bukaPembayaranTunai() {
        Intent intent = new Intent(PembayaranActivity.this, PembayaranTunaiActivity.class);

        // Lampirkan total tagihan
        intent.putExtra("TOTAL_TAGIHAN", totalTagihan);


        // Ini adalah kunci agar aktivitas berikutnya tahu produk mana yang stoknya harus dikurangi.
        intent.putParcelableArrayListExtra("CART_LIST", productList);

        startActivity(intent);
    }
}
