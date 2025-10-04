package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.CartItem;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.tanimart.data.model.Product;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class PembayaranTunaiViewModel extends ViewModel {

    private final MutableLiveData<Double> kembalian = new MutableLiveData<>(0.0);

    public LiveData<Double> getKembalian() {
        return kembalian;
    }

    public void hitungKembalian(double uangDiterima, double totalTagihan) {
        kembalian.setValue(uangDiterima - totalTagihan);
    }

    // Simpan transaksi ke Firestore
    public void prosesPembayaran(double uangDiterima, double total, String metode, Runnable onSuccess) {
        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("total", total);
        transaksi.put("uangDiterima", uangDiterima);
        transaksi.put("kembalian", uangDiterima - total);
        transaksi.put("metode", metode);
        transaksi.put("tanggal", new Date());

        FirebaseFirestore.getInstance()
                .collection("transaksi")
                .add(transaksi)
                .addOnSuccessListener(doc -> {
                    Log.d("PembayaranTunaiVM", "Transaksi berhasil disimpan");
                    onSuccess.run();
                })
                .addOnFailureListener(e -> Log.e("PembayaranTunaiVM", "Gagal simpan transaksi", e));
    }
}
