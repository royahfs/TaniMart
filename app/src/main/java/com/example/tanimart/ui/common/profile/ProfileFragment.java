package com.example.tanimart.ui.common.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.tanimart.R;
import com.example.tanimart.utils.PrefsUtil;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;

public class ProfileFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvName, tvEmail, tvRole;
    private Button btnEdit, btnLogout;
    private ProfileViewModel viewModel;

    // ✅ Launcher untuk hasil dari EditProfileActivity
    private final ActivityResultLauncher<Intent> editProfileLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Refresh ulang data profil setelah edit
                    viewModel.loadUser();
                    refreshPhoto();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = v.findViewById(R.id.imgProfile);
        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvRole = v.findViewById(R.id.tvRole);
        btnEdit = v.findViewById(R.id.btnEdit);
        btnLogout = v.findViewById(R.id.btnLogout);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Observasi data user dari ViewModel
        viewModel.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
                tvRole.setText("Peran: " + user.getRole());
                refreshPhoto();
            }
        });

        // Buka halaman edit profil pakai launcher
        btnEdit.setOnClickListener(vw -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            editProfileLauncher.launch(intent);
        });

        // Logout user
        btnLogout.setOnClickListener(vw -> {
            FirebaseAuth.getInstance().signOut();
            requireActivity().finish();
        });

        // load data user pertama kali
        viewModel.loadUser();

        return v;
    }

    // Refresh foto profil (ambil dari penyimpanan lokal)
    private void refreshPhoto() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String localPath = PrefsUtil.getPhotoPath(requireContext(), uid);

        if (localPath != null && new File(localPath).exists()) {
            Glide.with(requireContext())
                    .load(new File(localPath))
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(imgProfile);
        } else {
            imgProfile.setImageResource(R.drawable.baseline_account_circle_24);
        }
    }

    // data direfresh kalau user balik tanpa result
    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadUser();
        refreshPhoto();
    }
}
