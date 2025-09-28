package com.example.tanimart.ui.common.signup;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.User;
import com.example.tanimart.data.repository.UserRepository;

public class SignUpViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public SignUpViewModel() {
        userRepository = new UserRepository();
    }

    public void registerUser(String name,String email, String password, String role) {
        userRepository.registerUser(name, email, password, role, userLiveData, errorLiveData);
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
}
