package com.example.tanimart.ui.kasir.transaksi;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransaksiViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<Product>> tagihanList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalTagihan = new MutableLiveData<>(0.0);

    private final FirebaseFirestore db;
    private List<Product> allProdukCache = new ArrayList<>();

    public TransaksiViewModel() {
        db = FirebaseFirestore.getInstance();
        loadProduk();
    }

    private void loadProduk() {
        //  Ganti "produk" ke "inventory" sesuai koleksi Firestore kamu
        db.collection("inventory").addSnapshotListener((QuerySnapshot snapshots, com.google.firebase.firestore.FirebaseFirestoreException e) -> {
            if (e != null) {
                Log.e("TransaksiViewModel", "Firestore error", e);
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
            Log.d("TransaksiViewModel", "Produk loaded: " + temp.size());
            allProdukCache = temp;
            produkList.setValue(new ArrayList<>(temp));
        });
    }

    // Getter LiveData
    public LiveData<List<Product>> getProdukList() { return produkList; }
    public LiveData<List<Product>> getTagihanList() { return tagihanList; }
    public LiveData<Double> getTotalTagihan() { return totalTagihan; }

    // Tambah / Hapus tagihan
    public void tambahKeTagihan(Product produk) {
        List<Product> current = tagihanList.getValue();
        if (current == null) current = new ArrayList<>();
        current.add(produk);
        tagihanList.setValue(current);
        hitungTotal(current);
    }

    public void hapusDariTagihan(Product produk) {
        List<Product> current = tagihanList.getValue();
        if (current != null) {
            current.remove(produk);
            tagihanList.setValue(current);
            hitungTotal(current);
        }
    }

    private void hitungTotal(List<Product> list) {
        double total = 0.0;
        for (Product p : list) total += p.getHargaJual();
        totalTagihan.setValue(total);
    }

    // SEARCH: filter dari cache allProdukCache
    public void cariProduk(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            produkList.setValue(new ArrayList<>(allProdukCache));
            return;
        }
        String k = keyword.toLowerCase().trim();
        List<Product> hasil = new ArrayList<>();
        for (Product p : allProdukCache) {
            if (p.getNamaProduk() != null && p.getNamaProduk().toLowerCase().contains(k)) {
                hasil.add(p);
            }
        }
        Log.d("TransaksiViewModel", "Hasil pencarian \"" + keyword + "\": " + hasil.size());
        produkList.setValue(hasil);
    }

    public void resetProduk() {
        produkList.setValue(new ArrayList<>(allProdukCache));
    }

    // Optional: filter by kategori
    public void filterByKategori(String kategori) {
        if (kategori == null || kategori.equalsIgnoreCase("Semua")) {
            resetProduk();
            return;
        }
        List<Product> hasil = new ArrayList<>();
        for (Product p : allProdukCache) {
            if (p.getKategori() != null && p.getKategori().equalsIgnoreCase(kategori)) {
                hasil.add(p);
            }
        }
        produkList.setValue(hasil);
    }
}
