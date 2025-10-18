package com.example.tanimart.ui.common.inventory;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.example.tanimart.data.repository.InventoryRepository; // Pastikan import ini benar
import com.google.firebase.firestore.FirebaseFirestore; // Diperlukan untuk checkout

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarProdukViewModel extends ViewModel {
    private static final String TAG = "DaftarProdukViewModel";

    private final InventoryRepository inventoryRepository;
    private final LiveData<List<Product>> produkList;
    private final LiveData<Integer> lowStockCount;

    private final FirebaseFirestore db; // Tetap perlukan untuk operasi write seperti checkout
    private static final double MINIMUM_STOK = 5; // ambang batas stok menipis

    public DaftarProdukViewModel() {
        inventoryRepository = InventoryRepository.getInstance(); // Ambil instance tunggal
        produkList = inventoryRepository.getAllProducts(); // Cukup ambil LiveData dari repository
        db = FirebaseFirestore.getInstance(); // Inisialisasi db untuk fungsi checkout

        // Logika untuk menghitung stok rendah bisa dipindahkan ke sini
        lowStockCount = Transformations.map(produkList, products -> {
            int counter = 0;
            if (products != null) {
                for (Product p : products) {
                    if (p.getStok() <= MINIMUM_STOK) {
                        counter++;
                        // Memanggil notifikasi dari sini juga bisa
                        // showLowStockNotification(p);
                    }
                }
            }
            return counter;
        });
    }

    public LiveData<List<Product>> getProdukList() {
        return produkList;
    }

    public LiveData<Integer> getLowStockCount() {
        return lowStockCount;
    }


    private void showLowStockNotification(Product product) {
    }

    // Metode checkout masih relevan di sini karena ini adalah sebuah "aksi" atau "use case"
    public void checkoutProdukList(List<Product> cartList, String idTransaksi) {
        for (Product p : cartList) {
            final Product product = p;
            final int jumlahBeli = product.getQuantity();

            db.collection("inventory").document(product.getId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        Long stok = doc.getLong("stok");
                        if (stok == null) stok = 0L;

                        long stokBaru = stok - jumlahBeli;
                        if (stokBaru < 0) stokBaru = 0;

                        // Update stok
                        db.collection("inventory").document(product.getId())
                                .update("stok", stokBaru);

                        // Catat ke produk_keluar
                        Map<String, Object> data = new HashMap<>();
                        data.put("productId", product.getId());
                        data.put("namaProduk", product.getNamaProduk());
                        data.put("jumlahKeluar", jumlahBeli);
                        data.put("tanggalKeluar", new Date());
                        data.put("idTransaksi", idTransaksi);
                        db.collection("produk_keluar").add(data);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "Gagal checkout untuk produk: " + product.getId(), e));
        }
        // Listener di Repository akan otomatis menangani pembaruan data, jadi tidak perlu refresh manual.
    }


    public LiveData<Product> getProdukDetail(String produkId) {
        // Delegasikan pemanggilan ke Repository
        return inventoryRepository.getProductById(produkId);
    }
}
