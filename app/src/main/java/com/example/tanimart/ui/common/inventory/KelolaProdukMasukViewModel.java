package com.example.tanimart.ui.common.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.data.repository.InventoryRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KelolaProdukMasukViewModel extends ViewModel {
    private final InventoryRepository repository;
    private final MutableLiveData<List<Inventory>> inventoryList = new MutableLiveData<>();

    public KelolaProdukMasukViewModel() {
        repository = new InventoryRepository();
        loadInventory();
    }

    public LiveData<List<Inventory>> getInventoryList() {
        return inventoryList;
    }

    public void loadInventory() {
        repository.getAllInventory(task -> {
            if (task.isSuccessful()) {
                List<Inventory> list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Inventory inventory = doc.toObject(Inventory.class);
                    list.add(inventory);
                }
                inventoryList.setValue(list);
            }
        });
    }

    public void addInventory(Inventory inventory) {
        repository.addInventory(inventory, task -> {
            if (task.isSuccessful()) {
                loadInventory();
            }
        });
    }

    public void updateInventory(Inventory inventory) {
        repository.updateInventory(inventory, task -> {
            if (task.isSuccessful()) {
                loadInventory();
            }
        });
    }

    public void deleteInventory(String id) {
        repository.deleteInventory(id, task -> {
            if (task.isSuccessful()) {
                loadInventory();
            }
        });
    }
}



