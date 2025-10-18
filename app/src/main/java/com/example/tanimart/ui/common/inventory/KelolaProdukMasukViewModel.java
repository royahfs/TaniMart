package com.example.tanimart.ui.common.inventory;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.data.repository.InventoryRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class KelolaProdukMasukViewModel extends ViewModel {
    private static final String TAG = "KelolaProdukMasukVM";
    private final InventoryRepository inventoryRepository;
    private final LiveData<List<Product>> produkList;

    public KelolaProdukMasukViewModel() {
        inventoryRepository = InventoryRepository.getInstance(); // Ambil instance tunggal
        produkList = inventoryRepository.getAllProducts(); // Cukup ambil LiveData dari repository
    }

    public LiveData<List<Product>> getProdukList() {
        return produkList;
    }

    // 3. Delegasikan semua operasi CUD (Create, Update, Delete) ke Repository
    public void addProduct(Product product) {
        // Panggil metode yang sesuai di Repository
        inventoryRepository.addProduct(product,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Produk berhasil ditambahkan via repository.");
                    } else {
                        Log.e(TAG, "Gagal menambahkan produk.", task.getException());
                    }
                });
    }

    public void updateProduct(Product product) {
        inventoryRepository.updateProduct(product,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Produk berhasil diupdate via repository.");
                    } else {
                        Log.e(TAG, "Gagal mengupdate produk.", task.getException());
                    }
                });
    }

    public void deleteProduct(String id) {
        inventoryRepository.deleteProduct(id,
                task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Produk berhasil dihapus via repository.");
                    } else {
                        Log.e(TAG, "Gagal menghapus produk.", task.getException());
                    }
                });
    }
}
