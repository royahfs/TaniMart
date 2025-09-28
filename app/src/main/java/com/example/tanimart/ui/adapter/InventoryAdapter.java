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

import java.util.ArrayList;
import java.util.List;

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
        this.inventoryList = list;
        this.inventoryListFull = new ArrayList<>(list);
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
        holder.harga.setText("Rp " + item.getHargaJual());
        holder.kategori.setText(item.getKategori());
        holder.merek.setText(item.getMerek());

        // load gambar dengan Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.upload) // gambar default saat loading
                    .error(R.drawable.upload)       // gambar default kalau gagal load
                    .into(holder.image);
        } else {
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
