package com.example.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bluetoothdemo.Thread.BluetoothServerThread;
import com.example.bluetoothdemo.Thread.BluetoothThread;
import com.example.bluetoothdemo.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static int OPEN_BLUETOOTH = 1;//打开蓝牙
    private static int REQUEST_DISCOVERABLE = 2;//
    private List<BluetoothDevice> mBluetoothDeviceList;//存放搜索到的设备
    private BluetoothAdapter mAdapter;
    private RecyclerView deviceList;//显示设备列表
    private TextView hinttv;//提示
    private Button searchbtn;
    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothServerThread bluetoothServerThread;//监听连接的线程
    private BluetoothThread bluetoothThread;//主动连接别人的线程


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1://连接服务器端成功
                    Toast.makeText(MainActivity.this, "连接服务器端成功", Toast.LENGTH_SHORT).show();
                    break;
                case 2://客户端连接成功
                    Toast.makeText(MainActivity.this, "客户端连接成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        registerBlueReceiver();
        initBluetoothAdapter();

        mBluetoothDeviceList = new ArrayList<>();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        deviceList = (RecyclerView) findViewById(R.id.main_recyclerview);
        hinttv = (TextView) findViewById(R.id.main_hint);
        searchbtn = (Button) findViewById(R.id.main_search);
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.setAdapter(mDeviceListAdapter = new DeviceListAdapter(mBluetoothDeviceList));
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hinttv.setVisibility(View.GONE);
                deviceList.setVisibility(View.VISIBLE);
                mAdapter.startDiscovery();//开始搜索
            }
        });
        mDeviceListAdapter.setItemClick(new DeviceListAdapter.ItemClick() {
            @Override
            public void onItemClick(BluetoothDevice device) {
                if(bluetoothThread!=null){
                    bluetoothThread.cancel();
                }
                bluetoothThread = new BluetoothThread(device, mAdapter, new BluetoothThread.MyBluetoothSocket() {
                    @Override
                    public void getBluetoothSocket(BluetoothSocket bluetoothSocket) {
                        if (bluetoothSocket != null) {
                            handler.sendEmptyMessage(1);
                        }
                    }
                });
                bluetoothThread.start();
            }
        });

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
     * 初始化蓝牙适配器
     */
    private void initBluetoothAdapter() {
        //获取蓝牙适配器
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mAdapter.isEnabled()) {//如果适配器不可用   没打开蓝牙
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enabler.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);//设置蓝牙可被其他设备搜索到的时间（最多300秒）
            startActivityForResult(enabler, OPEN_BLUETOOTH);
        }
    }


    /**
     * 注册蓝牙相关的广播
     */
    private void registerBlueReceiver() {
        //搜索到新蓝牙设备广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
        //搜索结束广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(MainActivity.this, "请手动开启蓝牙！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "蓝牙开启成功！", Toast.LENGTH_SHORT).show();
                //设置蓝牙可以被搜索
                Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(enabler, REQUEST_DISCOVERABLE);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //找到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {

                    Log.e(TAG, "find device:" + device.getName()
                            + device.getAddress());
                    mBluetoothDeviceList.add(device);
                    mDeviceListAdapter.notifyItemRangeChanged(1, 2);
                }
            }
            //搜索完成
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                Log.e(TAG, "搜索完成");
                if (mBluetoothDeviceList.size() == 0) {
                    Log.e(TAG, "没有设备");
                }
            }
        }
    };
}
