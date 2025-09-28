package com.example.tanimart.ui.common.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tanimart.data.model.User;
import com.example.tanimart.data.repository.UserRepository;

public class LoginViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LoginViewModel(){
        userRepository = new UserRepository();
    }
    public void loginUser(String email, String password){
        userRepository.loginUser(email, password, userLiveData, errorLiveData);
    }
    public LiveData<User> getUserLiveData(){ return userLiveData;}
    public LiveData<String> getErrorLiveData(){ return errorLiveData;}
}
