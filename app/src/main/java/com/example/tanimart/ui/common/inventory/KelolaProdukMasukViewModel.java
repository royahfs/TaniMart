// File: KelolaProdukMasukViewModel.java (VERSI REVISI)

package com.example.tanimart.ui.common.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.data.repository.InventoryRepository;
import com.google.firebase.firestore.ListenerRegistration; // <-- Import
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class KelolaProdukMasukViewModel extends ViewModel {
    private final InventoryRepository repository;
    private final MutableLiveData<List<Inventory>> inventoryList = new MutableLiveData<>();
    private ListenerRegistration inventoryListener; // <-- Tambahkan ini untuk menyimpan referensi listener

    public KelolaProdukMasukViewModel() {
        repository = new InventoryRepository();
        // Ganti pemanggilan method lama dengan yang baru
        setupInventoryListener();
    }

    public LiveData<List<Inventory>> getInventoryList() {
        return inventoryList;
    }

    // Method LAMA, sudah tidak dipakai lagi. Bisa dihapus atau biarkan saja.
    /*
    public void loadInventory() {
        repository.getAllInventory(task -> { ... });
    }
    */

    // --- METODE BARU UNTUK MENDENGARKAN PERUBAHAN ---
    private void setupInventoryListener() {
        inventoryListener = repository.listenToAllInventory((snapshots, error) -> {
            if (error != null) {
                // Handle error, misalnya dengan menampilkan log
                System.err.println("Listen failed: " + error);
                return;
            }

            if (snapshots != null) {
                List<Inventory> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : snapshots) {
                    Inventory inventory = doc.toObject(Inventory.class);
                    // Pastikan ID dokumen tersimpan, ini praktik yang baik
                    inventory.setId(doc.getId());
                    list.add(inventory);
                }
                // Kirim daftar yang sudah diperbarui ke LiveData
                inventoryList.setValue(list);
            }
        });
    }

    // Metode add, update, delete tidak perlu memanggil loadInventory() lagi
    // karena listener akan menangani pembaruan secara otomatis.
    public void addInventory(Inventory inventory) {
        repository.addInventory(inventory, task -> { /* Tidak perlu melakukan apa-apa di sini */ });
    }

    public void updateInventory(Inventory inventory) {
        repository.updateInventory(inventory, task -> { /* Tidak perlu melakukan apa-apa di sini */ });
    }

    public void deleteInventory(String id) {
        repository.deleteInventory(id, task -> { /* Tidak perlu melakukan apa-apa di sini */ });
    }

    // --- PENTING: Lepas listener saat ViewModel dihancurkan ---
    @Override
    protected void onCleared() {
        super.onCleared();
        if (inventoryListener != null) {
            inventoryListener.remove(); // Hentikan listener untuk mencegah memory leak
        }
    }
}
