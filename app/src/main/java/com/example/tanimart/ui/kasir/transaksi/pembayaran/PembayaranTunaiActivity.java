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
// Tambahkan import ini
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.kasir.transaksi.pembayaran.DialogTransaksiBerhasilActivity;


import java.text.NumberFormat;
import java.text.SimpleDateFormat;
// Tambahkan import ini
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PembayaranTunaiActivity extends AppCompatActivity {

    private EditText etUangDiterima;
    private TextView totalTagihanLabelTunai, tvKembalian;
    private Button btnUangPas, btnBayar;
    private PembayaranTunaiViewModel pembayaranTunaiViewModel;
    private double totalTagihan = 0.0;
    private NumberFormat formatter;
    // Tambahkan variabel ini untuk menyimpan daftar barang belanjaan
    private ArrayList<Product> cartList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pembayaran_tunai_activity);

        // Formatter Rupiah
        formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // Ambil data dari intent
        Intent intent = getIntent();
        totalTagihan = intent.getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        // ========================= PERUBAHAN 1: Terima daftar barang =========================
        // Terima ArrayList<Product> dari TransaksiActivity
        cartList = intent.getParcelableArrayListExtra("CART_LIST");

        // Pencegahan jika cartList tidak terkirim (null)
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        // ====================================================================================

        // Init UI
        etUangDiterima = findViewById(R.id.etUangDiterima);
        btnUangPas = findViewById(R.id.btnUangPas);
        totalTagihanLabelTunai = findViewById(R.id.totalTagihanLabelTunai);
        tvKembalian = findViewById(R.id.tvKembalian);
        btnBayar = findViewById(R.id.btnBayar);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Tampilkan total tagihan (format rupiah)
        String formattedTotal = formatter.format(totalTagihan);
        totalTagihanLabelTunai.setText("Total: Rp" + formattedTotal);

        // Init ViewModel
        pembayaranTunaiViewModel = new ViewModelProvider(this).get(PembayaranTunaiViewModel.class);

        // Observasi LiveData dari ViewModel
        pembayaranTunaiViewModel.getKembalian().observe(this, kembalian -> {
            if (kembalian >= 0) {
                tvKembalian.setText("Kembalian: Rp" + formatter.format(kembalian));
            } else {
                tvKembalian.setText("Belum cukup");
            }
        });

        // Listener input uang diterima
        etUangDiterima.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double uang = Double.parseDouble(s.toString());
                    pembayaranTunaiViewModel.hitungKembalian(uang, totalTagihan);
                } catch (NumberFormatException e) {
                    pembayaranTunaiViewModel.hitungKembalian(0, totalTagihan);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Tombol uang pas
        btnUangPas.setOnClickListener(v -> {
            etUangDiterima.setText(String.valueOf((int) totalTagihan));
        });

        // Tombol Bayar
        btnBayar.setOnClickListener(v -> {
            double uangDiterima;
            try {
                uangDiterima = Double.parseDouble(etUangDiterima.getText().toString());
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Masukkan jumlah uang!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uangDiterima < totalTagihan) {
                Toast.makeText(this, "Uang kurang!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Proses simpan transaksi
            pembayaranTunaiViewModel.prosesPembayaran(uangDiterima, totalTagihan, "Tunai", () -> {
                // Nama variabel diubah agar tidak bentrok dengan intent di atas
                Intent successIntent = new Intent(this, DialogTransaksiBerhasilActivity.class);
                // --- BAGIAN PENTING UNTUK TANGGAL ---
                // 1. Buat format tanggal yang diinginkan
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"));

                // 2. Dapatkan tanggal dan waktu saat ini, lalu format menjadi String
                String tanggalHariIni = sdf.format(new Date());

                // 3. Kirim data ke Nota
                successIntent.putExtra("TANGGAL", tanggalHariIni);
                successIntent.putExtra("TOTAL_TAGIHAN", totalTagihan);
                successIntent.putExtra("UANG_DITERIMA", uangDiterima);
                successIntent.putExtra("KEMBALIAN", uangDiterima - totalTagihan);

                // ================= PERUBAHAN 2: Teruskan daftar barang ke Nota =================
                // Kirim juga daftar barang belanjaan ke DialogTransaksiBerhasilActivity
                successIntent.putParcelableArrayListExtra("CART_LIST", cartList);
                // ==============================================================================

                startActivity(successIntent);
                finish();
            });
        });

        // Tombol back
        btnBack.setOnClickListener(v -> finish());
    }
}
