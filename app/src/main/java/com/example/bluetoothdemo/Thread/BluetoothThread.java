package com.example.bluetoothdemo.Thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.bluetoothdemo.config.Appconfig;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2016/5/15.
 */
public class BluetoothThread extends Thread {
    private BluetoothSocket bluetoothSocket;
    private BluetoothAdapter bluetoothAdapter;
    private MyBluetoothSocket myBluetoothSocket;

    public BluetoothThread(BluetoothDevice device, BluetoothAdapter bluetoothAdapter, MyBluetoothSocket myBluetoothSocket) {
        this.myBluetoothSocket = myBluetoothSocket;
        this.bluetoothAdapter = bluetoothAdapter;
        try {
            bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Appconfig.UUID));
        } catch (IOException e) {

        }

    }
    public interface MyBluetoothSocket {
        void getBluetoothSocket(BluetoothSocket bluetoothSocket);
    }

    @Override
    public void run() {
        bluetoothAdapter.cancelDiscovery();//取消搜索  否则连接时可能导致失败
        try {
            bluetoothSocket.connect();//连接
            myBluetoothSocket.getBluetoothSocket(bluetoothSocket);
        } catch (IOException e) {
            try {
                bluetoothSocket.close();
            } catch (IOException e1) {

            }
        }
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) { }
    }
}
