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
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.kasir.transaksi.TransaksiViewModel;
import com.example.tanimart.utils.CurrencyHelper;

import java.util.List;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private List<CartItem> cartList;
    private List<Product> productList;
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
        Product itemProduct = item.getProduct();

        double hargaAwal = itemProduct.getHargaJual();
        double diskonPersen = itemProduct.getDiskonPersen();
        double diskonNominal = itemProduct.getDiskonNominal();
        double hargaAkhir = hargaAwal;

        if (diskonPersen > 0) {
            hargaAkhir = hargaAwal - (hargaAwal * diskonPersen / 100);
        } else if (diskonNominal > 0) {
            hargaAkhir = hargaAwal - diskonNominal;
        }
        if (hargaAkhir < 0) hargaAkhir = 0;

        holder.title.setText(item.getProduct().getNamaProduk());

        // Format harga jadi Rupiah
        holder.feeEachItem.setText(CurrencyHelper.formatRupiah(hargaAkhir));
//        holder.feeEachItem.setText(CurrencyHelper.formatRupiah(item.getProduct().getHargaJual()));

        holder.numberItemTxt.setText(String.valueOf(item.getQuantity()));

        holder.trashBtn.setOnClickListener(v -> viewModel.hapusDariCart(item.getProduct()));
        holder.plusItem.setOnClickListener(v -> viewModel.tambahKeCart(item.getProduct()));
        holder.minusItem.setOnClickListener(v -> viewModel.kurangiDariCart(item.getProduct()));
    }

    @Override
    public int getItemCount() {
        return cartList != null ? cartList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem, numberItemTxt;
        ImageView trashBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            numberItemTxt = itemView.findViewById(R.id.numberItemTxt);
            trashBtn = itemView.findViewById(R.id.imgTrashBtn);
        }
    }
}
