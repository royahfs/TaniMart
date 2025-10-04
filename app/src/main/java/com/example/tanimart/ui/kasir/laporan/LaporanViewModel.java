package com.example.tanimart.ui.kasir.laporan;

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

    // filter data berdasarkan pilihan
    public void filterData(String type, Date startDate, Date endDate) {
        Calendar cal = Calendar.getInstance();

        switch (type) {
            case "Harian":
                startDate = getStartOfDay(new Date());
                endDate = getEndOfDay(new Date());
                break;
            case "Mingguan":
                cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
                startDate = getStartOfDay(cal.getTime());
                cal.add(Calendar.DAY_OF_WEEK, 6);
                endDate = getEndOfDay(cal.getTime());
                break;
            case "Bulanan":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = getStartOfDay(cal.getTime());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endDate = getEndOfDay(cal.getTime());
                break;
            case "Custom":
                // startDate & endDate sudah dikirim dari activity
                break;
        }

        loadTransaksi(startDate, endDate, "Semua");
    }

    // ambil data dari firestore sesuai rentang tanggal
    private void loadTransaksi(Date startDate, Date endDate, String metode) {
        Query query = db.collection("transaksi");
        if (!metode.equalsIgnoreCase("Semua")) {
            query = query.whereEqualTo("metode", metode);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Transaksi> list = new ArrayList<>();
            int totalPendapatanVal = 0;
            int jumlahTransaksiVal = 0;
            int produkTerjualVal = 0;

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Transaksi transaksi = doc.toObject(Transaksi.class);
                if (transaksi != null) {
                    Date tgl = transaksi.getTanggal().toDate(); // ✅ langsung dari Timestamp
                    if (tgl != null && !tgl.before(startDate) && !tgl.after(endDate)) {
                        transaksi.setIdTransaksi(doc.getId());
                        list.add(transaksi);

                        totalPendapatanVal += transaksi.getTotal(); // ✅ gunakan getTotal()
                        jumlahTransaksiVal++;
                        // produkTerjualVal += transaksi.getJumlahProduk(); // bisa aktifkan kalau ada field jumlahProduk
                    }
                }
            }

            transaksiLiveData.setValue(list);
            totalPendapatan.setValue(totalPendapatanVal);
            jumlahTransaksi.setValue(jumlahTransaksiVal);
            produkTerjual.setValue(produkTerjualVal);
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
