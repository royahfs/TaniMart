package com.example.tanimart.ui.common.inventory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.ui.adapter.ProdukKeluarAdapter;

public class ProdukKeluarActivity extends AppCompatActivity {

    private ProdukKeluarViewModel viewModel;
    private ProdukKeluarAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_keluar);

        RecyclerView recycler = findViewById(R.id.recyclerProdukKeluar);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProdukKeluarAdapter();
        recycler.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProdukKeluarViewModel.class);
        viewModel.getProdukKeluarList().observe(this, list -> adapter.updateData(list));
    }
}
