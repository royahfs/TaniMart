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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void checkoutProdukList(List<Product> cartList, String idTransaksi) {
        for (Product p : cartList) {
            final Product product = p; // <-- buat final
            final int jumlahBeli = product.getQuantity(); // <-- jumlah beli juga final

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

                        // Refresh list produk
                        loadDaftarProduk();
                    });
        }
    }




    public LiveData<List<Product>> getProdukList() { return produkList; }

}
