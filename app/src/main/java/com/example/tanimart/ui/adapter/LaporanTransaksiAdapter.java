package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Transaksi;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LaporanTransaksiAdapter extends RecyclerView.Adapter<LaporanTransaksiAdapter.ViewHolder> {

    private final List<Transaksi> transaksiList;

    // Constructor kosong
    public LaporanTransaksiAdapter() {
        this.transaksiList = new ArrayList<>();
    }

    public LaporanTransaksiAdapter(List<Transaksi> initialList) {
        this.transaksiList = initialList != null ? new ArrayList<>(initialList) : new ArrayList<>();
    }

    // Untuk update dari observer
    public void updateData(List<Transaksi> newList) {
        transaksiList.clear();
        if (newList != null) transaksiList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_laporan_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi t = transaksiList.get(position);

        // Format tanggal
        Timestamp ts = t.getTanggal();
        if (ts != null) {
            Date date = ts.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            holder.txtTanggal.setText(sdf.format(date));
        } else {
            holder.txtTanggal.setText("-");
        }

        holder.txtMetode.setText(t.getMetode() != null ? t.getMetode() : "-");

        // Format rupiah sederhana
        holder.txtTotal.setText("Rp " + String.format(Locale.getDefault(), "%,.0f", t.getTotal()));
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggal, txtMetode, txtTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtMetode = itemView.findViewById(R.id.txtMetode);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}
