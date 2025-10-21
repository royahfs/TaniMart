package com.example.tanimart.ui.kasir.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.adapter.CartAdapter;
import com.example.tanimart.utils.CurrencyHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCartList;
    private CartAdapter adapter;
    private ArrayList<Product> keranjangDiterima = new ArrayList<>();
    private double totalDiterima = 0.0;
    private TextView textTotalHarga;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_checkout);

        // --- Inisialisasi View ---
        ImageView btnBack = findViewById(R.id.btnBack);
        recyclerCartList = findViewById(R.id.recyclerCartList);
        textTotalHarga = findViewById(R.id.textTotalHarga);
        btnCheckout = findViewById(R.id.btnCheckout);

        // ---  Ambil Data dari Intent ---
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EXTRA_KERANJANG")) {
            keranjangDiterima = intent.getParcelableArrayListExtra("EXTRA_KERANJANG");
            totalDiterima = intent.getDoubleExtra("EXTRA_TOTAL", 0.0);

            if (keranjangDiterima == null) {
                keranjangDiterima = new ArrayList<>();
                Log.d("CartActivity", "Data keranjang null, diinisialisasi ulang.");
            } else {
                Log.d("CartActivity", "Menerima " + keranjangDiterima.size() + " item, total: " + totalDiterima);
            }
        } else {
            Log.d("CartActivity", "Tidak ada data keranjang diterima dari Intent.");
            Toast.makeText(this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
        }
        // --- set RecyclerView ---
        adapter = new CartAdapter(keranjangDiterima);
        recyclerCartList.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartList.setAdapter(adapter);

        // --- Tampilkan Total Harga ---
        if (textTotalHarga != null) {
            textTotalHarga.setText(CurrencyHelper.formatRupiah(totalDiterima));
        }

        // --- Tombol Kembali ---
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("EXTRA_KERANJANG", keranjangDiterima);
                resultIntent.putExtra("EXTRA_TOTAL", totalDiterima);
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }


        // --- Tombol Checkout ---
        if (btnCheckout != null) {
            btnCheckout.setOnClickListener(v -> {
                if (!keranjangDiterima.isEmpty()) {
                    checkoutTransaksi();
                } else {
                    Toast.makeText(this, "Tidak ada produk dalam keranjang", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkoutTransaksi() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Data transaksi
        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("items", keranjangDiterima);
        transaksi.put("totalHarga", totalDiterima);
        transaksi.put("timestamp", System.currentTimeMillis());

        // Disable tombol selama proses
        btnCheckout.setEnabled(false);
        btnCheckout.setText("Memproses...");

        db.collection("transaksi")
                .add(transaksi)
                .addOnSuccessListener(documentReference -> {
                    String idTransaksiBaru = documentReference.getId();
                    Log.d("Firebase", "Transaksi berhasil disimpan. ID: " + idTransaksiBaru);
                    Toast.makeText(CartActivity.this, "Checkout berhasil! Lanjut ke pembayaran...", Toast.LENGTH_SHORT).show();
                    //Beri tahu TransaksiActivity bahwa checkout sukses agar keranjang dikosongkan
                    setResult(RESULT_OK);

                    Intent intentKePembayaran = new Intent(CartActivity.this, PembayaranActivity.class);

                    intentKePembayaran.putExtra("EXTRA_TRANSACTION_ID", idTransaksiBaru);
                    intentKePembayaran.putExtra("EXTRA_TRANSACTION_TOTAL", totalDiterima);
                    startActivity(intentKePembayaran);
                    finish();
                })

                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Gagal menyimpan transaksi", e);
                    Toast.makeText(CartActivity.this, "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    btnCheckout.setEnabled(true);
                    btnCheckout.setText("Checkout");
                    btnCheckout.setOnClickListener(v-> startActivity(new Intent(CartActivity.this, PembayaranActivity.class)));

                });
    }
}