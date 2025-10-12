package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class DialogTransaksiActivity extends DialogFragment {

    // 🔹 Interface listener supaya Activity tahu kalau user menekan “Sudah Terpotong”
    public interface OnKonfirmasiListener {
        void onKonfirmasi();
    }

    private OnKonfirmasiListener listener;

    // 🔹 Setter untuk dipanggil dari Activity (misalnya QRIS / Transfer)
    public void setOnKonfirmasiListener(OnKonfirmasiListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi Pembayaran")
                .setMessage("Apakah saldo sudah terpotong?")
                .setPositiveButton("Sudah Terpotong", (dialog, which) -> {
                    if (listener != null) listener.onKonfirmasi(); // ✅ callback ke Activity
                    dismiss();
                })
                .setNegativeButton("Belum Terpotong", (dialog, which) -> {
                    // 🔹 Jika user belum bayar, tampilkan dialog konfirmasi keluar
                    showLeaveConfirmDialog();
                });

        return builder.create();
    }

    private void showLeaveConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Kamu yakin ingin meninggalkan halaman?");
        builder.setMessage("Kamu akan membatalkan pembayaran.");
        builder.setCancelable(false);
        builder.setNegativeButton("Kembali", (dialog, which) -> {
            dialog.dismiss(); // tetap di halaman
            dismiss(); // tutup dialog utama
        });
        builder.setPositiveButton("Tinggalkan", (dialog, which) -> {
            dialog.dismiss();
            requireActivity().finish(); // keluar dari halaman pembayaran
        });
        builder.show();
    }
}
