package com.example.tanimart.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class StockNotifier {

    private static final String TAG = "StockNotifier";
    private static final String CHANNEL_ID = "low_stock_channel";
    private final Context context;
    private final FirebaseFirestore db;

    // Batas stok minimum
    private static final double MIN_STOCK_THRESHOLD = 5.0;

    public StockNotifier(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        createNotificationChannel();
    }

    // Cek stok menipis di Firestore
    public void checkLowStockProducts() {
        db.collection("produk") // ganti sesuai nama koleksi di Firestore kamu
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> handleProductList(queryDocumentSnapshots))
                .addOnFailureListener(e -> Log.e(TAG, "Gagal memuat data produk", e));
    }

    private void handleProductList(QuerySnapshot querySnapshot) {
        if (querySnapshot == null || querySnapshot.isEmpty()) {
            Log.d(TAG, "Tidak ada data produk ditemukan");
            return;
        }

        StringBuilder lowStockProducts = new StringBuilder();

        for (DocumentSnapshot doc : querySnapshot) {
            Product product = doc.toObject(Product.class);
            if (product != null) {
                if (product.getStok() <= MIN_STOCK_THRESHOLD) {
                    lowStockProducts.append("- ")
                            .append(product.getNamaProduk())
                            .append(" (stok: ")
                            .append(product.getStok())
                            .append(")\n");
                }
            } else {
                Log.w(TAG, "Dokumen " + doc.getId() + " gagal dikonversi ke objek Product.");
            }
        }

        if (lowStockProducts.length() > 0) {
            sendNotification(lowStockProducts.toString());
        } else {
            Log.d(TAG, "Semua stok produk masih aman ✅");
        }
    }

    private void sendNotification(String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_warning) // pastikan ada ikon ini di drawable
                .setContentTitle("⚠️ Stok Produk Menipis")
                .setContentText("Beberapa produk hampir habis.")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Cek izin notifikasi sebelum menampilkan
        if (androidx.core.app.ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        } else {
            Log.w(TAG, "Izin notifikasi belum diberikan oleh pengguna");
        }

    }

    // Untuk Android 8+ wajib bikin notification channel
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notifikasi Stok Menipis";
            String description = "Peringatan untuk stok produk yang hampir habis";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
