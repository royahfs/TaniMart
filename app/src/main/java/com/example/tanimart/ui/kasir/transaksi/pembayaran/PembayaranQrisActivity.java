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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PembayaranQrisActivity extends AppCompatActivity {

    private TextView tvTotalTagihan, btnKonfirmasiPembayaran;
    private ImageView btnBack, imgQrisCode;
    private double totalTagihan;
    private ArrayList<Product> cartList;

    private DaftarProdukViewModel daftarProdukViewModel;
    private PembayaranQrisViewModel pembayaranQrisViewModel;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran_qris);

        // --- Inisialisasi ViewModel ---
        daftarProdukViewModel = new ViewModelProvider(this).get(DaftarProdukViewModel.class);
        pembayaranQrisViewModel = new ViewModelProvider(this).get(PembayaranQrisViewModel.class);
        db = FirebaseFirestore.getInstance();

        // --- Ambil data dari intent ---
        totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        cartList = getIntent().getParcelableArrayListExtra("CART_LIST");
        if (cartList == null || cartList.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // --- Setup tampilan ---
        tvTotalTagihan = findViewById(R.id.tvTotalTagihan);
        btnBack = findViewById(R.id.btnBack);
        imgQrisCode = findViewById(R.id.imgQrisCode);
        btnKonfirmasiPembayaran = findViewById(R.id.btnKonfirmasiPembayaran);

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));
        tvTotalTagihan.setText("Rp" + formatter.format(totalTagihan));

        // --- Tombol kembali ---
        btnBack.setOnClickListener(v -> tampilkanDialogKonfirmasi());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                tampilkanDialogKonfirmasi();
            }
        });

        // --- Tombol konfirmasi ---
        btnKonfirmasiPembayaran.setOnClickListener(v -> tampilkanDialogKonfirmasi());

        // --- Observe hasil konfirmasi pembayaran dari ViewModel ---
        pembayaranQrisViewModel.getNavigasiKeBerhasil().observe(this, sudahTerpotong -> {
            if (Boolean.TRUE.equals(sudahTerpotong)) {
                prosesPembayaran(); // lanjut ke halaman berhasil
                pembayaranQrisViewModel.resetNavigasi();
            }
        });
    }

    private void tampilkanDialogKonfirmasi() {
        DialogTransaksiActivity dialog = new DialogTransaksiActivity();
        dialog.setOnKonfirmasiListener(this::prosesPembayaran); // otomatis panggil saat user pilih "Sudah Terpotong"
        dialog.show(getSupportFragmentManager(), "DialogTransaksi");
    }


    private void prosesPembayaran() {
        // QRIS = uang diterima sama dengan totalTagihan
        double uangDiterima = totalTagihan;
        double kembalian = 0.0;
        String idTransaksi = "TRX_" + System.currentTimeMillis();

        // Kurangi stok dan simpan produk keluar
        daftarProdukViewModel.checkoutProdukList(cartList, idTransaksi);

        // Simpan transaksi utama
        pembayaranQrisViewModel.simpanTransaksi(
                idTransaksi,
                totalTagihan,
                "QRIS",
                uangDiterima,
                kembalian,
                cartList, // pakai cartList, bukan productList
                () -> {
                    // onSuccess -> buka DialogTransaksiBerhasilActivity
                    Intent intent = new Intent(this, DialogTransaksiBerhasilActivity.class);

                    String tanggal = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", new Locale("id", "ID"))
                            .format(new Date());

                    intent.putExtra("TANGGAL", tanggal);
                    intent.putExtra("ID_TRANSAKSI", idTransaksi);
                    intent.putExtra("TOTAL_TAGIHAN", totalTagihan);
                    intent.putExtra("UANG_DITERIMA", uangDiterima);
                    intent.putExtra("KEMBALIAN", kembalian);
                    intent.putParcelableArrayListExtra("CART_LIST", cartList);

                    startActivity(intent);
                    finish();
                },
                () -> Toast.makeText(this, "Gagal menyimpan transaksi!", Toast.LENGTH_SHORT).show()
        );
    }
}
