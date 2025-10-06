package com.example.tanimart.ui.common.inventory;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tanimart.R;
import com.example.tanimart.data.model.Inventory;
import com.example.tanimart.ui.adapter.InventoryAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class KelolaProdukMasukActivity extends AppCompatActivity {
    // Definisikan TAG untuk logging
    private static final String TAG = "KelolaProdukMasuk";

    private KelolaProdukMasukViewModel viewModel;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    ImageView uploadImage;
    Button saveBtnKPM;
    EditText uploadNB, uploadHJ, uploadStokBarangMasuk, deskripsiProduk;
    TextView pilihMerek, pilihKategori, pilihSatuanUnit;
    Toolbar toolbar;
    Uri uri;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        toolbar = findViewById(R.id.toolbar_kelolaProduk);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        initViews();
        viewModel = new ViewModelProvider(this).get(KelolaProdukMasukViewModel.class);
        populateDefaultLists();
        setupRecyclerView();
        viewModel.getInventoryList().observe(this, inventories -> adapter.setInventoryList(inventories));

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        uploadImage.setImageURI(uri);
                    } else {
                        Toast.makeText(KelolaProdukMasukActivity.this, "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show();
                    }
                });

        setupListeners(activityResultLauncher);
    }

    private void initViews() {
        uploadImage = findViewById(R.id.uploadImage);
        saveBtnKPM = findViewById(R.id.saveBtnKPM);
        uploadNB = findViewById(R.id.uploadNB);
        uploadHJ = findViewById(R.id.uploadHJ);
        uploadStokBarangMasuk = findViewById(R.id.uploadStokBarangMasuk);
        pilihSatuanUnit = findViewById(R.id.pilihSatuanUnit);
        pilihMerek = findViewById(R.id.pilihMerek);
        pilihKategori = findViewById(R.id.pilihKategori);
        deskripsiProduk = findViewById(R.id.deskripsiProduk);
        recyclerView = findViewById(R.id.recyclerInventory);
    }

    private void populateDefaultLists() {
        merekList.add("Umum");
        merekList.add("Pupuk");

        kategoriList.add("Pupuk");
        kategoriList.add("Pupuk Subsidi");
        kategoriList.add("Bibit");
        kategoriList.add("Peralatan");

        satuanList.add("Pcs");
        satuanList.add("Kg");
        satuanList.add("Liter");
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new InventoryAdapter(
                new ArrayList<>(),
                inventory -> Toast.makeText(this, "Klik: " + inventory.getNamaProduk(), Toast.LENGTH_SHORT).show(),
                inventory -> viewModel.deleteInventory(inventory.getId())
        );
        recyclerView.setAdapter(adapter);
    }

    private void setupListeners(ActivityResultLauncher<Intent> launcher) {
        uploadImage.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            launcher.launch(intent);
        });

        pilihMerek.setOnClickListener(v -> showDialog(merekList, "Pilih Merek", item -> {
            selectedMerek = item;
            pilihMerek.setText(item);
        }));
        pilihKategori.setOnClickListener(v -> showDialog(kategoriList, "Pilih Kategori", item -> {
            selectedKategori = item;
            pilihKategori.setText(item);
        }));
        pilihSatuanUnit.setOnClickListener(v -> showDialog(satuanList, "Pilih Satuan", item -> {
            selectedSatuan = item;
            pilihSatuanUnit.setText(item);
        }));

        saveBtnKPM.setOnClickListener(view -> saveData());
    }

    // =========================================================================
    //         BAGIAN UTAMA YANG DIPERBAIKI: ALUR PENYIMPANAN BASE64
    // =========================================================================

    public void saveData() {
        String namaProduk = uploadNB.getText().toString().trim();
        String hargaStr = uploadHJ.getText().toString().trim();
        String stokStr = uploadStokBarangMasuk.getText().toString().trim();
        String deskripsi = deskripsiProduk.getText().toString().trim();

        if (namaProduk.isEmpty() || hargaStr.isEmpty() || selectedMerek.isEmpty() ||
                selectedKategori.isEmpty() || stokStr.isEmpty() || selectedSatuan.isEmpty() || deskripsi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog loadingDialog = new AlertDialog.Builder(this)
                .setMessage("Menyimpan produk...")
                .setCancelable(false)
                .show();

        if (uri != null) {
            // Jika ada gambar, konversi ke Base64, lalu simpan
            convertImageToBase64AndSave(loadingDialog);
        } else {
            // Jika tidak ada gambar, langsung simpan dengan string kosong
            saveProductToFirestore("", loadingDialog);
        }
    }

    private void convertImageToBase64AndSave(AlertDialog loadingDialog) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

            // Kompres gambar agar ukurannya tidak melebihi batas 1MB Firestore
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            int targetWidth = 480; // Target lebar 480px sudah cukup jelas
            int targetHeight = (int) (originalHeight * ((float) targetWidth / (float) originalWidth));

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            saveProductToFirestore(base64Image, loadingDialog);

        } catch (IOException e) {
            loadingDialog.dismiss();
            Log.e(TAG, "Gagal memproses gambar", e);
            Toast.makeText(this, "Gagal memproses gambar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveProductToFirestore(String imageString, AlertDialog loadingDialog) {
        String namaProduk = uploadNB.getText().toString().trim();
        String hargaStr = uploadHJ.getText().toString().trim();
        String stokStr = uploadStokBarangMasuk.getText().toString().trim();
        String deskripsi = deskripsiProduk.getText().toString().trim();

        double hargaJual;
        try {
            hargaJual = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Harga jual harus angka", Toast.LENGTH_SHORT).show();
            return;
        }

        double stok;
        try {
            stok = Double.parseDouble(stokStr);
        } catch (NumberFormatException e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Stok harus angka", Toast.LENGTH_SHORT).show();
            return;
        }

        String tanggal = java.text.DateFormat.getDateInstance().format(new java.util.Date());

        // Buat objek Inventory dengan imageUrl berisi string Base64
        Inventory inventory = new Inventory(
                null, namaProduk, hargaJual, selectedMerek, selectedKategori,
                stok, selectedSatuan, tanggal, imageString, deskripsi);

        viewModel.addInventory(inventory);
        loadingDialog.dismiss();
        Toast.makeText(this, "Produk berhasil disimpan", Toast.LENGTH_SHORT).show();
        resetForm();
    }

    private void resetForm() {
        uploadNB.setText("");
        uploadHJ.setText("");
        uploadStokBarangMasuk.setText("");
        deskripsiProduk.setText("");
        pilihMerek.setText("Pilih Merek");
        pilihKategori.setText("Pilih Kategori");
        pilihSatuanUnit.setText("Pilih Satuan");
        selectedMerek = "";
        selectedKategori = "";
        selectedSatuan = "";
        uri = null;
        uploadImage.setImageResource(R.drawable.upload);
    }

    // =========================================================================
    // BAGIAN DIALOG (Tidak ada perubahan di sini)
    // =========================================================================

    private void showDialog(ArrayList<String> dataList, String title, DialogCallback callback) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_generic_list);

        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        ListView listView = dialog.findViewById(R.id.genericListView);
        TextView btnTambah = dialog.findViewById(R.id.btnTambahItem);

        dialogTitle.setText(title);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = dataList.get(position);
            callback.onItemSelected(selectedItem);
            dialog.dismiss();
        });

        btnTambah.setOnClickListener(v -> {
            EditText input = new EditText(this);
            new AlertDialog.Builder(this)
                    .setTitle("Tambah " + title.replace("Pilih ", ""))
                    .setView(input)
                    .setPositiveButton("Simpan", (d, which) -> {
                        String newItem = input.getText().toString().trim();
                        if (!newItem.isEmpty() && !dataList.contains(newItem)) {
                            dataList.add(newItem);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
        });

        dialog.show();
    }

    interface DialogCallback {
        void onItemSelected(String item);
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
