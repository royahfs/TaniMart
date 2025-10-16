package com.example.tanimart.ui.kasir.laporan;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.Transaksi;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LaporanViewModel extends ViewModel {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration transaksiListener;

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

    /**
     * Filter data transaksi berdasarkan jenis waktu (Hari Ini, Minggu Ini, Bulan Ini, Custom)
     */
    public void filterData(String filterType, @Nullable Date startDate, @Nullable Date endDate) {
        if (transaksiListener != null) {
            transaksiListener.remove();
        }

        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        Date awal, akhir;

        switch (filterType) {
            case "Minggu Ini":
                // Set ke awal minggu (misal: Minggu atau Senin tergantung locale)
                calStart.setTime(new Date());
                calStart.set(Calendar.DAY_OF_WEEK, calStart.getFirstDayOfWeek());
                setWaktuKeAwalHari(calStart);
                awal = calStart.getTime();

                // 7 hari ke depan dari awal minggu
                calEnd.setTime(awal);
                calEnd.add(Calendar.DAY_OF_YEAR, 6);
                setWaktuKeAkhirHari(calEnd);
                akhir = calEnd.getTime();
                break;

            case "Bulan Ini":
                calStart.setTime(new Date());
                calStart.set(Calendar.DAY_OF_MONTH, 1);
                setWaktuKeAwalHari(calStart);
                awal = calStart.getTime();

                calEnd.setTime(calStart.getTime());
                calEnd.set(Calendar.DAY_OF_MONTH, calEnd.getActualMaximum(Calendar.DAY_OF_MONTH));
                setWaktuKeAkhirHari(calEnd);
                akhir = calEnd.getTime();
                break;

            case "Custom":
                if (startDate != null && endDate != null) {
                    calStart.setTime(startDate);
                    setWaktuKeAwalHari(calStart);
                    awal = calStart.getTime();

                    calEnd.setTime(endDate);
                    setWaktuKeAkhirHari(calEnd);
                    akhir = calEnd.getTime();
                } else {
                    return;
                }
                break;

            case "Hari Ini":
            default:
                setWaktuKeAwalHari(calStart);
                awal = calStart.getTime();

                setWaktuKeAkhirHari(calEnd);
                akhir = calEnd.getTime();
                break;
        }

        // 🔥 Query Firestore berdasarkan tanggal transaksi
        Query query = db.collection("transaksi")
                .whereGreaterThanOrEqualTo("tanggal", awal)
                .whereLessThanOrEqualTo("tanggal", akhir) // inklusif agar tanggal akhir ikut
                .orderBy("tanggal", Query.Direction.DESCENDING);

        transaksiListener = query.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
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
                _transaksiList.postValue(list);
                _totalPendapatan.postValue(total);
                _jumlahTransaksi.postValue(list.size());
            }
        });
    }

    private void setWaktuKeAwalHari(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private void setWaktuKeAkhirHari(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (transaksiListener != null) {
            transaksiListener.remove();
        }
    }
}
