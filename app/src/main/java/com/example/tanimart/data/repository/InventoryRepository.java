package com.example.tanimart.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.android.gms.tasks.OnCompleteListener; // <-- PENTING: Import ini
import com.google.android.gms.tasks.Task; // <-- PENTING: Import ini
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {

    private static final String TAG = "InventoryRepository";
    private static volatile InventoryRepository instance;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Product>> allProductsLiveData = new MutableLiveData<>();
    private ListenerRegistration productsListener;

    // Singleton Pattern: Agar hanya ada satu instance Repository di seluruh aplikasi
    public static InventoryRepository getInstance() {
        if (instance == null) {
            synchronized (InventoryRepository.class) {
                if (instance == null) {
                    instance = new InventoryRepository();
                }
            }
        }
        return instance;
    }

    // Constructor dibuat private untuk Singleton
    private InventoryRepository() {
        listenToAllProducts();
    }

    // Metode ini yang akan di-observe oleh semua ViewModel
    public LiveData<List<Product>> getAllProducts() {
        return allProductsLiveData;
    }

    private void listenToAllProducts() {
        // Hentikan listener lama jika ada, untuk menghindari duplikasi
        if (productsListener != null) {
            productsListener.remove();
        }

        productsListener = db.collection("inventory").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Error listening to products", e);
                return;
            }

            if (snapshots != null) {
                List<Product> productList = new ArrayList<>();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product product = doc.toObject(Product.class);
                    if (product != null) {
                        product.setId(doc.getId()); // Selalu pastikan ID ter-set
                        productList.add(product);
                    }
                }
                Log.d(TAG, "Data products updated. Total: " + productList.size());
                allProductsLiveData.postValue(productList); // Kirim data baru ke semua observer
            }
        });
    }

    public LiveData<Product> getProductById(String produkId) {
        MutableLiveData<Product> produkDetailLiveData = new MutableLiveData<>();

        if (produkId == null || produkId.trim().isEmpty()) {
            produkDetailLiveData.setValue(null);
            Log.w(TAG, "getProductById dipanggil dengan ID null atau kosong.");
            return produkDetailLiveData;
        }

        db.collection("inventory").document(produkId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Product product = documentSnapshot.toObject(Product.class);
                        if (product != null) {
                            product.setId(documentSnapshot.getId()); // Pastikan ID-nya ter-set
                            produkDetailLiveData.postValue(product);
                            Log.d(TAG, "Detail produk berhasil diambil: " + product.getNamaProduk());
                        } else {
                            produkDetailLiveData.postValue(null);
                            Log.e(TAG, "Gagal konversi dokumen ke objek Product. ID: " + produkId);
                        }
                    } else {
                        produkDetailLiveData.postValue(null);
                        Log.w(TAG, "Dokumen produk tidak ditemukan dengan ID: " + produkId);
                    }
                })
                .addOnFailureListener(e -> {
                    produkDetailLiveData.postValue(null);
                    Log.e(TAG, "Gagal mengambil detail produk untuk ID: " + produkId, e);
                });

        return produkDetailLiveData;
    }


    /**
     * Menambahkan produk baru ke koleksi 'inventory' di Firestore.
     * Listener di repository ini akan otomatis memperbarui LiveData.
     * @param product Objek produk yang akan ditambahkan.
     * @param listener Callback untuk mengetahui status operasi (sukses/gagal).
     */
    public void addProduct(Product product, OnCompleteListener<Void> listener) {
        db.collection("inventory")
                .add(product) // Firestore akan otomatis generate ID
                .addOnSuccessListener(documentReference -> {
                    // Setelah sukses, kita bisa update ID di objek jika perlu,
                    // tapi listener real-time sudah cukup untuk refresh UI.
                    Log.d(TAG, "Produk berhasil ditambahkan dengan ID: " + documentReference.getId());
                    // Kita buat Task dummy yang sukses untuk diteruskan ke ViewModel
                    listener.onComplete(Tasks.forResult(null));
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Gagal menambahkan produk", e);
                    // Kita buat Task dummy yang gagal untuk diteruskan ke ViewModel
                    listener.onComplete(Tasks.forException(e));
                });
    }

    /**
     * Memperbarui data produk yang ada di Firestore berdasarkan ID-nya.
     * @param product Objek produk dengan data terbaru. ID tidak boleh null.
     * @param listener Callback untuk mengetahui status operasi.
     */
    public void updateProduct(Product product, OnCompleteListener<Void> listener) {
        if (product.getId() == null || product.getId().isEmpty()) {
            Log.e(TAG, "Gagal update: ID produk null atau kosong.");
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Product ID cannot be null")));
            return;
        }
        db.collection("inventory").document(product.getId())
                .set(product) // .set() akan menimpa seluruh dokumen dengan data baru
                .addOnCompleteListener(listener);
    }

    /**
     * Menghapus produk dari Firestore berdasarkan ID-nya.
     * @param productId ID dari produk yang akan dihapus.
     * @param listener Callback untuk mengetahui status operasi.
     */
    public void deleteProduct(String productId, OnCompleteListener<Void> listener) {
        if (productId == null || productId.isEmpty()) {
            Log.e(TAG, "Gagal hapus: ID produk null atau kosong.");
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Product ID cannot be null")));
            return;
        }
        db.collection("inventory").document(productId)
                .delete()
                .addOnCompleteListener(listener);
    }


    // --- Helper untuk membuat Task dummy ---
    // Anda mungkin perlu mengimpor 'com.google.android.gms.tasks.Tasks'
    private static class Tasks {
        public static <TResult> Task<TResult> forResult(TResult result) {
            com.google.android.gms.tasks.TaskCompletionSource<TResult> completionSource = new com.google.android.gms.tasks.TaskCompletionSource<>();
            completionSource.setResult(result);
            return completionSource.getTask();
        }

        public static <TResult> Task<TResult> forException(Exception e) {
            com.google.android.gms.tasks.TaskCompletionSource<TResult> completionSource = new com.google.android.gms.tasks.TaskCompletionSource<>();
            completionSource.setException(e);
            return completionSource.getTask();
        }
    }


    // Metode untuk menghentikan listener, bisa dipanggil saat aplikasi benar-benar ditutup
    public void cleanup() {
        if (productsListener != null) {
            productsListener.remove();
        }
    }
}
