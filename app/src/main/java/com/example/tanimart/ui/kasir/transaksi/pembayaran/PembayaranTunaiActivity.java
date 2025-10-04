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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
    // Tipe data ArrayList<Product> sekarang sudah benar karena import di atas sudah diperbaiki
    private ArrayList<Product> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pembayaran_tunai_activity);

        // Formatter Rupiah
        formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // Ambil total tagihan DAN cartList dari Intent.
        // Baris ini sekarang akan berfungsi dengan benar.
        totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        cartList = getIntent().getParcelableArrayListExtra("CART_LIST");

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

            // =================== INI BAGIAN YANG DIPERBAIKI ===================
            // Panggil prosesPembayaran dengan menyertakan cartList
            pembayaranTunaiViewModel.prosesPembayaran(
                    uangDiterima,
                    totalTagihan,
                    "Tunai",
                    cartList, // <-- Kirim daftar produk ke ViewModel
                    () -> { // <-- Ini adalah lambda untuk callback onComplete
                        // Kode ini hanya akan berjalan JIKA pengurangan stok di Firestore BERHASIL
                        Intent intent = new Intent(this, DialogTransaksiBerhasilActivity.class);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"));
                        String tanggalHariIni = sdf.format(new Date());

                        intent.putExtra("TANGGAL", tanggalHariIni);
                        intent.putExtra("TOTAL_TAGIHAN", totalTagihan);
                        intent.putExtra("UANG_DITERIMA", uangDiterima);
                        intent.putExtra("KEMBALIAN", uangDiterima - totalTagihan);
                        intent.putParcelableArrayListExtra("CART_LIST", cartList);

                        startActivity(intent);
                        finish();
                    });
        });

        // Tombol back
        btnBack.setOnClickListener(v -> finish());
    }
}
