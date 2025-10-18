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
// ... import lainnya
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
import com.example.tanimart.data.model.Product;
import com.example.tanimart.ui.adapter.DaftarProdukAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date; // Import java.util.Date
import java.util.List;

public class KelolaProdukMasukActivity extends AppCompatActivity {

    private static final String TAG = "KelolaProdukMasuk";

    private KelolaProdukMasukViewModel kelolaProdukMasukViewModel;
    private RecyclerView recyclerView;
    private DaftarProdukAdapter daftarProdukAdapter;

    private ActivityResultLauncher<Intent> editProductLauncher;

    // --- Akhir Perubahan 2 ---
    ImageView uploadImage;
    Button saveBtnKPM;
    EditText uploadNB, uploadHJ, uploadStokBarangMasuk, deskripsiProduk;
    TextView pilihMerek, pilihKategori, pilihSatuanUnit;
    Toolbar toolbar;
    Uri uri;

    private ArrayList<String> merekList = new ArrayList<>();
    private ArrayList<String> kategoriList = new ArrayList<>();
    private ArrayList<String> satuanList = new ArrayList<>();
    private List<Product> semuaProduk = new ArrayList<>();

    private String selectedMerek = "";
    private String selectedKategori = "";
    private String selectedSatuan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelola_produk_masuk);

        // ... (kode EdgeToEdge, OnBackPressed, Toolbar tetap sama)
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
        populateDefaultLists();

        editProductLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Cek jika sinyalnya "OK"
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Toast.makeText(this, "Memeriksa pembaruan data...", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // inisialisasi view model
        kelolaProdukMasukViewModel = new ViewModelProvider(this).get(KelolaProdukMasukViewModel.class);
        setupRecyclerView();

        kelolaProdukMasukViewModel.getProdukList().observe(this, productList -> {
            semuaProduk.clear();
            semuaProduk.addAll(productList);
            daftarProdukAdapter.setProductList(productList);
        });

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
        // ... (kode initViews() tidak berubah)
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
        // ... (kode populateDefaultLists() tidak berubah)
        merekList.clear();
        merekList.add("Umum");
        merekList.add("Pupuk");

        satuanList.clear();
        satuanList.add("Pcs");
        satuanList.add("Kg");
        satuanList.add("Liter");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("kategori_produk").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    kategoriList.clear();
                    if (queryDocumentSnapshots != null) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            String nama = doc.getString("nama");
                            if (nama != null) kategoriList.add(nama);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (kategoriList.isEmpty()) {
                        kategoriList.add("Pupuk");
                        kategoriList.add("Bibit");
                        kategoriList.add("Peralatan");
                    }
                });
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Buat adapter dengan tipe TIPE_KELOLA_PRODUK
        daftarProdukAdapter = new DaftarProdukAdapter(
                new ArrayList<>(),
                product -> {

                    // Tampilkan dialog dengan pilihan Edit atau Hapus
                    final CharSequence[] options = {"Edit Produk", "Hapus Produk", "Batal"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(KelolaProdukMasukActivity.this);
                    builder.setTitle("Pilih Aksi untuk " + product.getNamaProduk());
                    builder.setItems(options, (dialog, item) -> {
                        if (options[item].equals("Edit Produk")) {
                            // Aksi untuk Edit
                            Intent intent = new Intent(KelolaProdukMasukActivity.this, EditProdukActivity.class);
                            intent.putExtra("product", product);
                            editProductLauncher.launch(intent);
                        } else if (options[item].equals("Hapus Produk")) {
                            // Aksi untuk Hapus
                            new AlertDialog.Builder(KelolaProdukMasukActivity.this)
                                    .setTitle("Konfirmasi Hapus")
                                    .setMessage("Anda yakin ingin menghapus produk ini?")
                                    .setPositiveButton("Hapus", (d, w) -> kelolaProdukMasukViewModel.deleteProduct(product.getId()))
                                    .setNegativeButton("Batal", null)
                                    .show();
                        } else if (options[item].equals("Batal")) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                },
                DaftarProdukAdapter.TIPE_KELOLA_PRODUK // <-- Memberi tahu tipe layoutnya
        );

        recyclerView.setAdapter(daftarProdukAdapter);
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
            convertImageToBase64AndSave(loadingDialog);
        } else {
            saveProductToFirestore("", loadingDialog);
        }
    }

    private void convertImageToBase64AndSave(AlertDialog loadingDialog) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            int targetWidth = 480;
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
        int stok;

        try {
            hargaJual = Double.parseDouble(hargaStr);
            stok = Integer.parseInt(stokStr);
        } catch (NumberFormatException e) {
            loadingDialog.dismiss();
            Toast.makeText(this, "Harga dan Stok harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        // Buat objek Product
        Product product = new Product();
        product.setNamaProduk(namaProduk);
        product.setHargaJual(hargaJual);
        product.setMerek(selectedMerek);
        product.setKategori(selectedKategori);
        product.setStok(stok);
        product.setSatuan(selectedSatuan);
        product.setTanggal(new Date());
        product.setImageUrl(imageString);
        product.setDeskripsi(deskripsi);

        // Panggil metode addProduct dari ViewModel
        kelolaProdukMasukViewModel.addProduct(product);

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
                            if (title.equals("Pilih Kategori")) {
                                FirebaseFirestore.getInstance()
                                        .collection("kategori_produk")
                                        .add(new java.util.HashMap<String, Object>() {{
                                            put("nama", newItem);
                                        }})
                                        .addOnSuccessListener(ref -> Log.d("Firestore", "Kategori disimpan"))
                                        .addOnFailureListener(e -> Log.e("Firestore", "Gagal simpan kategori", e));
                            }
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
