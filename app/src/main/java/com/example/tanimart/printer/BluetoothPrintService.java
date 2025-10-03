package com.example.tanimart.printer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrintService {
    private static final String TAG = "BluetoothPrintService";
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket socket;
    private OutputStream out;

    // Umum: SPP UUID
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public BluetoothPrintService() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Helper: cek permission BLUETOOTH_CONNECT untuk Android 12+
    private boolean hasBluetoothConnectPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Connect ke device via macAddress (harus sudah dipair).
     * Sekarang menerima Context untuk mengecek permission sebelum memanggil API yang butuh izin.
     */
    public boolean connect(String macAddress, Context context) {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth adapter is null");
            return false;
        }

        if (!hasBluetoothConnectPermission(context)) {
            Log.e(TAG, "Missing BLUETOOTH_CONNECT permission");
            return false;
        }

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);
            socket = device.createRfcommSocketToServiceRecord(SPP_UUID);

            // cancel discovery sebelum connect
            try {
                bluetoothAdapter.cancelDiscovery();
            } catch (SecurityException se) {
                Log.w(TAG, "cancelDiscovery SecurityException: " + se.getMessage());
            }

            // connect dapat melempar IOException atau SecurityException
            try {
                socket.connect();
            } catch (SecurityException se) {
                Log.e(TAG, "connect SecurityException: " + se.getMessage());
                close();
                return false;
            }

            try {
                out = socket.getOutputStream();
            } catch (SecurityException | IOException e) {
                Log.e(TAG, "getOutputStream error: " + e.getMessage());
                close();
                return false;
            }

            return true;
        } catch (IllegalArgumentException iae) {
            Log.e(TAG, "Invalid MAC address: " + iae.getMessage());
            close();
            return false;
        } catch (IOException e) {
            Log.e(TAG, "connect io error: " + e.getMessage());
            close();
            return false;
        } catch (SecurityException se) {
            Log.e(TAG, "SecurityException: " + se.getMessage());
            close();
            return false;
        } catch (Exception ex) {
            Log.e(TAG, "unexpected error: " + ex.getMessage());
            close();
            return false;
        }
    }

    public boolean write(byte[] data) {
        if (out == null) return false;
        try {
            out.write(data);
            out.flush();
            return true;
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "write error: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (out != null) out.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        socket = null;
        out = null;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }
}
