package com.example.tanimart.ui.kasir.laporan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Transaksi;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<List<Transaksi>> transaksiLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> ringkasanLiveData = new MutableLiveData<>();

    public LiveData<List<Transaksi>> getTransaksiList() {
        return transaksiLiveData;
    }

    public LiveData<String> getRingkasan() {
        return ringkasanLiveData;
    }

    public void loadTransaksi(Date startDate, Date endDate, String metode) {
        Query query = db.collection("transaksi");

        // filter metode pembayaran kalau tidak "semua"
        if (!metode.equalsIgnoreCase("Semua")) {
            query = query.whereEqualTo("metode", metode);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Transaksi> list = new ArrayList<>();
            double totalPendapatan = 0;
            int jumlahTransaksi = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Transaksi transaksi = doc.toObject(Transaksi.class);
                if (transaksi != null) {
                    try {
                        Date tgl = sdf.parse(transaksi.getTanggal());
                        if (tgl != null && (tgl.equals(startDate) || tgl.after(startDate)) && (tgl.equals(endDate) || tgl.before(endDate))) {
                            transaksi.setIdTransaksi(doc.getId());
                            list.add(transaksi);

                            totalPendapatan += transaksi.getTotalHarga();
                            jumlahTransaksi++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            transaksiLiveData.setValue(list);

            String ringkasan = "Total Pendapatan: Rp " + totalPendapatan +
                    "\nJumlah Transaksi: " + jumlahTransaksi +
                    "\nProduk Terjual: (hitung dari items jika ada)";
            ringkasanLiveData.setValue(ringkasan);

        });
    }
}
