package com.example.tanimart.ui.common.inventory;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.data.repository.InventoryRepository;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.invoke.MutableCallSite;
import java.util.ArrayList;
import java.util.List;

public class DaftarProdukViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>(new ArrayList<>());
//    private final MutableLiveData<List<Product>> inventoryList = new MutableLiveData<>();
    private final FirebaseFirestore db;
    private List<Product> allProdukCache = new ArrayList<>();

    public DaftarProdukViewModel() {
        db = FirebaseFirestore.getInstance();
        loadDaftarProduk();
    }

    public void loadDaftarProduk() {
        db.collection("inventory").addSnapshotListener((QuerySnapshot snapshots, com.google.firebase.firestore.FirebaseFirestoreException e) -> {
            if (e != null) {
                Log.e("DaftarProdukViewModel", "Firestore error", e);
                return;
            }
            List<Product> temp = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product p = doc.toObject(Product.class);
                    if (p != null) {
                        // pastikan id terisi
                        if (p.getId() == null || p.getId().isEmpty()) {
                            p.setId(doc.getId());
                        }
                        temp.add(p);
                    }
                }
            }
            Log.d("DaftarProdukViewModel", "Produk loaded: " + temp.size());
            allProdukCache = temp;
            produkList.setValue(new ArrayList<>(temp));
        });
    }
    public LiveData<List<Product>> getProdukList() { return produkList; }

}
