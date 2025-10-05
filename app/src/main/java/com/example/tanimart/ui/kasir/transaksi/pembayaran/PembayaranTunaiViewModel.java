package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PembayaranTunaiViewModel extends ViewModel {

    private final MutableLiveData<Double> kembalian = new MutableLiveData<>(0.0);

    public LiveData<Double> getKembalian() {
        return kembalian;
    }

    public void hitungKembalian(double uangDiterima, double totalTagihan) {
        kembalian.setValue(uangDiterima - totalTagihan);
    }

    // Simpan transaksi + produk keluar ke Firestore
    public void prosesPembayaran(double uangDiterima,
                                 double total,
                                 String metode,
                                 List<Product> cartList,
                                 Runnable onSuccess) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Buat dokumen baru untuk transaksi
        DocumentReference transaksiRef = db.collection("transaksi").document();

        // 2. Data transaksi utama
        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("total", total);
        transaksi.put("uangDiterima", uangDiterima);
        transaksi.put("kembalian", uangDiterima - total);
        transaksi.put("metode", metode);
        transaksi.put("tanggal", Timestamp.now());

        // 3. Simpan transaksi
        transaksiRef.set(transaksi)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PembayaranTunaiVM", "Transaksi berhasil disimpan");

                    // 4. Simpan produk keluar di subcollection produkKeluar
                    if (cartList != null) {
                        for (Product p : cartList) {
                            Map<String, Object> produkData = new HashMap<>();
                            produkData.put("nama", p.getNamaProduk());
                            produkData.put("jumlah", p.getQuantity());
                            produkData.put("harga", p.getHargaJual());

                            transaksiRef.collection("produkKeluar").add(produkData)
                                    .addOnSuccessListener(doc -> Log.d("PembayaranTunaiVM", "Produk keluar disimpan: " + p.getNamaProduk()))
                                    .addOnFailureListener(e -> Log.e("PembayaranTunaiVM", "Gagal simpan produk keluar", e));
                        }
                    }

                    onSuccess.run();
                })
                .addOnFailureListener(e -> Log.e("PembayaranTunaiVM", "Gagal simpan transaksi", e));
    }
}
