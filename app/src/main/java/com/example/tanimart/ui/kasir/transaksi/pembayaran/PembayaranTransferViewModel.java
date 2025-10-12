package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PembayaranTransferViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // 1. Ubah nama LiveData agar lebih aman (hanya bisa diubah dari dalam ViewModel)
    private final MutableLiveData<Boolean> _navigasiKeBerhasil = new MutableLiveData<>(false);
    public LiveData<Boolean> getNavigasiKeBerhasil() { return _navigasiKeBerhasil; }

    public void resetNavigasi() {
        _navigasiKeBerhasil.setValue(false);
    }

    // 2. Buat metode baru yang akan dipanggil dari Activity
    // Metode ini akan menangani logika simpan dan mengubah LiveData jika berhasil
    public void prosesDanSimpanTransaksi(
            String idTransaksi,
            double total,
            String metode,
            ArrayList<Product> cartList
    ) {
        simpanTransaksi(
                idTransaksi, total, metode, total, 0.0, cartList,
                () -> {
                    // JIKA BERHASIL, ubah nilai LiveData dari DALAM ViewModel
                    _navigasiKeBerhasil.postValue(true);
                },
                () -> {
                    // Jika gagal, log error. Anda bisa menambahkan LiveData untuk notifikasi error di sini.
                    Log.e("PembayaranTransferVM", "Proses simpan transaksi dari Activity gagal.");
                }
        );
    }

    // 3. Metode simpanTransaksi ini tidak perlu diubah, biarkan seperti apa adanya.
    // Metode ini sekarang menjadi private karena hanya digunakan oleh metode baru di atas.
    private void simpanTransaksi(
            String idTransaksi,
            double total,
            String metode,
            double uangDiterima,
            double kembalian,
            ArrayList<Product> cartList,
            Runnable onSuccess,
            Runnable onFailure) {

        DocumentReference transaksiRef = db.collection("transaksi").document(idTransaksi);

        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("idTransaksi", idTransaksi);
        transaksi.put("total", total);
        transaksi.put("metode", metode);
        transaksi.put("uangDiterima", uangDiterima);
        transaksi.put("kembalian", kembalian);
        transaksi.put("tanggal", Timestamp.now());

        transaksiRef.set(transaksi)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PembayaranTransferVM", "Transaksi berhasil disimpan: " + idTransaksi);

                    // Simpan produkKeluar
                    if (cartList != null && !cartList.isEmpty()) {
                        for (Product p : cartList) {
                            Map<String, Object> produkData = new HashMap<>();
                            try {
                                produkData.put("nama", p.getNamaProduk());
                                produkData.put("jumlah", p.getQuantity());
                                produkData.put("harga", p.getHargaJual());
                            } catch (Exception e) {
                                Log.e("PembayaranTransferVM", "Error saat mengambil data produk", e);
                                produkData.put("nama", "Produk Tidak Dikenal");
                                produkData.put("jumlah", 1);
                                produkData.put("harga", 0.0);
                            }

                            transaksiRef.collection("produkKeluar")
                                    .add(produkData)
                                    .addOnSuccessListener(doc -> Log.d("PembayaranTransferVM", "produkKeluar disimpan: " + produkData.get("nama")))
                                    .addOnFailureListener(e -> Log.e("PembayaranTransferVM", "Gagal simpan produkKeluar", e));
                        }
                    }

                    // Panggil callback onSuccess yang menandakan proses telah selesai
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("PembayaranTransferVM", "Gagal simpan transaksi", e);
                    if (onFailure != null) onFailure.run();
                });
    }
}
