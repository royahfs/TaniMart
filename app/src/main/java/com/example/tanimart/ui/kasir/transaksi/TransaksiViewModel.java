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
    private String lastSearchKeyword = "";

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


            syncCartWithCache();


            cariProduk(lastSearchKeyword);
        });
    }

    private void syncCartWithCache() {
        List<CartItem> currentCart = cartList.getValue();
        // Jika keranjang atau cache kosong, tidak ada yang perlu disinkronkan.
        if (currentCart == null || currentCart.isEmpty() || allProdukCache.isEmpty()) {
            return;
        }

        boolean hasChanges = false;
        // Loop untuk setiap item di dalam keranjang
        for (CartItem item : currentCart) {
            // Loop untuk setiap produk di cache (data terbaru dari Firestore)
            for (Product freshProduct : allProdukCache) {
                // Jika ID produknya cocok...
                if (item.getProduct().getId().equals(freshProduct.getId())) {
                    // Ganti objek produk LAMA di dalam CartItem dengan objek produk BARU.
                    item.setProduct(freshProduct);
                    hasChanges = true; // Tandai bahwa ada perubahan data di dalam keranjang.
                    break; // Hemat proses, lanjut ke item keranjang berikutnya.
                }
            }
        }


        if (hasChanges) {
            cartList.setValue(currentCart);
            hitungTotal(currentCart);
        }
    }


    // --- Getters LiveData (Tidak Berubah) ---
    public LiveData<List<Product>> getProdukList() { return produkList; }
    public LiveData<List<CartItem>> getCartList() { return cartList; }
    public LiveData<Double> getTotalTagihan() { return totalTagihan; }

    // --- Operasi cart (Tidak Berubah) ---
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

        for (CartItem item : new ArrayList<>(current)) {
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
        if (list != null) {
            for (CartItem item : list) {
                total += item.getSubtotal();
            }
        }
        totalTagihan.setValue(total);
    }

    public void cariProduk(String keyword) {
        // Simpan keyword yang baru untuk digunakan lagi nanti
        lastSearchKeyword = (keyword != null) ? keyword : "";

        if (lastSearchKeyword.trim().isEmpty()) {
            produkList.setValue(new ArrayList<>(allProdukCache));
            return;
        }
        String k = lastSearchKeyword.toLowerCase().trim();
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

    // --- Clear cart (Tidak Berubah) ---
    public void clearCart() {
        cartList.setValue(new ArrayList<>());
        totalTagihan.setValue(0.0);
    }

    // --- Set cart dari CartItem langsung (Tidak Berubah) ---
    public void setCartList(List<CartItem> list) {
        if (list == null) list = new ArrayList<>();
        cartList.setValue(list);
        hitungTotal(list);
    }

    // --- Set cart dari daftar Product (konversi Product -> CartItem) (Tidak Berubah) ---
    public void setCartListFromProducts(List<Product> products) {
        List<CartItem> newCart = new ArrayList<>();
        if (products != null) {
            for (Product p : products) {
                if (p == null) continue;
                // Ambil quantity dari Product (asumsikan Product punya getQuantity())
                int qty = 1;
                try {
                    qty = p.getQuantity();
                    if (qty <= 0) qty = 1;
                } catch (Exception ignored) { /* kalau tidak ada getQuantity, tetap 1 */ }

                newCart.add(new CartItem(p, qty));
            }
        }
        cartList.setValue(newCart);
        hitungTotal(newCart);
    }

    // --- Set total tagihan langsung (jika diperlukan) (Tidak Berubah) ---
    public void setTotalTagihan(Double total) {
        totalTagihan.setValue(total != null ? total : 0.0);
    }
}
