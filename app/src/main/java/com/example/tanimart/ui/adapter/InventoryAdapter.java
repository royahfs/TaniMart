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
    private final List<Inventory> inventoryList; // List yang ditampilkan saat ini
    private List<Inventory> inventoryListFull; // Salinan data asli untuk filtering
    private final OnItemClickListener listener;
    private final OnDeleteClickListener deleteClickListener;
    private CharSequence latestFilterConstraint = ""; // Menyimpan keyword pencarian terakhir

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
        // Penting: Inisialisasi list di sini agar tidak pernah null
        this.inventoryList = new ArrayList<>();
        this.inventoryListFull = new ArrayList<>();
        this.listener = listener;
        this.deleteClickListener = deleteClickListener;
        // Panggil metode setInventoryList untuk pengisian data awal yang konsisten
        setInventoryList(inventoryList);
    }

    /**
     * Metode ini sekarang menjadi satu-satunya sumber kebenaran untuk memperbarui data adapter.
     * Ia akan memperbarui data master (inventoryListFull) dan secara otomatis memicu ulang
     * filter yang sedang aktif untuk memastikan tampilan selalu sinkron.
     */
    public void setInventoryList(List<Inventory> newList) {
        // 1. Ganti data master yang digunakan untuk filtering dengan data baru.
        this.inventoryListFull = new ArrayList<>(newList);

        // 2. Panggil ulang filter dengan keyword pencarian terakhir.
        //    - Jika tidak ada pencarian (kosong), filter akan menampilkan semua data baru.
        //    - Jika ada pencarian (misal "apel"), filter akan mencari "apel" di dalam data baru.
        //    Ini memastikan tampilan selalu konsisten.
        getFilter().filter(latestFilterConstraint);
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

    // Ubah Filter untuk menyimpan keyword terakhir
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // Simpan keyword pencarian untuk digunakan lagi nanti oleh setInventoryList
                latestFilterConstraint = constraint;

                List<Inventory> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    // Jika tidak ada filter, tampilkan semua data dari master list
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
                // Pastikan results.values tidak null sebelum di-cast
                if (results.values instanceof List) {
                    // Supress warning karena kita sudah cek tipenya
                    @SuppressWarnings("unchecked")
                    List<Inventory> newValues = (List<Inventory>) results.values;
                    inventoryList.addAll(newValues);
                }
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        // Cek null untuk keamanan tambahan
        return inventoryList != null ? inventoryList.size() : 0;
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
