package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PembayaranQrisViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Flag yang di-observe Activity / Dialog untuk trigger navigasi ke halaman berhasil
    private final MutableLiveData<Boolean> navigasiKeBerhasil = new MutableLiveData<>(false);
    public LiveData<Boolean> getNavigasiKeBerhasil() { return navigasiKeBerhasil; }

    // Dipanggil oleh DialogTransaksiActivity saat kasir pilih "Sudah Terpotong" atau "Belum"
    public void prosesPembayaranQris(boolean sudahTerpotong) {
        if (sudahTerpotong) {
            navigasiKeBerhasil.setValue(true);
        } else {
            navigasiKeBerhasil.setValue(false);
        }
    }

    public void resetNavigasi() {
        navigasiKeBerhasil.setValue(false);
    }

    /**
     * Simpan transaksi QRIS ke Firestore (transaksi + produkKeluar).
     * Mengikuti pola PembayaranTunaiViewModel.
     */
    public void simpanTransaksi(String idTransaksi,
                                double total,
                                String metode,
                                double uangDiterima,
                                double kembalian,
                                List<Product> cartList,
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
                    Log.d("PembayaranQrisVM", "Transaksi berhasil disimpan: " + idTransaksi);

                    // Simpan produkKeluar
                    if (cartList != null) {
                        for (Product p : cartList) {
                            Map<String, Object> produkData = new HashMap<>();
                            // gunakan getter sesuai modelmu (sama seperti PembayaranTunaiViewModel)
                            try {
                                produkData.put("nama", p.getNamaProduk());
                            } catch (Exception e) {
                                produkData.put("nama", "Produk");
                            }
                            try {
                                produkData.put("jumlah", p.getQuantity());
                            } catch (Exception e) {
                                produkData.put("jumlah", 1);
                            }
                            try {
                                produkData.put("harga", p.getHargaJual());
                            } catch (Exception e) {
                                produkData.put("harga", 0.0);
                            }

                            transaksiRef.collection("produkKeluar")
                                    .add(produkData)
                                    .addOnSuccessListener(doc -> Log.d("PembayaranQrisVM", "produkKeluar disimpan: " + produkData.get("nama")))
                                    .addOnFailureListener(e -> Log.e("PembayaranQrisVM", "Gagal simpan produkKeluar", e));
                        }
                    }

                    // callback success jika disediakan
                    if (onSuccess != null) onSuccess.run();
                })
                .addOnFailureListener(e -> {
                    Log.e("PembayaranQrisVM", "Gagal simpan transaksi QRIS", e);
                    if (onFailure != null) onFailure.run();
                });
    }
}
