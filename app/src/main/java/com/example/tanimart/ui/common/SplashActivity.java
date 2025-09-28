package com.example.tanimart.ui.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
//import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tanimart.databinding.ActivitySplashBinding;
import com.example.tanimart.ui.common.login.LoginActivity;
import com.example.tanimart.ui.common.signup.SignUpActivity;


public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding=ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // Tombol Login
        binding.splashButtonLogin.setOnClickListener(v ->
                startActivity(new Intent(SplashActivity.this, LoginActivity.class)));

        // Textview Signup pakai binding juga
        binding.redirectSplashToSignup.setOnClickListener(v -> {
            startActivity(new Intent(SplashActivity.this, SignUpActivity.class));
        });

    }
}
