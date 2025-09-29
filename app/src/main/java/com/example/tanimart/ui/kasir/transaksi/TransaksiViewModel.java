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

public class TransaksiViewModel extends ViewModel {

    private final MutableLiveData<List<Product>> produkList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<CartItem>> cartList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Double> totalTagihan = new MutableLiveData<>(0.0);

    private final FirebaseFirestore db;
    private List<Product> allProdukCache = new ArrayList<>();

    public TransaksiViewModel() {
        db = FirebaseFirestore.getInstance();
        loadProduk();
    }

    private void loadProduk() {
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
                        if (p.getId() == null || p.getId().isEmpty()) {
                            p.setId(doc.getId());
                        }
                        temp.add(p);
                    }
                }
            }
            allProdukCache = temp;
            produkList.setValue(new ArrayList<>(temp));
        });
    }

    public LiveData<List<Product>> getProdukList() { return produkList; }
    public LiveData<List<CartItem>> getCartList() { return cartList; }
    public LiveData<Double> getTotalTagihan() { return totalTagihan; }

    public void tambahKeCart(Product produk) {
        List<CartItem> current = cartList.getValue();
        if (current == null) current = new ArrayList<>();

        boolean found = false;
        for (CartItem item : current) {
            if (item.getProduct().getId().equals(produk.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }
        if (!found) {
            current.add(new CartItem(produk, 1));
        }
        cartList.setValue(current);
        hitungTotal(current);
    }

    public void kurangiDariCart(Product produk) {
        List<CartItem> current = cartList.getValue();
        if (current == null) return;

        for (CartItem item : current) {
            if (item.getProduct().getId().equals(produk.getId())) {
                int qty = item.getQuantity() - 1;
                if (qty > 0) {
                    item.setQuantity(qty);
                } else {
                    current.remove(item);
                }
                break;
            }
        }
        cartList.setValue(current);
        hitungTotal(current);
    }

    private void hitungTotal(List<CartItem> list) {
        double total = 0.0;
        for (CartItem item : list) total += item.getSubtotal();
        totalTagihan.setValue(total);
    }

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
        produkList.setValue(hasil);
    }

    public void hapusDariCart(Product produk) {
        List<CartItem> current = cartList.getValue();
        if (current == null) return;

        for (CartItem item : new ArrayList<>(current)) { // clone biar aman pas remove
            if (item.getProduct().getId().equals(produk.getId())) {
                current.remove(item);
                break;
            }
        }
        cartList.setValue(current);
        hitungTotal(current);
    }

}
