package com.example.tanimart.ui.common.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.ui.adapter.InventoryAdapter;

import java.util.ArrayList;

public class KelolaProdukMasukActivity extends AppCompatActivity {
    private KelolaProdukMasukViewModel viewModel;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    ImageView uploadImage;
    Button saveBtnKPM;
    EditText uploadNB, uploadHJ, uploadStokBarangMasuk;
    TextView pilihMerek, pilihKategori, pilihSatuanUnit;
    Toolbar toolbar;
    Uri uri;

    // simpan data merek & kategori sementara
    private ArrayList<String> merekList = new ArrayList<>();
    private ArrayList<String> kategoriList = new ArrayList<>();
    private ArrayList<String> satuanList = new ArrayList<>();

    private String selectedMerek = "";
    private String selectedKategori = "";
    private String selectedSatuan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_produk_masuk);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainKelolaProduk), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top , systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish(); // biar balik normal
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        toolbar = findViewById(R.id.toolbar_kelolaProduk);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(KelolaProdukMasukViewModel.class);
        uploadImage = findViewById(R.id.uploadImage);
        saveBtnKPM = findViewById(R.id.saveBtnKPM);
        uploadNB = findViewById(R.id.uploadNB);
        uploadHJ = findViewById(R.id.uploadHJ);
        uploadStokBarangMasuk = findViewById(R.id.uploadStokBarangMasuk);
        pilihSatuanUnit = findViewById(R.id.pilihSatuanUnit);
        pilihMerek = findViewById(R.id.pilihMerek);
        pilihKategori = findViewById(R.id.pilihKategori);

        // isi default
        merekList.add("Umum");
        merekList.add("Pupuk");


        kategoriList.add("Pupuk");
        kategoriList.add("Pupuk Subsidi");
        kategoriList.add("Bibit");
        kategoriList.add("Peralatan");


        satuanList.add("Pcs");
        satuanList.add("Kg");
        satuanList.add("Liter");

        // recycler view
        recyclerView = findViewById(R.id.recyclerInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(
                new ArrayList<>(),
                inventory -> Toast.makeText(this, "Klik: " + inventory.getNamaProduk(), Toast.LENGTH_SHORT).show(),
                inventory -> viewModel.deleteInventory(inventory.getId())
        );
        recyclerView.setAdapter(adapter);

        // observe LiveData
        viewModel.getInventoryList().observe(this, inventories -> adapter.setInventoryList(inventories));

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(KelolaProdukMasukActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        uploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });

        pilihMerek.setOnClickListener(v -> showDialogMerek());
        pilihKategori.setOnClickListener(v -> showDialogKategori());
        pilihSatuanUnit.setOnClickListener(v -> showDialogSatuanUnit());
        saveBtnKPM.setOnClickListener(view -> saveData());
    }

    private void showDialogMerek() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_merek);

        ListView listView = dialog.findViewById(R.id.listMerek);
        TextView btnTambah = dialog.findViewById(R.id.btnTambahMerek);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, merekList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedMerek = merekList.get(position);
            pilihMerek.setText(selectedMerek);
            dialog.dismiss();
        });

        btnTambah.setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Tambah Merek")
                    .setView(input)
                    .setPositiveButton("Simpan", (d, which) -> {
                        String newItem = input.getText().toString().trim();
                        if (!newItem.isEmpty()) {
                            merekList.add(newItem);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        dialog.show();
    }

    private void showDialogKategori() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_kategori);

        ListView listView = dialog.findViewById(R.id.listKategori);
        TextView btnTambah = dialog.findViewById(R.id.btnTambahSatuanUnit); // diperbaiki id

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, kategoriList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedKategori = kategoriList.get(position);
            pilihKategori.setText(selectedKategori);
            dialog.dismiss();
        });

        btnTambah.setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Tambah Kategori")
                    .setView(input)
                    .setPositiveButton("Simpan", (d, which) -> {
                        String newItem = input.getText().toString().trim();
                        if (!newItem.isEmpty()) {
                            kategoriList.add(newItem);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        dialog.show();
    }

    private void showDialogSatuanUnit() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_satuan_unit);

        ListView listView = dialog.findViewById(R.id.listSatuanUnit);
        TextView btnTambah = dialog.findViewById(R.id.btnTambahSatuanUnit); // pakai id baru

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, satuanList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedSatuan = satuanList.get(position);
            pilihSatuanUnit.setText(selectedSatuan);
            dialog.dismiss();
        });

        btnTambah.setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Tambah Satuan Unit")
                    .setView(input)
                    .setPositiveButton("Simpan", (d, which) -> {
                        String newItem = input.getText().toString().trim();
                        if (!newItem.isEmpty()) {
                            satuanList.add(newItem); // simpan ke list yang benar
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        dialog.show();
    }



    public void saveData() {
        String namaProduk = uploadNB.getText().toString().trim();
        String hargaStr = uploadHJ.getText().toString().trim();
        String stokStr = uploadStokBarangMasuk.getText().toString().trim();

        if (namaProduk.isEmpty() || hargaStr.isEmpty()
                || selectedMerek.isEmpty() || selectedKategori.isEmpty() ||
                stokStr.isEmpty() || selectedSatuan.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        double hargaJual;
        try {
            hargaJual = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Harga jual harus angka", Toast.LENGTH_SHORT).show();
            return;
        }

        double stok;
        try {
            stok = Double.parseDouble(stokStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Stok harus angka", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        String imageUrl = (uri != null) ? uri.toString() : "";

        Inventory inventory = new Inventory(
                null,
                namaProduk,
                hargaJual,
                selectedMerek,
                selectedKategori,
                stok,
                selectedSatuan,
                tanggal,
                imageUrl
        );

        viewModel.addInventory(inventory);
        Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();

        // reset form
        uploadNB.setText("");
        uploadHJ.setText("");
        uploadStokBarangMasuk.setText("");
        pilihMerek.setText("Pilih Merek");
        pilihKategori.setText("Pilih Kategori");
        pilihSatuanUnit.setText("Pilih Satuan");
        selectedMerek = "";
        selectedKategori = "";
        selectedSatuan = "";
        uri = null;
        uploadImage.setImageResource(R.drawable.upload);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
