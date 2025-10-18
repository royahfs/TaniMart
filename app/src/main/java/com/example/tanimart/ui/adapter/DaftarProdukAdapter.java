package com.example.tanimart.ui.adapter;

import android.annotation.SuppressLint;
import android.graphics.Paint;
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
import com.example.tanimart.data.model.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Adapter universal yang sudah disederhanakan
public class DaftarProdukAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final int TIPE_DAFTAR_PRODUK = 1;
    public static final int TIPE_KELOLA_PRODUK = 2;
    public static final int TIPE_TRANSAKSI = 3;

    private List<Product> productList;
    private List<Product> productListFull;
    private final OnItemClickListener listener;
    private final int viewType;

    // PERUBAHAN 1: Sederhanakan interface, hanya butuh satu jenis klik
    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    // Constructor tidak perlu diubah
    public DaftarProdukAdapter(List<Product> productList, OnItemClickListener listener, int viewType) {
        this.productList = productList;
        this.productListFull = new ArrayList<>(productList);
        this.listener = listener;
        this.viewType = viewType;
    }

    // Metode untuk mengupdate data dari ViewModel
    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<Product> list) {
        this.productList = list;
        this.productListFull = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return this.viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TIPE_KELOLA_PRODUK:
                view = inflater.inflate(R.layout.item_kelola_produk_masuk, parent, false);
                return new KelolaProdukViewHolder(view);
            case TIPE_TRANSAKSI:
                view = inflater.inflate(R.layout.item_product, parent, false);
                return new TransaksiViewHolder(view);
            case TIPE_DAFTAR_PRODUK:
            default:
                view = inflater.inflate(R.layout.item_daftar_produk, parent, false);
                return new DaftarProdukViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product item = productList.get(position);

        double hargaAwal = item.getHargaJual();
        double diskonPersen = item.getDiskonPersen();
        double diskonNominal = item.getDiskonNominal();
        double hargaAkhir = hargaAwal;

        if (diskonPersen > 0) {
            hargaAkhir = hargaAwal - (hargaAwal * diskonPersen / 100);
        } else if (diskonNominal > 0) {
            hargaAkhir = hargaAwal - diskonNominal;
        }
        if (hargaAkhir < 0) hargaAkhir = 0;

        switch (holder.getItemViewType()) {
            case TIPE_KELOLA_PRODUK:
                KelolaProdukViewHolder kelolaHolder = (KelolaProdukViewHolder) holder;
                kelolaHolder.nama.setText(item.getNamaProduk());
                kelolaHolder.merek.setText(item.getMerek());
                kelolaHolder.harga.setText(formatRupiah(hargaAkhir));
                kelolaHolder.kategori.setText(item.getKategori());
                loadImage(item.getImageUrl(), kelolaHolder.image);
                kelolaHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
                break;

            case TIPE_TRANSAKSI:
                TransaksiViewHolder trxHolder = (TransaksiViewHolder) holder;
                trxHolder.nama.setText(item.getNamaProduk());
                trxHolder.harga.setText(formatRupiah(hargaAkhir));
                loadImage(item.getImageUrl(), trxHolder.image);
                trxHolder.kategori.setText(item.getKategori());
                trxHolder.merek.setText(item.getMerek());
                trxHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
                break;

            case TIPE_DAFTAR_PRODUK:
            default:
                DaftarProdukViewHolder daftarHolder = (DaftarProdukViewHolder) holder;
                daftarHolder.nama.setText(item.getNamaProduk());
                daftarHolder.harga.setText(formatRupiah(hargaAkhir));
                daftarHolder.merek.setText(item.getMerek());
                daftarHolder.stok.setText("Stok: " + item.getStok());
                loadImage(item.getImageUrl(), daftarHolder.image);
                daftarHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    // --- Helper Methods (loadImage, formatRupiah, Filter) tidak berubah ---
    private void loadImage(String imageUrl, ImageView imageView) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            if (imageUrl.startsWith("http")) {
                Glide.with(imageView.getContext()).load(imageUrl).placeholder(R.drawable.upload).error(R.drawable.upload).into(imageView);
            } else {
                try {
                    byte[] imageBytes = Base64.decode(imageUrl, Base64.DEFAULT);
                    Glide.with(imageView.getContext()).asBitmap().load(imageBytes).placeholder(R.drawable.upload).error(R.drawable.upload).into(imageView);
                } catch (IllegalArgumentException e) {
                    Log.e("DaftarProdukAdapter", "Gagal decode Base64: " + e.getMessage());
                    imageView.setImageResource(R.drawable.upload);
                }
            }
        } else {
            imageView.setImageResource(R.drawable.upload);
        }
    }
    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        formatRupiah.setMaximumFractionDigits(0);
        return formatRupiah.format(number).replace("Rp", "Rp ");
    }
    @Override
    public Filter getFilter() { return filter; }
    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(productListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Product item : productListFull) {
                    if (item.getNamaProduk().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                productList.clear();
                productList.addAll((List<Product>) results.values);
                notifyDataSetChanged();
            }
        }
    };
    // --- End of Helper Methods ---


    // --- Definisi ViewHolder ---
    public static class DaftarProdukViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nama, harga, stok, merek;
        public DaftarProdukViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ivProduk);
            nama = itemView.findViewById(R.id.tvNamaProduk);
            merek = itemView.findViewById(R.id.txtDetailProdukMasuk);
            harga = itemView.findViewById(R.id.tvHarga);
            stok = itemView.findViewById(R.id.tvStok);
        }
    }

    public static class KelolaProdukViewHolder extends RecyclerView.ViewHolder {
        TextView nama, merek, kategori, harga;
        ImageView image, imgDelete;
        public KelolaProdukViewHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.itemNamaKPM);
            merek = itemView.findViewById(R.id.itemMerek);
            image = itemView.findViewById(R.id.itemImage);
            harga = itemView.findViewById(R.id.itemHarga);
            kategori = itemView.findViewById(R.id.itemKategori);
            imgDelete = itemView.findViewById(R.id.itemDelete);
        }
    }

    public static class TransaksiViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nama, harga, kategori, merek;
        public TransaksiViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            nama = itemView.findViewById(R.id.itemNama);
            harga = itemView.findViewById(R.id.itemHarga);
            kategori = itemView.findViewById(R.id.itemKategori);
            merek = itemView.findViewById(R.id.itemMerek);
        }
    }
}
