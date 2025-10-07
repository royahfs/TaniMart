package com.example.tanimart.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tanimart.data.firebase.FirebaseAuthHelper;
import com.example.tanimart.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseAuthHelper authHelper;
    private final FirebaseFirestore db;

    public UserRepository() {
        authHelper = new FirebaseAuthHelper();
        db = FirebaseFirestore.getInstance();
    }

    // ===================== LOGIN USER =====================
    public void loginUser(String email, String password,
                          MutableLiveData<User> userLiveData,
                          MutableLiveData<String> errorLiveData) {

        authHelper.login(email, password, new FirebaseAuthHelper.LoginCallback() {
            @Override
            public void onSuccess(FirebaseUser firebaseUser) {
                db.collection("users").document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                User user = documentSnapshot.toObject(User.class);
                                userLiveData.postValue(user);
                            } else {
                                errorLiveData.postValue("User tidak ditemukan di database.");
                            }
                        })
                        .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
            }

            @Override
            public void onFailure(String errorMessage) {
                errorLiveData.postValue(errorMessage);
            }
        });
    }

    // ===================== REGISTER USER =====================
    public void registerUser(String name, String email, String password, String role,
                             MutableLiveData<User> userLiveData,
                             MutableLiveData<String> errorLiveData) {

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // User default (tanpa foto)
                    User newUser = new User(uid, name, email, role, "default");


                    db.collection("users").document(uid)
                            .set(newUser)
                            .addOnSuccessListener(aVoid -> userLiveData.postValue(newUser))
                            .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
                })
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    // ===================== GET USER DATA =====================
    public void getUserData(@NonNull String userId,
                            MutableLiveData<User> userLiveData,
                            MutableLiveData<String> errorLiveData) {

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        userLiveData.postValue(user);
                    } else {
                        errorLiveData.postValue("Data profil tidak ditemukan.");
                    }
                })
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    // ===================== UPDATE PROFILE =====================
    public void updateUserProfile(@NonNull String userId, @NonNull User updatedUser,
                                  MutableLiveData<Boolean> successLiveData,
                                  MutableLiveData<String> errorLiveData) {

        db.collection("users").document(userId)
                .set(updatedUser)
                .addOnSuccessListener(aVoid -> successLiveData.postValue(true))
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    // ===================== GET USER (callback style) =====================
    public void getUser(String userId, final OnUserLoadedListener listener) {
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        listener.onUserLoaded(user);
                    } else {
                        listener.onUserLoaded(null);
                    }
                })
                .addOnFailureListener(e -> listener.onUserLoaded(null));
    }

    // ===================== UPDATE USER NAME =====================
    public void updateUserName(String userId, String newName) {
        db.collection("users").document(userId)
                .update("name", newName)
                .addOnSuccessListener(aVoid -> {})
                .addOnFailureListener(Throwable::printStackTrace);
    }

    // ===================== INTERFACE CALLBACK =====================
    public interface OnUserLoadedListener {
        void onUserLoaded(User user);
    }


    // ===================== LOGOUT =====================
    public void logoutUser() {
        FirebaseAuth.getInstance().signOut();
    }
}
