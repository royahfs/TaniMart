package com.example.tanimart.data.repository;

import com.example.tanimart.data.model.Inventory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class InventoryRepository {
    private final CollectionReference inventoryRef;

    public InventoryRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        inventoryRef = db.collection("inventory"); // nama koleksi di Firestore
    }

    // CREATE
    public void addInventory(Inventory inventory, OnCompleteListener<Void> listener) {
        String id = inventoryRef.document().getId();
        inventory.setId(id);
        inventoryRef.document(id).set(inventory).addOnCompleteListener(listener);
    }

    // READ (ambil semua data)
    public void getAllInventory(OnCompleteListener<QuerySnapshot> listener) {
        inventoryRef.get().addOnCompleteListener(listener);
    }

    // UPDATE
    public void updateInventory(Inventory inventory, OnCompleteListener<Void> listener) {
        inventoryRef.document(inventory.getId()).set(inventory).addOnCompleteListener(listener);
    }

    // DELETE
    public void deleteInventory(String id, OnCompleteListener<Void> listener) {
        inventoryRef.document(id).delete().addOnCompleteListener(listener);
    }
}

