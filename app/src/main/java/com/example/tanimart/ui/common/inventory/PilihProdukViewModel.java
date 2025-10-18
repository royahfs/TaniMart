package com.example.tanimart.ui.common.inventory;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class PilihProdukViewModel extends ViewModel {

    private static final String TAG = "PilihProdukVM";

    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public PilihProdukViewModel() {
        loadProducts(); // Langsung muat produk saat ViewModel dibuat
    }

    // Fungsi getter agar Activity bisa meng-observe (mengamati) perubahan data
    public LiveData<List<Product>> getProducts() {
        return produkList;
    }

    private void loadProducts() {

        db.collection("inventory")
                .orderBy("namaProduk", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e(TAG, "Gagal memuat produk dari Firestore", error);
                        produkList.postValue(null);
                        return;
                    }

                    if (snapshots != null) {
                        List<Product> tempList = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Product product = doc.toObject(Product.class);
                            if (product != null) {
                                product.setId(doc.getId()); // Pastikan ID dokumen ikut tersimpan
                                tempList.add(product);
                            }
                        }
                        produkList.postValue(tempList);
                        Log.d(TAG, "Berhasil memuat " + tempList.size() + " produk untuk dipilih.");
                    }
                });
    }
}
