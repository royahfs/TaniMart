package com.example.tanimart.ui.laporan;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.ui.adapter.LaporanTransaksiAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LaporanActivity extends AppCompatActivity {

    private LaporanViewModel viewModel;
    private LaporanTransaksiAdapter adapter;
    private Date startDate, endDate;
    private TextView txtRingkasan, btnStartDate, btnEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        RecyclerView recyclerView = findViewById(R.id.recyclerTransaksi);
        txtRingkasan = findViewById(R.id.txtRingkasan);
        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);
        Spinner metodeSpinner = findViewById(R.id.spinnerMetode);

        adapter = new LaporanTransaksiAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LaporanViewModel.class);

        viewModel.getTransaksiList().observe(this, list -> adapter.setData(list));
        viewModel.getRingkasan().observe(this, txtRingkasan::setText);

        // Spinner metode pembayaran
        String[] metodeArray = {"Semua", "Tunai", "QRIS", "Transfer"};
        metodeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, metodeArray));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());

        btnStartDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                c.set(year, month, dayOfMonth, 0, 0, 0);
                startDate = c.getTime();
                btnStartDate.setText(sdf.format(startDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnEndDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                c.set(year, month, dayOfMonth, 23, 59, 59);
                endDate = c.getTime();
                btnEndDate.setText(sdf.format(endDate));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // tombol filter (misalnya TextView dengan id btnFilter)
        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            String metode = metodeSpinner.getSelectedItem().toString();
            if (startDate != null && endDate != null) {
                viewModel.loadTransaksi(startDate, endDate, metode);
            }
        });
    }
}
