package com.example.tanimart.ui.common.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.ProdukKeluar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProdukKeluarViewModel extends ViewModel {

    private final MutableLiveData<List<ProdukKeluar>> produkKeluarList = new MutableLiveData<>();

    public ProdukKeluarViewModel() {
        loadProdukKeluar();
    }

    public LiveData<List<ProdukKeluar>> getProdukKeluarList() {
        return produkKeluarList;
    }

    private void loadProdukKeluar() {
        FirebaseFirestore.getInstance().collection("produk_keluar")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProdukKeluar> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ProdukKeluar p = doc.toObject(ProdukKeluar.class);
                        list.add(p);
                    }
                    produkKeluarList.setValue(list);
                });
    }
}
