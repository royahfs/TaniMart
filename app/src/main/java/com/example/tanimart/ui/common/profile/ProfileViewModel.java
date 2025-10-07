package com.example.tanimart.ui.common.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.User;
import com.example.tanimart.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final UserRepository repo = new UserRepository();

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    // Load user dari Firestore berdasarkan UID aktif
    public void loadUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            userLiveData.setValue(null);
            return;
        }

        String uid = firebaseUser.getUid();
        repo.getUser(uid, user -> {
            if (user != null) {
                userLiveData.postValue(user);
            }
        });
    }

    // Tambahan opsional: bisa dipanggil kalau mau update langsung tanpa reload
    public void updateUserLocally(User updatedUser) {
        userLiveData.postValue(updatedUser);
    }
}
