package com.example.tanimart.data.repository;

import androidx.annotation.NonNull;

import com.example.tanimart.data.model.Inventory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductRepository {

    private final DatabaseReference productRef;

    public ProductRepository() {
        productRef = FirebaseDatabase.getInstance().getReference("products");
        // "products" = nama node di Firebase Realtime Database
    }

    // Ambil semua produk
    public void getAllProduk(Consumer<List<Inventory>> callback) {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Inventory> produkList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Inventory produk = data.getValue(Inventory.class);
                    if (produk != null) {
                        produk.setId(data.getKey()); // ambil ID dari key
                        produkList.add(produk);
                    }
                }
                callback.accept(produkList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    // Cari produk berdasarkan nama
    public void searchProduk(String keyword, Consumer<List<Inventory>> callback) {
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Inventory> hasil = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Inventory produk = data.getValue(Inventory.class);
                    if (produk != null && produk.getNamaProduk().toLowerCase().contains(keyword.toLowerCase())) {
                        hasil.add(produk);
                    }
                }
                callback.accept(hasil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    // Tambah produk ke database
    public void tambahProduk(Inventory produk) {
        String key = productRef.push().getKey();
        if (key != null) {
            produk.setId(key);
            productRef.child(key).setValue(produk);
        }
    }

    // Update produk
    public void updateProduk(Inventory produk) {
        if (produk.getId() != null) {
            productRef.child(produk.getId()).setValue(produk);
        }
    }

    // Hapus produk
    public void hapusProduk(String id) {
        productRef.child(id).removeValue();
    }
}
