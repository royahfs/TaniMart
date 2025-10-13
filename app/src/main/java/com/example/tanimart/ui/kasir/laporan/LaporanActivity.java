package com.example.tanimart.ui.kasir.laporan;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.ui.adapter.LaporanTransaksiAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.ArrayList;
import java.util.Date;

public class LaporanActivity extends AppCompatActivity {
    private LaporanViewModel viewModel;
    private LaporanTransaksiAdapter adapter;
    private Button btnCustomDate;
    private Spinner spinnerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        viewModel = new ViewModelProvider(this).get(LaporanViewModel.class);
        adapter = new LaporanTransaksiAdapter(new ArrayList<>());

        RecyclerView recycler = findViewById(R.id.recyclerLaporan);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnCustomDate = findViewById(R.id.btnCustomDate);

        // Observers
        viewModel.getTransaksiList().observe(this, list -> adapter.updateData(list));
        viewModel.getTotalPendapatan().observe(this, val ->
                ((TextView) findViewById(R.id.txtTotalPendapatan)).setText("Rp " + val));
        viewModel.getJumlahTransaksi().observe(this, val ->
                ((TextView) findViewById(R.id.txtJumlahTransaksi)).setText(String.valueOf(val)));

        // Spinner event
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                if (filter.equals("Custom")) {
                    btnCustomDate.setVisibility(View.VISIBLE);
                } else {
                    btnCustomDate.setVisibility(View.GONE);
                    viewModel.filterData(filter, null, null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Custom date range picker
        btnCustomDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Pilih Rentang Tanggal");
            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
            picker.show(getSupportFragmentManager(), picker.toString());

            picker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>) selection -> {
                Date start = new Date(selection.first);
                Date end = new Date(selection.second);
                viewModel.filterData("Custom", start, end);
            });
        });
    }
}
