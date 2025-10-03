package com.example.tanimart.ui.kasir.transaksi.pembayaran;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tanimart.data.repository.PrintRepository;

public class PrintViewModel extends AndroidViewModel {
    private final PrintRepository repository;
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    public PrintViewModel(@NonNull Application application) {
        super(application);
        repository = new PrintRepository();
    }

    public LiveData<Boolean> getIsConnected() {
        return isConnected;
    }

    public void connectPrinter(String macAddress, Context context) {
        repository.connect(macAddress, context, success -> isConnected.postValue(success));
    }

    public void printReceipt(byte[] data) {
        repository.print(data, success -> {
            // Bisa kasih livedata status printing kalau mau
        });
    }

    public void disconnectPrinter() {
        repository.disconnect();
    }
}
