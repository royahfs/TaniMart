package com.example.tanimart.ui.common.inventory;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.ProdukKeluar;
import com.google.firebase.Timestamp; // <-- IMPORT BARU DAN PENTING
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query; // <-- IMPORT BARU DAN PENTING
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProdukKeluarViewModel extends ViewModel {

    public enum SortType {
        NONE,
        BY_DATE_DESC, // Tanggal Terbaru
        BY_DATE_ASC,  // Tanggal Terlama
        BY_NAME_ASC,  // Abjad A-Z
        BY_NAME_DESC  // Abjad Z-A
    }
    private final MutableLiveData<List<ProdukKeluar>> produkKeluarList = new MutableLiveData<>();
    private final List<ProdukKeluar> originalList = new ArrayList<>(); // Untuk menyimpan data asli

    public ProdukKeluarViewModel() {
        loadProdukKeluar();
    }

    public LiveData<List<ProdukKeluar>> getProdukKeluarList() {
        return produkKeluarList;
    }

    private void loadProdukKeluar() {
        // PERBAIKAN: Gunakan order by untuk mendapatkan data yang sudah terurut dari awal
        FirebaseFirestore.getInstance().collection("produk_keluar")
                .orderBy("tanggalKeluar", Query.Direction.DESCENDING) // Urutkan berdasarkan tanggal terbaru
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalList.clear(); // Bersihkan list lama sebelum diisi
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ProdukKeluar p = doc.toObject(ProdukKeluar.class);
                        originalList.add(p);
                    }
                    // Tampilkan data awal yang sudah terurut
                    produkKeluarList.setValue(new ArrayList<>(originalList));
                });
    }

    // --- PERBAIKAN UTAMA DI SINI ---
    public void sortData(SortType sortType) {
        // Ambil data dari list original agar tidak menumpuk hasil sorting sebelumnya
        List<ProdukKeluar> listToSsort = new ArrayList<>(originalList);

        if (listToSsort.isEmpty()) {
            return; // Tidak ada data untuk diurutkan
        }

        switch (sortType) {
            case BY_DATE_DESC: // Tanggal Terbaru ke Terlama
                Collections.sort(listToSsort, (o1, o2) -> {
                    Timestamp t1 = o1.getTanggalKeluar();
                    Timestamp t2 = o2.getTanggalKeluar();
                    if (t1 == null || t2 == null) return 0;
                    return t2.compareTo(t1); // Langsung bandingkan Timestamp
                });
                break;

            case BY_DATE_ASC: // Tanggal Terlama ke Terbaru
                Collections.sort(listToSsort, (o1, o2) -> {
                    Timestamp t1 = o1.getTanggalKeluar();
                    Timestamp t2 = o2.getTanggalKeluar();
                    if (t1 == null || t2 == null) return 0;
                    return t1.compareTo(t2); // Dibalik untuk ascending
                });
                break;

            case BY_NAME_ASC: // Abjad A-Z
                Collections.sort(listToSsort, (o1, o2) ->
                        o1.getNamaProduk().compareToIgnoreCase(o2.getNamaProduk()));
                break;

            case BY_NAME_DESC: // Abjad Z-A
                Collections.sort(listToSsort, (o1, o2) ->
                        o2.getNamaProduk().compareToIgnoreCase(o1.getNamaProduk()));
                break;
        }

        // Setelah diurutkan, perbarui LiveData agar UI ikut berubah
        produkKeluarList.setValue(listToSsort);
    }
}
