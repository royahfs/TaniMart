package com.example.tanimart.ui.common.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.data.model.User;
import com.example.tanimart.ui.admin.dashboard.AdminDashboardActivity;
import com.example.tanimart.ui.common.signup.SignUpActivity;
import com.example.tanimart.ui.karyawan.dashboard.KaryawanDashboardActivity;
import com.example.tanimart.ui.kasir.dashboard.KasirDashboardActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEt, passwordEt;
    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        emailEt = findViewById(R.id.etEmail_login);
        passwordEt = findViewById(R.id.etPassword_login);
        Button loginBtn = findViewById(R.id.login_button);
        TextView redirectLoginToSignUp= findViewById(R.id.redirectLoginToSignup);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginBtn.setOnClickListener(v -> {
            String email = emailEt.getText().toString();
            String password = passwordEt.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginViewModel.loginUser(email, password);
            }
        });

        redirectLoginToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        observeViewModel();
        }

    private void observeViewModel() {
        loginViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                navigateToDashboard(user);
            }
        });

        loginViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void navigateToDashboard(User user) {

        Intent intent;
        switch (user.getRole()) {
            case "Admin":
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                break;
            case "Kasir":
                intent = new Intent(LoginActivity.this, KasirDashboardActivity.class);
                break;
            case "Karyawan":
                intent = new Intent(LoginActivity.this, KaryawanDashboardActivity.class);
                break;
            default:
                Toast.makeText(this, "Role tidak dikenali", Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish();

    }
}
