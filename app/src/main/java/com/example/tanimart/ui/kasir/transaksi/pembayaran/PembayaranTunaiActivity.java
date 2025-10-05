package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.common.inventory.DaftarProdukViewModel;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.DialogTransaksiBerhasilActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PembayaranTunaiActivity extends AppCompatActivity {

    private EditText etUangDiterima;
    private TextView totalTagihanLabelTunai, tvKembalian;
    private Button btnUangPas, btnBayar;
    private double totalTagihan = 0.0;
    private NumberFormat formatter;

    private ArrayList<Product> cartList;
    private DaftarProdukViewModel daftarProdukViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_tunai);

        // --- SETUP AWAL ---
        formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        daftarProdukViewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);

        // --- AMBIL DATA DARI INTENT ---
        Intent intent = getIntent();
        totalTagihan = intent.getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        cartList = intent.getParcelableArrayListExtra("CART_LIST");
        if (cartList == null) {
            cartList = new ArrayList<>();
            Toast.makeText(this, "Error: Keranjang tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish(); // Keluar jika tidak ada data keranjang
            return;
        }

        // --- INISIALISASI UI ---
        etUangDiterima = findViewById(R.id.etUangDiterima);
        btnUangPas = findViewById(R.id.btnUangPas);
        totalTagihanLabelTunai = findViewById(R.id.totalTagihanLabelTunai);
        tvKembalian = findViewById(R.id.tvKembalian);
        btnBayar = findViewById(R.id.btnBayar);
        ImageView btnBack = findViewById(R.id.btnBack);

        totalTagihanLabelTunai.setText("Total: Rp" + formatter.format(totalTagihan));

        // --- SETUP LISTENERS ---
        btnUangPas.setOnClickListener(v -> etUangDiterima.setText(String.valueOf((int) totalTagihan)));

        etUangDiterima.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double uang = Double.parseDouble(s.toString());
                    double kembalian = uang - totalTagihan;
                    tvKembalian.setText(kembalian >= 0 ? "Kembalian: Rp" + formatter.format(kembalian) : "Belum cukup");
                } catch (NumberFormatException e) {
                    tvKembalian.setText("Belum cukup");
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> finish());

        // --- LOGIKA UTAMA TOMBOL BAYAR ---
        btnBayar.setOnClickListener(v -> {
            // 1. Validasi Input
            double uangDiterima;
            try {
                uangDiterima = Double.parseDouble(etUangDiterima.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Masukkan jumlah uang!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uangDiterima < totalTagihan) {
                Toast.makeText(this, "Uang yang diterima kurang!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2. Proses Checkout (Update Stok & Catat Produk Keluar)
            String idTransaksi = "TRX_" + System.currentTimeMillis();
            daftarProdukViewModel.checkoutProdukList(cartList, idTransaksi);


            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> dataTransaksi = new HashMap<>();
            dataTransaksi.put("idTransaksi", idTransaksi);
            dataTransaksi.put("total", totalTagihan);
            dataTransaksi.put("metode", "Tunai"); // Metode pembayaran untuk transaksi ini
            dataTransaksi.put("uangDiterima", uangDiterima);
            dataTransaksi.put("kembalian", uangDiterima - totalTagihan);
            // Gunakan Timestamp server untuk konsistensi waktu
            dataTransaksi.put("tanggal", com.google.firebase.firestore.FieldValue.serverTimestamp());

            // simpan ke koleksi 'transaksi'
            db.collection("transaksi").document(idTransaksi).set(dataTransaksi);


            // 3. Pindah ke Halaman Nota dengan Membawa Semua Data
            Intent successIntent = new Intent(this, DialogTransaksiBerhasilActivity.class);

            // Siapkan data tanggal untuk nota
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"));
            String tanggalHariIni = sdf.format(new Date());

            // Kirim semua data yang dibutuhkan oleh nota
            successIntent.putExtra("TANGGAL", tanggalHariIni);
            successIntent.putExtra("ID_TRANSAKSI", idTransaksi);
            successIntent.putExtra("TOTAL_TAGIHAN", totalTagihan);
            successIntent.putExtra("UANG_DITERIMA", uangDiterima);
            successIntent.putExtra("KEMBALIAN", uangDiterima - totalTagihan);
            successIntent.putParcelableArrayListExtra("CART_LIST", cartList);

            startActivity(successIntent);
            finish(); // Tutup halaman ini setelah transaksi berhasil
        });
    }

    // Fungsi checkoutProduk() sudah tidak diperlukan lagi karena logikanya sudah menyatu di dalam OnClickListener
}
