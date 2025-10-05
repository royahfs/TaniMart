package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.ProdukKeluar;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProdukKeluarAdapter extends RecyclerView.Adapter<ProdukKeluarAdapter.ViewHolder> {

    private final List<ProdukKeluar> produkKeluarList = new ArrayList<>();

    public void updateData(List<ProdukKeluar> newList) {
        produkKeluarList.clear();
        if (newList != null) produkKeluarList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_produk_keluar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProdukKeluar item = produkKeluarList.get(position);
        holder.txtNamaProduk.setText(item.getNamaProduk());
        holder.txtJumlah.setText("Jumlah: " + item.getJumlahKeluar());

        Timestamp ts = item.getTanggalKeluar();
        if (ts != null) {
            Date date = ts.toDate();
            String formatted = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(date);
            holder.txtTanggal.setText(formatted);
        } else {
            holder.txtTanggal.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return produkKeluarList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNamaProduk, txtJumlah, txtTanggal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNamaProduk = itemView.findViewById(R.id.txtNamaProduk);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
        }
    }
}
