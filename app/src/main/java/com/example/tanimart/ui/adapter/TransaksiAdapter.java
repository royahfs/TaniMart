package com.example.tanimart.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tanimart.R;
import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.ui.kasir.transaksi.TransaksiViewModel;
import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<CartItem> cartList;
    private final TransaksiViewModel viewModel;

    public TransaksiAdapter(List<CartItem> cartList, TransaksiViewModel viewModel) {
        this.cartList = cartList;
        this.viewModel = viewModel;
    }

    public void setCartList(List<CartItem> newList) {
        this.cartList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransaksiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaksiAdapter.ViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        holder.title.setText(item.getProduct().getNamaProduk());
        holder.feeEachItem.setText("Rp" + item.getProduct().getHargaJual());
        holder.numberItemTxt.setText(String.valueOf(item.getQuantity()));

        holder.plusItem.setOnClickListener(v -> viewModel.tambahKeCart(item.getProduct()));
        holder.minusItem.setOnClickListener(v -> viewModel.kurangiDariCart(item.getProduct()));
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem, numberItemTxt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
        }
    }
}
