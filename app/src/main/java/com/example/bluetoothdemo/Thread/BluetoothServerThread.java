package com.example.bluetoothdemo.Thread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

import com.example.bluetoothdemo.config.Appconfig;

import java.io.IOException;

/**
 * Created by Administrator on 2016/5/15.
 */
public class BluetoothServerThread extends Thread {
    private BluetoothServerSocket serverSocket = null;
    private BluetoothSocket bluetoothSocket;
    private MyBluetoothSocket myBluetoothSocket;
    private BluetoothAdapter mAdapter;

    public BluetoothServerThread(BluetoothAdapter mAdapter, MyBluetoothSocket myBluetoothSocket) {
        this.mAdapter = mAdapter;
        this.myBluetoothSocket = myBluetoothSocket;
        try {
            if (mAdapter.isEnabled()) {
                serverSocket = mAdapter.listenUsingRfcommWithServiceRecord(Appconfig.SERVICE_NAME, java.util.UUID.fromString(Appconfig.UUID));
            }
        } catch (IOException e) {
            Log.e("------------", "创建服务端失败！");
            Log.e("sss", e.toString());
        }
    }

    public interface MyBluetoothSocket {
        void getBluetoothSocket(BluetoothSocket bluetoothSocket);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if(!mAdapter.isEnabled()){
                    break;
                }
                bluetoothSocket = serverSocket.accept();
                while (bluetoothSocket != null) {//如果有设备连接了
                    myBluetoothSocket.getBluetoothSocket(bluetoothSocket);
                    serverSocket.close();//因为要只实现1对1聊天所以关闭服务端socket
                    break;
                }
            } catch (IOException e) {
                break;//如果有错误 结束循环
            }

        }

    }

    public void close() {
        try {
            serverSocket.close();
        } catch (IOException e) {
        }
    }


}
