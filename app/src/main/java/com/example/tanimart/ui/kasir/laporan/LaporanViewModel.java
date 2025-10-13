package com.example.tanimart.ui.kasir.laporan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Transaksi;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LaporanViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<Transaksi>> _transaksiList = new MutableLiveData<>();
    public LiveData<List<Transaksi>> getTransaksiList() {
        return _transaksiList;
    }

    private final MutableLiveData<Double> _totalPendapatan = new MutableLiveData<>(0.0);
    public LiveData<Double> getTotalPendapatan() {
        return _totalPendapatan;
    }

    private final MutableLiveData<Integer> _jumlahTransaksi = new MutableLiveData<>(0);
    public LiveData<Integer> getJumlahTransaksi() {
        return _jumlahTransaksi;
    }

    // Metode yang dipanggil dari Activity
    public void loadLaporanHariIni() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date awalHari = cal.getTime();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        Date akhirHari = cal.getTime();

        db.collection("transaksi")
                .whereGreaterThanOrEqualTo("tanggal", awalHari)
                .whereLessThanOrEqualTo("tanggal", akhirHari)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        // Handle error
                        return;
                    }

                    if (snapshots != null) {
                        List<Transaksi> list = new ArrayList<>();
                        double total = 0.0;
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Transaksi trx = doc.toObject(Transaksi.class);
                            list.add(trx);
                            total += trx.getTotal();
                        }
                        _transaksiList.setValue(list);
                        _totalPendapatan.setValue(total);
                        _jumlahTransaksi.setValue(list.size());
                    }
                });
    }
}
