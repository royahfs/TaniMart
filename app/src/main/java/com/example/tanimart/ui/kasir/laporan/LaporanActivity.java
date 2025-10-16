package com.example.tanimart.ui.kasir.laporan;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Transaksi;
import com.example.tanimart.ui.adapter.LaporanTransaksiAdapter;
import com.example.tanimart.ui.adapter.PdfDocumentAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import androidx.core.util.Pair;
import com.google.android.material.datepicker.MaterialDatePicker;

public class LaporanActivity extends AppCompatActivity {

    private TextView tvHeaderTitle, txtTotalPendapatan, txtJumlahTransaksi;
    private Spinner spinnerFilter;
    private Button btnCustomDate;
    private RecyclerView recyclerLaporan;
    private ImageButton btnExportPdf;

    private LaporanViewModel laporanViewModel;
    private LaporanTransaksiAdapter laporanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan);

        // Inisialisasi Views (pastikan ID sama dengan activity_laporan.xml)
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        txtTotalPendapatan = findViewById(R.id.txtTotalPendapatan);
        txtJumlahTransaksi = findViewById(R.id.txtJumlahTransaksi);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        btnCustomDate = findViewById(R.id.btnCustomDate);
        recyclerLaporan = findViewById(R.id.recyclerLaporan);
        btnExportPdf = findViewById(R.id.btnExportPdf);

        // Setup spinner (pastikan string sesuai dengan ViewModel)
        String[] filters = {"Hari Ini", "Minggu Ini", "Bulan Ini", "Custom"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, filters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // Setup ViewModel & RecyclerView
        laporanViewModel = new ViewModelProvider(this).get(LaporanViewModel.class);
        recyclerLaporan.setLayoutManager(new LinearLayoutManager(this));
        laporanAdapter = new LaporanTransaksiAdapter(new ArrayList<>());
        recyclerLaporan.setAdapter(laporanAdapter);

        setupObservers();
        setupListeners();

        // Default: "Hari Ini"
        spinnerFilter.setSelection(0);
        laporanViewModel.filterData("Hari Ini", null, null);
    }

    private void setupObservers() {
        laporanViewModel.getTotalPendapatan().observe(this, pendapatan -> {
            txtTotalPendapatan.setText(formatRupiah(pendapatan != null ? pendapatan : 0.0));
        });

        laporanViewModel.getJumlahTransaksi().observe(this, jumlah -> {
            txtJumlahTransaksi.setText(String.valueOf(jumlah != null ? jumlah : 0));
        });

        laporanViewModel.getTransaksiList().observe(this, transaksis -> {
            if (transaksis != null) {
                laporanAdapter.updateData(transaksis);
            }
        });
    }

    private void setupListeners() {
        btnExportPdf.setOnClickListener(v -> exportAndPrintPdf());

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                if (filter.equals("Custom")) {
                    btnCustomDate.setVisibility(View.VISIBLE);
                } else {
                    btnCustomDate.setVisibility(View.GONE);
                    laporanViewModel.filterData(filter, null, null);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnCustomDate.setOnClickListener(v -> {
            // Build date range picker dengan Pair<Long, Long>
            MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("Pilih Rentang Tanggal");
            MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

            picker.show(getSupportFragmentManager(), "DATE_RANGE_PICKER");

            // selection datang sebagai Object, jadi cast ke Pair<Long, Long>
            picker.addOnPositiveButtonClickListener(selection -> {
                Pair<Long, Long> range = (Pair<Long, Long>) selection;
                if (range != null && range.first != null && range.second != null) {
                    Date start = new Date(range.first);
                    Date end = new Date(range.second);
                    laporanViewModel.filterData("Custom", start, end);
                }
            });
        });
    }

    // ---------------- PDF export & print ----------------
    private void exportAndPrintPdf() {
        List<Transaksi> transaksiList = laporanViewModel.getTransaksiList().getValue();
        Double totalPendapatan = laporanViewModel.getTotalPendapatan().getValue();
        Integer jumlahTransaksi = laporanViewModel.getJumlahTransaksi().getValue();

        if (totalPendapatan == null) totalPendapatan = 0.0;
        if (jumlahTransaksi == null) jumlahTransaksi = 0;

        if (transaksiList != null && !transaksiList.isEmpty()) {
            try {
                File pdfFile = createPdf(transaksiList, totalPendapatan, jumlahTransaksi);
                printPdf(pdfFile);
            } catch (IOException e) {
                Log.e("LaporanActivity", "Error creating or printing PDF", e);
                Toast.makeText(this, "Gagal membuat PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show();
        }
    }

    private File createPdf(List<Transaksi> transaksiList, double totalPendapatan, int jumlahTransaksi) throws IOException {
        String pdfFileName = "Laporan_Penjualan_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), pdfFileName);

        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Laporan Penjualan TaniMart")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20));

        String tglExport = new SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        document.add(new Paragraph("Diekspor pada: " + tglExport)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));

        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total Pendapatan: " + formatRupiah(totalPendapatan)));
        document.add(new Paragraph("Jumlah Transaksi: " + jumlahTransaksi));
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Detail Transaksi").setBold());

        float[] columnWidths = {3, 4, 3};
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));

        table.addHeaderCell(new Cell().add(new Paragraph("ID Transaksi").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Tanggal").setBold()));
        table.addHeaderCell(new Cell().add(new Paragraph("Total").setBold().setTextAlignment(TextAlignment.RIGHT)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        for (Transaksi trx : transaksiList) {
            table.addCell(new Cell().add(new Paragraph(trx.getIdTransaksi() != null ? trx.getIdTransaksi() : "N/A")));
            String tgl = trx.getTanggal() != null ? dateFormat.format(trx.getTanggal().toDate()) : "N/A";
            table.addCell(new Cell().add(new Paragraph(tgl)));
            table.addCell(new Cell().add(new Paragraph(formatRupiah(trx.getTotal())).setTextAlignment(TextAlignment.RIGHT)));
        }

        document.add(table);
        document.close();

        Log.i("LaporanActivity", "PDF created at: " + pdfFile.getAbsolutePath());
        return pdfFile;
    }

    private void printPdf(File pdfFile) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Laporan";

        PdfDocumentAdapter printAdapter = new PdfDocumentAdapter(this, pdfFile.getAbsolutePath());
        printManager.print(jobName, printAdapter, null);
    }

    private String formatRupiah(double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeID);
        return currencyFormatter.format(number);
    }
}
