package com.example.tanimart.ui.kasir.laporan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Transaksi;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LaporanViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<List<Transaksi>> transaksiLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalPendapatan = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> jumlahTransaksi = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> produkTerjual = new MutableLiveData<>(0);

    public LiveData<List<Transaksi>> getTransaksiList() { return transaksiLiveData; }
    public LiveData<Integer> getTotalPendapatan() { return totalPendapatan; }
    public LiveData<Integer> getJumlahTransaksi() { return jumlahTransaksi; }
    public LiveData<Integer> getProdukTerjual() { return produkTerjual; }

    // --- PERBAIKAN UTAMA ADA DI SINI ---
    public LaporanViewModel() {
        // Panggil filterData dengan "Harian" sebagai default saat ViewModel dibuat.
        // Argumen startDate dan endDate bisa null karena akan di-generate di dalam method.
        filterData("Harian", null, null);
    }
    // ------------------------------------

    // filter data berdasarkan pilihan
    public void filterData(String type, Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();

        // Buat variabel baru untuk menampung tanggal yang sudah dihitung
        Date finalStartDate = startDate;
        Date finalEndDate = endDate;

        switch (type) {
            case "Harian":
                finalStartDate = getStartOfDay(new Date());
                finalEndDate = getEndOfDay(new Date());
                break;
            case "Mingguan":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                finalStartDate = getStartOfDay(cal.getTime());
                cal.add(Calendar.DAY_OF_WEEK, 6);
                finalEndDate = getEndOfDay(cal.getTime());
                break;
            case "Bulanan":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                finalStartDate = getStartOfDay(cal.getTime());
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                finalEndDate = getEndOfDay(cal.getTime());
                break;
            case "Custom":
                // Pastikan startDate dan endDate tidak null untuk mode custom
                if (finalStartDate != null && finalEndDate != null) {
                    finalStartDate = getStartOfDay(finalStartDate);
                    finalEndDate = getEndOfDay(finalEndDate);
                }
                break;
        }

        // Pastikan tanggal tidak null sebelum memuat data
        if (finalStartDate != null && finalEndDate != null) {
            loadTransaksi(finalStartDate, finalEndDate, "Semua");
        } else {
            Log.e("LaporanViewModel", "Tanggal start atau end tidak valid untuk filter tipe: " + type);
        }
    }

    // ambil data dari firestore sesuai rentang tanggal
    private void loadTransaksi(Date startDate, Date endDate, String metode) {
        Query query = db.collection("transaksi");
        if (!metode.equalsIgnoreCase("Semua")) {
            query = query.whereEqualTo("metode", metode);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Transaksi> list = new ArrayList<>();
            double totalPendapatanVal = 0.0;
            int jumlahTransaksiVal = 0;
            // produkTerjualVal tidak bisa dihitung karena tidak ada 'items' di model Transaksi
            int produkTerjualVal = 0;

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Transaksi transaksi = doc.toObject(Transaksi.class);

                if (transaksi != null && transaksi.getTanggal() != null) {
                    Date tgl = transaksi.getTanggal().toDate();

                    if (!tgl.before(startDate) && !tgl.after(endDate)) {
                        transaksi.setIdTransaksi(doc.getId());
                        list.add(transaksi);

                        // Gunakan method getTotal() dari model Transaksi.java
                        totalPendapatanVal += transaksi.getTotal();
                        jumlahTransaksiVal++;
                    }
                } else {
                    Log.w("LaporanViewModel", "Transaksi dengan ID: " + doc.getId() + " tidak valid atau tidak memiliki tanggal.");
                }
            }


            transaksiLiveData.setValue(list);
            totalPendapatan.setValue((int) totalPendapatanVal);
            jumlahTransaksi.setValue(jumlahTransaksiVal);
            produkTerjual.setValue(produkTerjualVal); // Akan selalu 0
        }) .addOnFailureListener(e -> {
            Log.e("LaporanViewModel", "Gagal memuat transaksi", e);
        });
    }

    private Date getStartOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
}
