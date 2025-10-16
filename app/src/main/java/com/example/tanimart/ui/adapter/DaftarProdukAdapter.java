package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tanimart.R;
import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.data.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Base64;
import android.util.Log;


public class DaftarProdukAdapter extends RecyclerView.Adapter<DaftarProdukAdapter.ViewHolder> implements Filterable {

    private List<Product> daftarProdukList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public DaftarProdukAdapter(List<Product> daftarProdukList,
                               OnItemClickListener listener) {
        this.daftarProdukList = daftarProdukList;
        this.listener = listener;
    }

    public void setDaftarProdukList(List<Product> list) {
        this.daftarProdukList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daftar_produk, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product item = daftarProdukList.get(position);


        // Ambil semua nilai yang dibutuhkan dari objek 'item'
        double hargaAwal = item.getHargaJual();
        double diskonPersen = item.getDiskonPersen();
        double diskonNominal = item.getDiskonNominal();
        double hargaAkhir = hargaAwal;

        //Terapkan logika diskon (jika ada)
        if (diskonPersen > 0) {
            hargaAkhir = hargaAwal - (hargaAwal * diskonPersen / 100);
        } else if (diskonNominal > 0) {
            hargaAkhir = hargaAwal - diskonNominal;
        }

        // Pastikan harga tidak menjadi minus
        if (hargaAkhir < 0) {
            hargaAkhir = 0;
        }

        // Set text dengan nilai yang sudah dihitung
        holder.nama.setText(item.getNamaProduk());
        // Gunakan 'hargaAkhir' yang sudah dihitung, bukan harga asli lagi
        holder.harga.setText(formatRupiah(hargaAkhir));
        holder.merek.setText(item.getMerek());
        holder.stok.setText("Stok: " + item.getStok());


        // Logika untuk memuat gambar (tidak diubah, sudah bagus)
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.upload)
                        .error(R.drawable.upload)
                        .into(holder.image);
            } else {
                try {
                    byte[] imageBytes = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                    Glide.with(holder.itemView.getContext())
                            .asBitmap()
                            .load(imageBytes)
                            .placeholder(R.drawable.upload)
                            .error(R.drawable.upload)
                            .into(holder.image);
                } catch (IllegalArgumentException e) {
                    android.util.Log.e("DaftarProdukAdapter", "Gagal decode Base64: " + e.getMessage());
                    holder.image.setImageResource(R.drawable.upload);
                }
            }
        } else {
            holder.image.setImageResource(R.drawable.upload);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    // Fungsi updateData Anda sudah benar, tidak perlu diubah.
    public void updateData(List<Product> newProductList) {
        this.daftarProdukList.clear();
        this.daftarProdukList.addAll(newProductList);
        notifyDataSetChanged();
    }

    // Fungsi filter Anda juga sudah benar, tidak perlu diubah.
    @Override
    public Filter getFilter() {
        // ... (kode filter tidak berubah)
        return filter;
    }

    private final Filter filter = new Filter() {
        // ... (implementasi filter tidak berubah)
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(daftarProdukList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product item : daftarProdukList) {
                    if (item.getNamaProduk().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;

        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            daftarProdukList.clear();
            daftarProdukList.addAll((List<Product>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return daftarProdukList != null ? daftarProdukList.size() : 0;
    }

    // Fungsi formatRupiah Anda sudah benar, tidak perlu diubah.
    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);
        return formatRupiah.format(number).replace("Rp", "Rp ");
    }

    // ViewHolder Anda sudah benar, tidak perlu diubah.
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nama, harga, stok, merek;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivProduk);
            nama = itemView.findViewById(R.id.tvNamaProduk);
            merek = itemView.findViewById(R.id.txtDetailProdukMasuk); // Pastikan ID ini benar untuk Merek
            harga = itemView.findViewById(R.id.tvHarga);
            stok = itemView.findViewById(R.id.tvStok);
        }
    }
}
