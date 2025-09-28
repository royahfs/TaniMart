package com.example.tanimart.ui.common.signup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.tanimart.R;
import com.example.tanimart.ui.common.login.LoginActivity;
//import com.google.android.material.button.MaterialButton;


public class SignUpActivity extends AppCompatActivity {

    private EditText etName,etEmail, etPassword;
    private Spinner spinnerUserType;
    private Button btnSignup;
    private TextView redirectSignUpToLogin;

    private SignUpViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

//        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainSignUp), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Bind view
        etName = findViewById(R.id.etNameSignup);
        etEmail = findViewById(R.id.etEmailSignup);
        etPassword = findViewById(R.id.etPasswordSignup);
        spinnerUserType = findViewById(R.id.spinnerUserTypeSignup);
        btnSignup = findViewById(R.id.buttonSignup);
        redirectSignUpToLogin = findViewById(R.id.redirectSignupToLogin);


        // Init ViewModel
        signupViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        // Setup spinner (ambil dari arrays.xml )
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.spinner_user_type,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        // Observe
        observeViewModel();

        // Action Sign Up
        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String role = spinnerUserType.getSelectedItem().toString();

            if (email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Email & Password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            signupViewModel.registerUser(name, email, password, role);
        });
        // redirect to login
        redirectSignUpToLogin.setOnClickListener(v -> {
            // TODO: Intent ke LoginActivity
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void observeViewModel() {
        signupViewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Registration successful" + user.getRole(), Toast.LENGTH_SHORT).show();
                // ini biar ngarah ke login setelah register
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupViewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
