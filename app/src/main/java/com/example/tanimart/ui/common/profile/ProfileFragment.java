package com.example.tanimart.ui.common.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.example.tanimart.R;
import com.example.tanimart.utils.PrefsUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;

public class ProfileFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvName, tvEmail, tvRole;
    private Button btnEdit, btnLogout;
    private ProfileViewModel viewModel;
    private String uid;

    // Launcher ini sudah benar, akan memicu refresh setelah EditProfileActivity selesai.
    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("ProfileFragment", "Update diterima, merefresh profil...");
                    // Refresh data teks (nama, email, dll)
                    viewModel.loadUser();
                    // Refresh foto dari penyimpanan lokal (ini akan dipanggil setelah viewModel selesai)
                    // Panggilan refreshPhoto() di observer sudah cukup.
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupViewModel();
        setupClickListeners();
        loadInitialData();
    }

    private void initializeViews(View view) {
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnKeluar);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvRole.setText("Peran: " + user.getRole());
                // PERBAIKAN: Panggilan refreshPhoto() dipusatkan di sini.
                // Ini akan dieksekusi baik saat data pertama kali dimuat maupun setelah diperbarui.
                refreshPhoto();
            }
        });
    }

    private void setupClickListeners() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void loadInitialData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            this.uid = currentUser.getUid();
            // Cukup panggil loadUser(). Observer di setupViewModel akan menangani pembaruan UI.
            viewModel.loadUser();
        }
    }

    /**
     * PERBAIKAN UTAMA: Memuat ulang gambar profil dan MENGALAHKAN CACHE GLIDE.
     */
    private void refreshPhoto() {
        if (getContext() == null || uid == null) {
            return;
        }

        String localPath = PrefsUtil.getPhotoPath(getContext(), uid);
        Log.d("ProfileFragment", "Mencoba memuat foto dari path: " + localPath);

        if (localPath != null && new File(localPath).exists()) {
            File imageFile = new File(localPath);

            // Perbaikan paling penting untuk memaksa Glide memuat ulang gambar
            Glide.with(this)
                    .load(imageFile)
                    // 1. Kunci unik berdasarkan kapan file terakhir diubah. Jika file berubah, kunci ini berubah.
                    .signature(new ObjectKey(String.valueOf(imageFile.lastModified())))
                    // 2. (Opsional tapi direkomendasikan) Lewati cache memori untuk permintaan ini.
                    .skipMemoryCache(true)
                    // 3. (Opsional tapi direkomendasikan) Jangan gunakan cache disk untuk permintaan ini.
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .error(R.drawable.baseline_account_circle_24)
                    .into(imgProfile);
        } else {
            // Jika path tidak ada, tampilkan gambar default.
            Glide.with(this)
                    .load(R.drawable.baseline_account_circle_24)
                    .into(imgProfile);
        }
    }
}
