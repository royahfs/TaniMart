package com.example.tanimart.ui.kasir.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.common.inventory.DaftarProdukViewModel;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.DialogTransaksiBerhasilActivity;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.PembayaranTunaiActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PembayaranActivity extends AppCompatActivity {

    private ArrayList<Product> productList;
    private double totalTagihan;

    private ImageView btnBack;
    private TextView tvTotalTagihan;
    private TextView tunaiPay, transferPay, qrisPay, debitPay;

    private DaftarProdukViewModel daftarProdukViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);

        // Ambil data dari Intent
        totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        productList = getIntent().getParcelableArrayListExtra("CART_LIST");

        // Validasi
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(this, "Keranjang belanja kosong!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inisialisasi UI
        btnBack = findViewById(R.id.btnBack);
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        tunaiPay = findViewById(R.id.tunaiPay);
        transferPay = findViewById(R.id.transferPay);
        qrisPay = findViewById(R.id.qrisPay);
        debitPay = findViewById(R.id.debitPay);

        // Tampilkan total tagihan
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        tvTotalTagihan.setText("Rp" + formatter.format(totalTagihan));

        // Setup ViewModel
        daftarProdukViewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);

        // Tombol back
        btnBack.setOnClickListener(v -> onBackPressed());

        // Semua tombol pembayaran memanggil checkoutProduk()
        tunaiPay.setOnClickListener(v -> {
            Intent tunaiIntent = new Intent(this, PembayaranTunaiActivity.class);
            // Kirim data yang dibutuhkan ke halaman berikutnya
            tunaiIntent.putExtra("TOTAL_TAGIHAN", totalTagihan);
            tunaiIntent.putParcelableArrayListExtra("CART_LIST", productList);
            startActivity(tunaiIntent);
            // Tutup halaman ini agar alurnya lebih rapi
            finish();
        });
        transferPay.setOnClickListener(v -> checkoutProduk("Transfer"));
        qrisPay.setOnClickListener(v -> checkoutProduk("QRIS"));
        debitPay.setOnClickListener(v -> checkoutProduk("Debit"));
    }

    /**
     * Fungsi checkout:
     * - Stok berkurang di Firestore
     * - Produk tercatat di "produk_keluar"
     */
    private void checkoutProduk(String metodeBayar) {
        String idTransaksi = "TRX_" + System.currentTimeMillis();
        daftarProdukViewModel.checkoutProdukList(productList, idTransaksi);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> dataTransaksi = new HashMap<>();
        dataTransaksi.put("idTransaksi", idTransaksi);
        dataTransaksi.put("total", totalTagihan);
        dataTransaksi.put("metode", metodeBayar); // Gunakan variabel dari parameter
        // Gunakan Timestamp server untuk konsistensi waktu
        dataTransaksi.put("tanggal", com.google.firebase.firestore.FieldValue.serverTimestamp());

        // Simpan dokumen ringkasan ke koleksi 'transaksi'
        db.collection("transaksi").document(idTransaksi).set(dataTransaksi);

        Intent intent = new Intent(this, DialogTransaksiBerhasilActivity.class);
        // ... (putExtra lainnya juga biarkan sama)
        intent.putExtra("METODE_BAYAR", metodeBayar);
        intent.putParcelableArrayListExtra("CART_LIST", productList);
        intent.putExtra("TOTAL_TAGIHAN", totalTagihan);
        intent.putExtra("ID_TRANSAKSI", idTransaksi);
        startActivity(intent);

        finish();
    }
}
