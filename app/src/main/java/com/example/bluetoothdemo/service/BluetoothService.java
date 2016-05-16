package com.example.bluetoothdemo.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetoothdemo.Thread.BluetoothServerThread;
import com.example.bluetoothdemo.Thread.BluetoothThread;

/**
 * Created by Administrator on 2016/5/15.
 */
public class BluetoothService extends Service {
    private BluetoothServerThread bluetoothServerThread;//监听连接的线程
    private BluetoothThread bluetoothThread;//主动连接别人的线程
    private BluetoothAdapter mAdapter;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1://连接服务器端成功
                    Toast.makeText(BluetoothService.this, "连接服务器端成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2://客户端连接成功
                    Toast.makeText(BluetoothService.this, "客户端连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case 3://打开蓝牙
                    openBluetooth();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyIBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        startServerThread();
        if (mAdapter.isEnabled()) {
            //设置蓝牙可以被搜索
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            enabler.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(enabler);
        }

    }

    /**
     * 打开服务线程
     */
    private void startServerThread() {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {//如果适配器不可用   没打开蓝牙
            openBluetooth();
        }
        bluetoothServerThread = new BluetoothServerThread(mAdapter, new BluetoothServerThread.MyBluetoothSocket() {
            @Override
            public void getBluetoothSocket(BluetoothSocket bluetoothSocket) {
                if (bluetoothSocket != null) {
                    handler.sendEmptyMessage(2);
                }
            }
        });
        bluetoothServerThread.start();//开始监听其他的客户端连接
    }

    /**
     * 打开蓝牙
     */
    private void openBluetooth() {
        Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        enabler.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//设置蓝牙可被其他设备搜索到的时间（最多300秒）
        enabler.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(enabler);
    }

    public class MyIBinder extends Binder {
        /**
         * 主动连接设备
         *
         * @param device
         */
        public void connectionDevice(BluetoothDevice device) {
            if (bluetoothThread != null) {
                bluetoothThread.cancel();
            }
            bluetoothThread = new BluetoothThread(device, mAdapter, new BluetoothThread.MyBluetoothSocket() {
                @Override
                public void getBluetoothSocket(BluetoothSocket bluetoothSocket) {
                    if (bluetoothSocket != null) {
//                        handler.sendEmptyMessage(1);
//                        Intent intent = new Intent(BluetoothService)
                    }
                }
            });
            bluetoothThread.start();
        }


        /**
         * 开始搜索
         */
        public boolean startSearch() {
            if (!mAdapter.isEnabled()) {//如果蓝牙没开
                openBluetooth();
                Toast.makeText(BluetoothService.this, "请打开蓝牙后重试！", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                if (!bluetoothServerThread.isAlive()) {
                    try {
                        bluetoothServerThread.start();//开始监听其他的客户端连接
                    } catch (Exception e) {
                    }
                }
                mAdapter.startDiscovery();//开始搜索
                return true;
            }
        }


    }


}
