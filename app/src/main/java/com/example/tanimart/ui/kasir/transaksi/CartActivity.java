package com.example.tanimart.ui.kasir.transaksi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.CartItem;
import com.example.tanimart.ui.adapter.CartSavedAdapter;
import com.example.tanimart.utils.CurrencyHelper;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private TransaksiViewModel transaksiViewModel;
    private CartSavedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ImageView btnBack = findViewById(R.id.btnBack);
        EditText searchCart = findViewById(R.id.searchCart);
        RecyclerView recyclerCartList = findViewById(R.id.recyclerCartList);

        transaksiViewModel = new ViewModelProvider(this).get(TransaksiViewModel.class);
        adapter = new CartSavedAdapter(new ArrayList<>(), cartItem -> {
            // ketika item ditekan -> kembali ke bottom sheet transaksi
            Intent resultIntent = new Intent();
            resultIntent.putExtra("SELECTED_CART_ID", cartItem.getProduct().getId());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        recyclerCartList.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartList.setAdapter(adapter);

        transaksiViewModel.getCartList().observe(this, cartItems -> adapter.setCartList(cartItems));

        btnBack.setOnClickListener(v -> finish());
    }
}
