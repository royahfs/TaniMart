package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;

import java.util.List;

public class TagihanAdapter extends RecyclerView.Adapter<TagihanAdapter.ViewHolder> {

    private List<Product> tagihanList;
    private final OnRemoveClickListener removeClickListener;

    public interface OnRemoveClickListener {
        void onRemoveClick(Product product);
    }

    public TagihanAdapter(List<Product> tagihanList, OnRemoveClickListener listener) {
        this.tagihanList = tagihanList;
        this.removeClickListener = listener;
    }

    public void setTagihanList(List<Product> list) {
        this.tagihanList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TagihanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tagihan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagihanAdapter.ViewHolder holder, int position) {
        Product item = tagihanList.get(position);
        holder.nama.setText(item.getNamaProduk());
        holder.harga.setText("Rp " + String.valueOf((long)item.getHargaJual()));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.upload)
                    .error(R.drawable.upload)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.upload);
        }

        holder.btnRemove.setOnClickListener(v -> removeClickListener.onRemoveClick(item));
    }

    @Override
    public int getItemCount() {
        return tagihanList != null ? tagihanList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nama, harga;
        ImageView image, btnRemove;

        public ViewHolder(View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.itemNama);
            harga = itemView.findViewById(R.id.itemHarga);
            image = itemView.findViewById(R.id.itemImage);
            btnRemove = itemView.findViewById(R.id.itemRemove);
        }
    }
}
