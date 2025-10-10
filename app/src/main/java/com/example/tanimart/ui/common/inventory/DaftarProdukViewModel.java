package com.example.tanimart.ui.common.inventory;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DaftarProdukViewModel extends ViewModel {
    // TAG untuk memudahkan pencarian di Logcat
    private static final String TAG = "DaftarProdukViewModel";

    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseFirestore db;
    private List<Product> allProdukCache = new ArrayList<>();
    private final MutableLiveData<Integer> lowStockCount = new MutableLiveData<>(0);
    private static final double MINIMUM_STOK = 5; // ambang batas stok menipis


    public DaftarProdukViewModel() {
        db = FirebaseFirestore.getInstance();
        loadDaftarProduk();
    }

    public void loadDaftarProduk() {
        db.collection("inventory").addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "Firestore error saat load daftar", e);
                return;
            }

            List<Product> temp = new ArrayList<>();
            int lowStockCounter = 0; // counter stok menipis

            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product p = doc.toObject(Product.class);
                    if (p != null) {
                        if (p.getId() == null || p.getId().isEmpty()) {
                            p.setId(doc.getId());
                        }

                        temp.add(p);

                        // === CEK STOK MENIPIS ===
                        if (p.getStok() <= MINIMUM_STOK) {
                            lowStockCounter++;
                            showLowStockNotification(p);
                        }
                    }
                }
            }

            Log.d(TAG, "Daftar produk berhasil dimuat: " + temp.size() + " item.");
            allProdukCache = temp;
            produkList.setValue(new ArrayList<>(temp));

            // update LiveData stok menipis
            lowStockCount.setValue(lowStockCounter);
        });
    }


    private void showLowStockNotification(Product product) {
        // Gunakan ApplicationContext, karena ViewModel tidak punya context langsung
        android.content.Context context =
                androidx.lifecycle.ProcessLifecycleOwner.get().getLifecycle().getCurrentState().isAtLeast(
                        androidx.lifecycle.Lifecycle.State.CREATED
                ) ?
                        com.google.firebase.FirebaseApp.getInstance().getApplicationContext() :
                        null;

        if (context == null) return;

        String channelId = "low_stock_channel";
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    channelId,
                    "Stok Menipis",
                    android.app.NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifikasi untuk produk dengan stok menipis");
            notificationManager.createNotificationChannel(channel);
        }

        android.app.Notification notification = new androidx.core.app.NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("Stok Menipis")
                .setContentText("Produk \"" + product.getNamaProduk() + "\" tersisa " + product.getStok() + " " + product.getSatuan())
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // ID notifikasi bisa unik per produk
        int notificationId = product.getId().hashCode();
        notificationManager.notify(notificationId, notification);
    }


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
        // Tidak perlu memanggil loadDaftarProduk() di sini karena addSnapshotListener sudah otomatis me-refresh
    }

    public LiveData<List<Product>> getProdukList() {
        return produkList;
    }

    /**
     * Mengambil detail satu produk dari Firestore berdasarkan ID.
     * @param produkId ID dari dokumen produk di Firestore.
     * @return LiveData yang akan berisi objek Product.
     */
    public LiveData<Product> getProdukDetail(String produkId) {
        MutableLiveData<Product> produkDetailLiveData = new MutableLiveData<>();

        db.collection("inventory").document(produkId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            Log.d(TAG, "Dokumen detail ditemukan di 'inventory': " + document.getId());
                            Log.d(TAG, "Data mentah: " + document.getData());

                            Product product = document.toObject(Product.class);
                            if (product != null) {
                                product.setId(document.getId());
                                Log.d(TAG, "Konversi objek BERHASIL. Nama Produk: " + product.getNamaProduk());

                                // ================== INI KUNCI UTAMANYA ==================
                                // Kita cek secara spesifik URL yang didapat untuk halaman detail
                                Log.d(TAG, "--> URL Gambar untuk Detail: " + product.getImageUrl());
                                // ========================================================

                                produkDetailLiveData.postValue(product);
                            } else {
                                Log.e(TAG, "Konversi objek GAGAL. Objeknya null. Periksa nama field di Firestore vs model Product.java.");
                                produkDetailLiveData.postValue(null);
                            }
                        } else {
                            Log.w(TAG, "Dokumen tidak ditemukan di 'inventory' dengan ID: " + produkId);
                            produkDetailLiveData.postValue(null);
                        }
                    } else {
                        Log.e(TAG, "Gagal mengambil dokumen dari 'inventory': ", task.getException());
                        produkDetailLiveData.postValue(null);
                    }
                });

        return produkDetailLiveData;
    }

}
