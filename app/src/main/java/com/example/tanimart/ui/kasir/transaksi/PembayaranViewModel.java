package com.example.tanimart.ui.kasir.transaksi;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PembayaranViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CartItem>> cartList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalTagihan = new MutableLiveData<>(0.0);
    private final FirebaseFirestore db;

    public PembayaranViewModel() {
        db = FirebaseFirestore.getInstance();
        loadProduk();
    }

    private void loadProduk() {
        db.collection("inventory").addSnapshotListener((QuerySnapshot snapshots, com.google.firebase.firestore.FirebaseFirestoreException e) -> {
            if (e != null) {
                Log.e("PembayaranViewModel", "Firestore error", e);
                return;
            }
            List<Product> temp = new ArrayList<>();
            if (snapshots != null) {
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Product p = doc.toObject(Product.class);
                    if (p != null) {
                        if (p.getId() == null || p.getId().isEmpty()) {
                            p.setId(doc.getId());
                        }
                        temp.add(p);
                    }
                }
            }
            produkList.setValue(temp);
        });
    }

    public LiveData<List<Product>> getProdukList() { return produkList; }
    public LiveData<List<CartItem>> getCartList() { return cartList; }
    public LiveData<Double> getTotalTagihan() { return totalTagihan; }

    public void setTotalTagihan(double total) {
        totalTagihan.setValue(total);
    }
}
