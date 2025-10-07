package com.example.tanimart.ui.common.inventory;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.tanimart.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KategoriProdukActivity extends AppCompatActivity {

    private EditText inputKategori;
    private Button btnTambahKategori;
    private ListView listKategori;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> kategoriList;
    private ArrayList<String> kategoriDocIds;
    private CollectionReference kategoriRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_produk);

        Toolbar toolbar = findViewById(R.id.toolbar_kategoriProduk);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Kategori Produk");
        }

        // Inisialisasi Firestore
        kategoriRef = FirebaseFirestore.getInstance().collection("kategori_produk");

        inputKategori = findViewById(R.id.inputKategori);
        btnTambahKategori = findViewById(R.id.btnTambahKategori);
        listKategori = findViewById(R.id.listKategori);

        kategoriList = new ArrayList<>();
        kategoriDocIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, kategoriList);
        listKategori.setAdapter(adapter);

        loadKategoriFromFirestore();

        btnTambahKategori.setOnClickListener(v -> {
            String nama = inputKategori.getText().toString().trim();
            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama kategori tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }
            if (kategoriList.contains(nama)) {
                Toast.makeText(this, "Kategori sudah ada", Toast.LENGTH_SHORT).show();
                return;
            }
            addKategoriToFirestore(nama);
        });

        listKategori.setOnItemLongClickListener((parent, view, position, id) -> {
            String nama = kategoriList.get(position);
            String docId = kategoriDocIds.size() > position ? kategoriDocIds.get(position) : null;

            new AlertDialog.Builder(this)
                    .setTitle("Hapus Kategori")
                    .setMessage("Hapus kategori \"" + nama + "\"?")
                    .setPositiveButton("Hapus", (dialog, which) -> {
                        if (docId != null) {
                            kategoriRef.document(docId)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        kategoriList.remove(position);
                                        kategoriDocIds.remove(position);
                                        adapter.notifyDataSetChanged();
                                        Toast.makeText(this, "Kategori dihapus", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Gagal menghapus kategori", Toast.LENGTH_SHORT).show());
                        }
                    })
                    .setNegativeButton("Batal", null)
                    .show();
            return true;
        });
    }

    private void loadKategoriFromFirestore() {
        kategoriRef.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    kategoriList.clear();
                    kategoriDocIds.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String nama = doc.getString("nama");
                        if (nama != null) {
                            kategoriList.add(nama);
                            kategoriDocIds.add(doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal memuat kategori", Toast.LENGTH_SHORT).show());
    }

    private void addKategoriToFirestore(String namaKategori) {
        Map<String, Object> data = new HashMap<>();
        data.put("nama", namaKategori);

        kategoriRef.add(data)
                .addOnSuccessListener(documentReference -> {
                    kategoriList.add(namaKategori);
                    kategoriDocIds.add(documentReference.getId());
                    adapter.notifyDataSetChanged();
                    inputKategori.setText("");
                    Toast.makeText(this, "Kategori ditambahkan", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Gagal menambah kategori", Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
