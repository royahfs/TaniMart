package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tanimart.R;
import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.utils.CurrencyHelper;
import java.util.List;

public class CartSavedAdapter extends RecyclerView.Adapter<CartSavedAdapter.ViewHolder> {

    public interface OnCartClickListener {
        void onCartClick(CartItem cartItem);
    }

    private List<CartItem> cartList;
    private final OnCartClickListener listener;

    public CartSavedAdapter(List<CartItem> cartList, OnCartClickListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }

    public void setCartList(List<CartItem> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartSavedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_saved, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartSavedAdapter.ViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.tvNamaTransaksi.setText(item.getProduct().getNamaProduk());
        holder.tvHargaTransaksi.setText(CurrencyHelper.formatRupiah(item.getSubtotal()));
        holder.itemView.setOnClickListener(v -> listener.onCartClick(item));
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaTransaksi, tvHargaTransaksi;
        ImageView btnHapus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaTransaksi = itemView.findViewById(R.id.tvNamaTransaksi);
            tvHargaTransaksi = itemView.findViewById(R.id.tvHargaTransaksi);
            btnHapus = itemView.findViewById(R.id.btnHapus);
        }
    }
}
