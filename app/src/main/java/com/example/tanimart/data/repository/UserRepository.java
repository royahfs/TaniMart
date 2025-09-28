package com.example.tanimart.data.repository;


import androidx.lifecycle.MutableLiveData;

import com.example.tanimart.data.firebase.FirebaseAuthHelper;
import com.example.tanimart.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {
    private final FirebaseAuthHelper authHelper;
    private final FirebaseFirestore db;


    public UserRepository(){
        authHelper = new FirebaseAuthHelper();
        db = FirebaseFirestore.getInstance();
    }

    //Login User
    public void loginUser(String email, String password,
                          MutableLiveData<User> userLiveData,
                          MutableLiveData<String> errorLiveData){
        authHelper.login(email, password, new FirebaseAuthHelper.LoginCallback(){
            @Override
            public void onSuccess(FirebaseUser firebaseUser){
                // Ambil detail user dari Firestore
                db.collection("users").document(firebaseUser.getUid())
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()){
                                User user = documentSnapshot.toObject(User.class);
                                userLiveData.postValue(user);
                            } else {
                                errorLiveData.postValue("User tidak ditemukan");
                            }
                            })
                        .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
            }

            @Override
            public void onFailure(String errorMessage){
                errorLiveData.postValue(errorMessage);
            }
        });
    }


    // Register
    public void registerUser(String name, String email, String password, String role,
                             MutableLiveData<User> userLiveData,
                             MutableLiveData<String> errorLiveData){
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    //rolenya dimana Roya?
                    String uid = authResult.getUser().getUid();

                    //simpan user ke Firestore (hanya email+role)
                    User newUser = new User(name, email, role);

                    db.collection("users").document(uid)
                            .set(newUser)
                            .addOnSuccessListener(aVoid -> userLiveData.postValue(newUser))
                            .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
                })
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }
}
