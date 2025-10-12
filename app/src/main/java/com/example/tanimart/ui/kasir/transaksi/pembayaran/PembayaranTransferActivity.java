package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.common.inventory.DaftarProdukViewModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PembayaranTransferActivity extends AppCompatActivity {

    private TextView tvTotalTagihan, btnKonfirmasiPembayaran, tvNamaBank, tvNoRekening;
    private ImageView btnBack;
    private double totalTagihan;
    private ArrayList<Product> cartList;
    private String idTransaksi; // Pindahkan idTransaksi ke level class agar bisa diakses

    private DaftarProdukViewModel daftarProdukViewModel;
    private PembayaranTransferViewModel pembayaranTransferViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_transfer);

        // --- Inisialisasi ViewModel ---
        daftarProdukViewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);
        pembayaranTransferViewModel = new ViewModelProvider(this).get(PembayaranTransferViewModel.class);

        // --- Ambil data dari intent ---
        totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        cartList = getIntent().getParcelableArrayListExtra("CART_LIST");
        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Buat ID Transaksi unik di awal ---
        idTransaksi = "TRX_" + System.currentTimeMillis();

        // --- Setup tampilan ---
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        btnBack = findViewById(R.id.btnBack);
        tvNamaBank = findViewById(R.id.tvNamaBank);
        tvNoRekening = findViewById(R.id.tvNoRekening);
        btnKonfirmasiPembayaran = findViewById(R.id.btnKonfirmasiPembayaran);

        // --- Format total tagihan ---
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        tvTotalTagihan.setText("Rp" + formatter.format(totalTagihan));

        // --- Contoh data bank ---
        tvNamaBank.setText("Bank BCA");
        tvNoRekening.setText("1234567890 a.n. TaniMart");

        // --- Tombol kembali (Sudah Benar) ---
        btnBack.setOnClickListener(v -> tampilkanDialogKonfirmasi());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                tampilkanDialogKonfirmasi();
            }
        });

        // --- Tombol konfirmasi pembayaran (Sudah Benar) ---
        btnKonfirmasiPembayaran.setOnClickListener(v -> tampilkanDialogKonfirmasi());

        // --- Panggil metode untuk setup observer ---
        setupObserver();
    }

    private void tampilkanDialogKonfirmasi() {
        DialogTransaksiActivity dialog = new DialogTransaksiActivity();
        // Metode prosesPembayaran() akan dipanggil saat konfirmasi dari dialog diterima
        dialog.setOnKonfirmasiListener(this::prosesPembayaran);
        dialog.show(getSupportFragmentManager(), "DialogTransaksiTransfer");
    }

    // --- LANGKAH 1: PERBAIKI METODE PROSES PEMBAYARAN ---
    // Metode ini sekarang menjadi sangat sederhana.
    private void prosesPembayaran() {
        // Tampilkan loading/progress bar di sini jika ada, untuk memberi feedback ke user.

        // Kurangi stok produk (ini bisa tetap di sini atau dipindah ke dalam ViewModel jika logikanya kompleks)
        daftarProdukViewModel.checkoutProdukList(cartList, idTransaksi);

        // Cukup panggil satu metode dari ViewModel.
        // ViewModel akan menangani penyimpanan ke Firestore dan mengubah LiveData jika sukses.
        pembayaranTransferViewModel.prosesDanSimpanTransaksi(
                idTransaksi,
                totalTagihan,
                "TRANSFER",
                cartList
        );
    }

    // --- LANGKAH 2: BUAT METODE OBSERVER YANG TERPISAH ---
    // Metode ini bertugas mengamati perubahan dari ViewModel dan melakukan navigasi.
    private void setupObserver() {
        pembayaranTransferViewModel.getNavigasiKeBerhasil().observe(this, berhasil -> {
            // Cek apakah event ini bernilai true (artinya transaksi sukses)
            if (Boolean.TRUE.equals(berhasil)) {
                // Sembunyikan loading/progress bar jika ada.

                // Siapkan Intent untuk pindah ke halaman sukses
                Intent intent = new Intent(this, DialogTransaksiBerhasilActivity.class);

                // Siapkan data yang akan dikirim ke halaman sukses
                String tanggal = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"))
                        .format(new Date());

                intent.putExtra("TANGGAL", tanggal);
                intent.putExtra("ID_TRANSAKSI", idTransaksi);
                intent.putExtra("TOTAL_TAGIHAN", totalTagihan);
                intent.putExtra("UANG_DITERIMA", totalTagihan); // Untuk transfer, uang diterima = total tagihan
                intent.putExtra("KEMBALIAN", 0.0);
                intent.putParcelableArrayListExtra("CART_LIST", cartList);

                // Pindah halaman
                startActivity(intent);

                // Reset LiveData agar tidak terpicu lagi jika user kembali ke halaman ini secara tidak sengaja
                pembayaranTransferViewModel.resetNavigasi();

                // Tutup activity ini agar tidak bisa kembali lagi dengan tombol back
                finish();
            }
            // Anda bisa menambahkan blok 'else' di sini jika ingin menangani kasus kegagalan
            // yang dilaporkan oleh ViewModel.
        });
    }
}
