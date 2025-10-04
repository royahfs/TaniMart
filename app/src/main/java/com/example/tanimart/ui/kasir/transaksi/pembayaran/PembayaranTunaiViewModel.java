package com.example.tanimart.ui.kasir.transaksi.pembayaran; // Sesuaikan dengan path Anda

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// =================== IMPORT YANG DIBUTUHKAN ===================
import com.example.tanimart.data.model.Product; // Pastikan path ini benar
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class PembayaranTunaiViewModel extends ViewModel {

    private static final String TAG = "PembayaranViewModel";

    private final MutableLiveData<Double> kembalian = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Inisialisasi Firestore

    public LiveData<Double> getKembalian() {
        return kembalian;
    }

    public void hitungKembalian(double uang, double total) {
        kembalian.setValue(uang - total);
    }

    // Callback interface untuk menandakan proses selesai
    public interface PembayaranCallback {
        void onComplete();
    }

    /**
     * Metode utama untuk memproses pembayaran.
     * Metode ini sekarang akan menangani logika pengurangan stok di Firestore.
     *
     * @param uangDiterima      Jumlah uang dari pelanggan.
     * @param totalTagihan      Total belanja.
     * @param metodePembayaran  Metode pembayaran (misal: "Tunai").
     * @param cartList          DAFTAR PRODUK YANG DIBELI. Ini sangat penting.
     * @param callback          Callback untuk dieksekusi setelah proses Firebase selesai.
     */
    public void prosesPembayaran(
            double uangDiterima,
            double totalTagihan,
            String metodePembayaran,
            ArrayList<Product> cartList, // Tambahkan parameter ini
            PembayaranCallback callback
    ) {
        // Validasi: Pastikan ada produk yang diproses
        if (cartList == null || cartList.isEmpty()) {
            Log.e(TAG, "Gagal proses pembayaran: Keranjang belanja kosong.");
            // Mungkin tampilkan error ke user jika perlu
            return;
        }

        // =================== LOGIKA PENGURANGAN STOK ===================
        WriteBatch batch = db.batch();

        // 1. Loop melalui setiap produk di keranjang belanja
        for (Product produk : cartList) {
            String produkId = produk.getId(); // Pastikan model Product Anda memiliki getter untuk ID
            int jumlahBeli = produk.getQuantity(); // Pastikan model Product Anda memiliki getter untuk kuantitas

            if (produkId == null || produkId.isEmpty()) {
                Log.w(TAG, "ID Produk kosong, item dilewati: " + produk.getNamaProduk());
                continue;
            }

            // Buat referensi ke dokumen produk di koleksi 'products'
            DocumentReference produkRef = db.collection("products").document(produkId);

            // Tambahkan operasi pengurangan stok ke dalam batch
            // FieldValue.increment() adalah cara paling aman untuk mengurangi nilai
            batch.update(produkRef, "stok", FieldValue.increment(-jumlahBeli));
            Log.d(TAG, "Menyiapkan pengurangan stok untuk produk: " + produk.getNamaProduk() + " sebanyak " + jumlahBeli);
        }

        // TODO: (Opsional tapi sangat disarankan) Anda juga bisa menambahkan operasi untuk menyimpan catatan transaksi ke dalam batch ini.

        // 2. Jalankan semua operasi dalam batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    // SUKSES! Stok di Firestore telah berhasil dikurangi.
                    Log.d(TAG, "SUKSES: Stok semua produk telah berhasil diperbarui di Firestore.");
                    // Sekarang panggil callback untuk melanjutkan ke dialog berhasil.
                    callback.onComplete();
                })
                .addOnFailureListener(e -> {
                    // GAGAL! Ada masalah saat menghubungi Firestore.
                    Log.e(TAG, "GAGAL: Gagal memperbarui stok di Firestore.", e);
                    // Di sini Anda harus memberi tahu pengguna bahwa transaksi gagal.
                    // Jangan panggil callback.onComplete() agar tidak pindah ke layar berhasil.
                    // Anda bisa melempar error atau menggunakan callback untuk error.
                });
    }
}
