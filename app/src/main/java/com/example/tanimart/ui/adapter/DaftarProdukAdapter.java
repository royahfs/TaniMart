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

import java.util.ArrayList;
import java.util.List;

public class DaftarProdukAdapter extends RecyclerView.Adapter<DaftarProdukAdapter.ViewHolder> implements Filterable {

    private List<Product> daftarProdukList;
//    private List<Product> daftarProdukListFull;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public DaftarProdukAdapter(List<Product> daftarProdukList,
                               OnItemClickListener listener) {
        this.daftarProdukList = daftarProdukList;
        this.listener = listener;
//        this.daftarProdukListFull = new ArrayList<>(daftarProdukList);
    }

    public void setDaftarProdukList(List<Product> list) {
        this.daftarProdukList = list;
//        this.daftarProdukListFull = new ArrayList<>(list);
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

        // set text
        holder.nama.setText(item.getNamaProduk());
        holder.harga.setText("Rp " + item.getHargaJual());
        holder.merek.setText(item.getMerek());
        holder.stok.setText("Stok: " + item.getStok());

        //load gambar dengan Glide
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.upload)
                    .error(R.drawable.upload)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.upload); // default jika url kosong
        }

        // event klik item biasa
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
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

    // ViewHolder

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nama, harga, stok, merek;
        // ini belum selesai cocok kan lagi dengan activity_daftar_produk.xml

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivProduk);
            nama = itemView.findViewById(R.id.tvNamaProduk);
            merek = itemView.findViewById(R.id.txtDetailProdukMasuk);
            harga = itemView.findViewById(R.id.tvHarga);
            stok = itemView.findViewById(R.id.tvStok);

        }
    }
}
