package com.example.tanimart.ui.common.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.tanimart.R;
import com.example.tanimart.data.repository.UserRepository;
import com.example.tanimart.utils.FileUtil;
import com.example.tanimart.utils.PrefsUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView imgProfile;
    private EditText etName;
    private Button btnSave, btnChangePhoto;
    private Uri selectedUri;

    private final UserRepository repo = new UserRepository();
    private final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedUri = uri;
                    Glide.with(this).load(uri).into(imgProfile);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgProfile = findViewById(R.id.imgProfileEdit);
        etName = findViewById(R.id.etName);
        btnSave = findViewById(R.id.btnSave);
        btnChangePhoto = findViewById(R.id.btnChangePhoto);

        repo.getUser(uid, user -> {
            if (user != null) {
                etName.setText(user.getName());
                String localPath = PrefsUtil.getPhotoPath(this, uid);
                if (localPath != null && new File(localPath).exists()) {
                    Glide.with(this).load(new File(localPath)).into(imgProfile);
                }
            }
        });

        btnChangePhoto.setOnClickListener(v -> imagePicker.launch("image/*"));

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            repo.updateUserName(uid, name);

            if (selectedUri != null) {
                String path = FileUtil.saveImageToInternalStorage(this, selectedUri, uid);
                if (path != null) {
                    PrefsUtil.savePhotoPath(this, uid, path);
                }
            }

            Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show();

            // Tambahkan baris ini biar ProfileFragment tahu update-nya
            setResult(RESULT_OK);
            finish();
        });

    }
}
