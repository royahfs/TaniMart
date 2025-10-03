package com.example.tanimart.data.repository;

import android.content.Context;

import com.example.tanimart.printer.BluetoothPrintService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrintRepository {
    private final BluetoothPrintService btService;
    private final ExecutorService executor;

    public PrintRepository() {
        btService = new BluetoothPrintService();
        executor = Executors.newSingleThreadExecutor();
    }

    public void connect(String macAddress, Context context, PrintCallback callback) {
        executor.execute(() -> {
            boolean success = btService.connect(macAddress, context);
            if (callback != null) {
                callback.onResult(success);
            }
        });
    }

    public void print(byte[] data, PrintCallback callback) {
        executor.execute(() -> {
            boolean success = btService.write(data);
            if (callback != null) {
                callback.onResult(success);
            }
        });
    }

    public void disconnect() {
        executor.execute(btService::close);
    }

    public interface PrintCallback {
        void onResult(boolean success);
    }
}
