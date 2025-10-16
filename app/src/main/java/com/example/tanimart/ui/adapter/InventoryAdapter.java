package com.example.tanimart.ui.adapter;

import android.util.Base64;
import android.util.Log;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> implements Filterable {
    private List<Inventory> inventoryList;
    private List<Inventory> inventoryListFull; // salinan data asli
    private final OnItemClickListener listener;
    private final OnDeleteClickListener deleteClickListener;

    // Listener untuk klik item
    public interface OnItemClickListener {
        void onItemClick(Inventory inventory);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Inventory inventory);
    }

    public InventoryAdapter(List<Inventory> inventoryList,
                            OnItemClickListener listener,
                            OnDeleteClickListener deleteClickListener) {
        this.inventoryList = inventoryList;
        this.listener = listener;
        this.deleteClickListener = deleteClickListener;
        this.inventoryListFull = new ArrayList<>(inventoryList); // copy data
    }

    public void setInventoryList(List<Inventory> list) {
        this.inventoryList.clear();
        this.inventoryListFull.clear();
        this.inventoryList.addAll(list);
        this.inventoryListFull.addAll(list);

        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inventory item = inventoryList.get(position);

        // set text
        holder.nama.setText(item.getNamaProduk());
        holder.harga.setText(formatRupiah(item.getHargaJual()));
        holder.kategori.setText(item.getKategori());
        holder.merek.setText(item.getMerek());

        // memuat gambar: bisa dari URL (http) atau dari string Base64
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Cek apakah ini Base64 (tidak dimulai dengan http) atau URL biasa
            if (imageUrl.startsWith("http")) {
                // Jika ini URL, muat seperti biasa (untuk data lama jika ada)
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.upload)
                        .error(R.drawable.upload)
                        .into(holder.image);
            } else {
                // Jika ini BUKAN URL, kita anggap ini adalah Base64
                try {
                    byte[] imageBytes = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                    Glide.with(holder.itemView.getContext())
                            .asBitmap()
                            .load(imageBytes)
                            .placeholder(R.drawable.upload)
                            .error(R.drawable.upload)
                            .into(holder.image);
                } catch (IllegalArgumentException e) {
                    // Ini terjadi jika string bukan Base64 yang valid
                    android.util.Log.e("InventoryAdapter", "Gagal decode Base64: " + e.getMessage());
                    holder.image.setImageResource(R.drawable.upload); // Tampilkan gambar error
                }
            }
        } else {
            // Handle jika tidak ada gambar sama sekali
            holder.image.setImageResource(R.drawable.upload); // default jika url kosong
        }

        // event klik item biasa
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        // event klik tombol delete
        holder.btnDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Inventory> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(inventoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Inventory item : inventoryListFull) {
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
            inventoryList.clear();
            inventoryList.addAll((List<Inventory>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        formatRupiah.setMinimumFractionDigits(0);
        return formatRupiah.format(number).replace("Rp", "Rp ");
    }

    // ====== ViewHolder ======
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnDelete;
        TextView nama, harga, kategori, merek;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            nama = itemView.findViewById(R.id.itemNama);
            harga = itemView.findViewById(R.id.itemHarga);
            kategori = itemView.findViewById(R.id.itemKategori);
            merek = itemView.findViewById(R.id.itemMerek);
            btnDelete = itemView.findViewById(R.id.itemDelete);
        }
    }
}
