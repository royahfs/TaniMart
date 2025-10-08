package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;
import com.example.tanimart.utils.CurrencyHelper;
import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ProductViewHolder> {

    private final List<Product> productList;
    public CartAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product currentProduct = productList.get(position);
        holder.tvNamaProduk.setText(currentProduct.getNamaProduk());
        holder.tvHargaProduk.setText(CurrencyHelper.formatRupiah(currentProduct.getHargaJual()));
        // Tambahkan 'x' untuk kuantitas
        holder.tvKuantitasProduk.setText(currentProduct.getQuantity() + "x");

        String imageUrl = currentProduct.getImageUrl();

        if (imageUrl != null && !imageUrl.isEmpty()) {

            // Cek apakah ini Base64 (tidak dimulai dengan http) atau URL biasa
            if (imageUrl.startsWith("http")) {
                // Jika ini URL, muat seperti biasa
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.upload)
                        .error(R.drawable.upload)
                        .into(holder.image);
            } else {
                // Jika ini BUKAN URL, kita anggap ini adalah Base64
                try {
                    // PENTING: Gunakan android.util.Base64
                    byte[] imageBytes = android.util.Base64.decode(imageUrl, android.util.Base64.DEFAULT);
                    Glide.with(holder.itemView.getContext())
                            .asBitmap()
                            .load(imageBytes)
                            .placeholder(R.drawable.upload)
                            .error(R.drawable.upload)
                            .into(holder.image);
                } catch (IllegalArgumentException e) {
                    // Ini terjadi jika string bukan Base64 yang valid
                    // PENTING: Gunakan android.util.Log
                    android.util.Log.e("DaftarProdukAdapter", "Gagal decode Base64: " + e.getMessage());
                    holder.image.setImageResource(R.drawable.upload); // Tampilkan gambar error
                }
            }
        } else {
            // Handle jika tidak ada gambar sama sekali
            holder.image.setImageResource(R.drawable.upload); // default jika url kosong
        }

    }

    @Override
    public int getItemCount() {
        // Pastikan null check untuk menghindari error
        return productList != null ? productList.size() : 0;
    }

    // 7. UBAH DAN LENGKAPI VIEWHOLDER
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tvNamaProduk;
        TextView tvHargaProduk;
        TextView tvKuantitasProduk;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageProduct);
            tvNamaProduk = itemView.findViewById(R.id.textProductName);
            tvHargaProduk = itemView.findViewById(R.id.textProductPrice);
            tvKuantitasProduk = itemView.findViewById(R.id.textProductQuantity);
        }
    }
}
