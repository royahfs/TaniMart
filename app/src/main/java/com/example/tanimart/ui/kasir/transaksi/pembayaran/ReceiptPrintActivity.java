package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Product;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;

public class ReceiptPrintActivity extends AppCompatActivity {
    private PrintViewModel viewModel;

    // MAC address printer Bluetooth (isi sesuai device kamu)
    private static final String PRINTER_MAC = "00:11:22:33:44:55";
    // Deklarasi formatter
    private NumberFormat formatter;
    // UI References
    private TextView tvStoreName, tvStoreBranch, tvCashier, tvDate, tvInvoice, tvPayment;
    private TextView tvSubtotal, tvTotal, tvPay, tvChange;
    private LinearLayout layoutItems;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = true;
                for (Boolean b : result.values()) {
                    if (!b) {
                        granted = false;
                        break;
                    }
                }
                if (granted) {
                    connectPrinter();
                } else {
                    Toast.makeText(this, "Permission ditolak!", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_print);

        viewModel = new ViewModelProvider(this).get(PrintViewModel.class);

        // Bind tombol
        Button btnConnect = findViewById(R.id.btnConnect);
        Button btnPrint = findViewById(R.id.btnPrint);

        // Bind receipt_layout
        tvStoreName = findViewById(R.id.tv_store_name);
        tvStoreBranch = findViewById(R.id.tv_store_branch);
        tvCashier = findViewById(R.id.tv_cashier);
        tvDate = findViewById(R.id.tv_date);
        tvInvoice = findViewById(R.id.tv_invoice);
        tvPayment = findViewById(R.id.tv_payment);
        tvTotal = findViewById(R.id.tv_total);
        tvPay = findViewById(R.id.tv_pay);
        tvChange = findViewById(R.id.tv_change);
        layoutItems = findViewById(R.id.layout_items);



        formatter = NumberFormat.getNumberInstance(new Locale("id", "ID"));

        // ambil data dari intent
        String tanggal = getIntent().getStringExtra("TANGGAL");
        double totalTagihan = getIntent().getDoubleExtra("TOTAL_TAGIHAN", 0.0);
        double uangDiterima = getIntent().getDoubleExtra("UANG_DITERIMA", 0.0);
        double kembalian = getIntent().getDoubleExtra("KEMBALIAN", 0.0);

        // update text di receipt_layout
        tvDate.setText(tanggal);
        tvTotal.setText("Total: Rp " + formatter.format(totalTagihan));
        tvPay.setText("Bayar: Rp " + formatter.format(uangDiterima));
        tvChange.setText("Kembali: Rp " + formatter.format(kembalian));

        // Ambil daftar produk dari intent
        ArrayList<Product> productList = getIntent().getParcelableArrayListExtra("CART_LIST");
        // Loop dan tambahkan produk ke layout
        if (productList != null) {
            for (Product product : productList) {
                addProduct(product.getNamaProduk(), product.getQuantity(), product.getHargaJual());
            }
        }

        // Tombol koneksi printer
        btnConnect.setOnClickListener(v -> checkPermissionAndConnect());

        // Tombol print
        btnPrint.setOnClickListener(v -> {
            StringBuilder receiptText = new StringBuilder();
            receiptText.append(tvStoreName.getText().toString()).append("\n");
            receiptText.append(tvStoreBranch.getText().toString()).append("\n\n");

            receiptText.append("Kasir : ").append(tvCashier.getText().toString()).append("\n");
            receiptText.append("Waktu : ").append(tvDate.getText().toString()).append("\n");
            receiptText.append("No. Struk : ").append(tvInvoice.getText().toString()).append("\n");
            receiptText.append("Jenis Pembayaran : ").append(tvPayment.getText().toString()).append("\n\n");

            // Loop produk dari layout_items
            for (int i = 0; i < layoutItems.getChildCount(); i++) {
                View itemView = layoutItems.getChildAt(i);
                TextView tvName = itemView.findViewById(R.id.tv_item_name);
                TextView tvQtyPrice = itemView.findViewById(R.id.tv_item_qty_price);
                TextView tvItemTotal = itemView.findViewById(R.id.tv_item_total);

                receiptText.append(tvName.getText().toString())
                        .append("  ")
                        .append(tvQtyPrice.getText().toString())
                        .append("  ")
                        .append(tvItemTotal.getText().toString())
                        .append("\n");
            }
            receiptText.append("\n");

            // Ringkasan
            receiptText.append("--------------------------------\n");
            receiptText.append(tvTotal.getText()).append("\n");
            receiptText.append(tvPay.getText()).append("\n");
            receiptText.append(tvChange.getText()).append("\n");
            receiptText.append("================================\n");
            receiptText.append("Terima Kasih Telah Berbelanja!\n");
            receiptText.append("=== TANI MART ===\n\n");

            // Kirim ke printer
            viewModel.printReceipt(receiptText.toString().getBytes());
        });

        // Observer status koneksi printer
        viewModel.getIsConnected().observe(this, connected -> {
            if (connected != null && connected) {
                Toast.makeText(this, "Printer terhubung!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Gagal konek printer", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPermissionAndConnect() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                });
                return;
            }
        }
        connectPrinter();
    }

    private void connectPrinter() {

        viewModel.connectPrinter(PRINTER_MAC, this);
    }

    // ============================
    // Helper untuk tambah produk
    // ============================
    private void addProduct(String name, int qty, double price) {
        View itemView = getLayoutInflater().inflate(R.layout.item_receipt_product, layoutItems, false);

        TextView tvName = itemView.findViewById(R.id.tv_item_name);
        TextView tvQtyPrice = itemView.findViewById(R.id.tv_item_qty_price);
        TextView tvTotal = itemView.findViewById(R.id.tv_item_total);

        tvName.setText(name);
        tvQtyPrice.setText(qty + " x " + formatCurrency(price));
        tvTotal.setText(formatCurrency(qty * price));

        layoutItems.addView(itemView);

    }

    private String formatCurrency(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return format.format(value);
    }
}
