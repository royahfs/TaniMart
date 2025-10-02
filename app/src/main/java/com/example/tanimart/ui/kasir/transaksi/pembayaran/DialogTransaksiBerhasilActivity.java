package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tanimart.R;
import com.example.tanimart.ui.kasir.transaksi.TransaksiActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class DialogTransaksiBerhasilActivity extends AppCompatActivity {

    private ImageView imgSuccess;
    private TextView tvTanggal, tvNominalTotal, tvNominalDiterima, tvNominalKembalian;
    private Button btnCetakStruk;
    private NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaksi_berhasil);

        imgSuccess = findViewById(R.id.imgSuccess);
        tvTanggal = findViewById(R.id.tvTanggal);
        tvNominalTotal = findViewById(R.id.tvNominalTotal);
        tvNominalDiterima = findViewById(R.id.tvNominalDiterima);
        tvNominalKembalian = findViewById(R.id.tvNominalKembalian);
        btnCetakStruk = findViewById(R.id.btnCetakStruk);

        // Formatter Rupiah
        formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // Ambil data dari intent
        String tanggal = getIntent().getStringExtra("TANGGAL");
        double totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        double uangDiterima = getIntent().getDoubleExtra("UANG_DITERIMA", 0.0);
        double kembalian = getIntent().getDoubleExtra("KEMBALIAN", 0.0);


        String formattedTotal = formatter.format(totalTagihan);
        String formattedDiterima = formatter.format(uangDiterima);
        String formattedKembalian = formatter.format(kembalian);
        tvNominalTotal.setText("Rp" + formattedTotal);
        tvNominalDiterima.setText("Rp" + formattedDiterima);
        tvNominalKembalian.setText("Rp" + formattedKembalian);


        //BARIS INI untuk menampilkan tanggal
        if (tanggal != null) {
            tvTanggal.setText(tanggal);
        }


        // Setup tombol untuk kembali ke halaman utama transaksi
        btnCetakStruk.setOnClickListener(v -> {
            kembaliKeTransaksi();
        });

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        kembaliKeTransaksi();
    }

    private void kembaliKeTransaksi() {
        // Buat Intent baru untuk TransaksiActivity
        Intent intent = new Intent(this, TransaksiActivity.class);

        // Flag ini akan membersihkan semua activity sebelumnya dari back stack
        // dan memulai TransaksiActivity sebagai tugas (task) baru.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        // Tutup activity dialog ini
        finish();
    }
}
