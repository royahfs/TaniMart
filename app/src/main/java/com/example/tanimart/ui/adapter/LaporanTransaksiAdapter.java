package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Transaksi;
import com.example.tanimart.utils.CurrencyHelper;

import java.util.List;

public class LaporanTransaksiAdapter extends RecyclerView.Adapter<LaporanTransaksiAdapter.ViewHolder> {

    private List<Transaksi> transaksiList;

    public void setData(List<Transaksi> newList) {
        this.transaksiList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi t = transaksiList.get(position);
        holder.txtTanggal.setText(t.getTanggal());
        holder.txtMetode.setText(t.getMetode());
        holder.txtTotal.setText(CurrencyHelper.formatRupiah(t.getTotalHarga()));
    }

    @Override
    public int getItemCount() {
        return transaksiList != null ? transaksiList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTanggal, txtMetode, txtTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTanggal = itemView.findViewById(R.id.txtTanggal);
            txtMetode = itemView.findViewById(R.id.txtMetode);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}
