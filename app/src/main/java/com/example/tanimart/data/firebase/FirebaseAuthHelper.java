package com.example.tanimart.data.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {
    private final FirebaseAuth auth;

    public FirebaseAuthHelper() {
        auth = FirebaseAuth.getInstance();
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser User);

        void onFailure(String errorMessage);
    }

    public void login(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("User tidak ditemukan");
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));

    }


}

