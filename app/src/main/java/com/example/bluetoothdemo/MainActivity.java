package com.example.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.example.bluetoothdemo.service.BluetoothService;
import com.example.bluetoothdemo.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<BluetoothDevice> mBluetoothDeviceList;//存放搜索到的设备

    private RecyclerView deviceList;//显示设备列表
    private TextView hinttv;//提示
    private Button searchbtn;
    private DeviceListAdapter mDeviceListAdapter;
    private BluetoothService.MyIBinder myIBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myIBinder = (BluetoothService.MyIBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerBlueReceiver();
        mBluetoothDeviceList = new ArrayList<>();
        initView();
        initService();

    }

    private void initService() {
        Intent startService = new Intent(MainActivity.this, BluetoothService.class);
        startService(startService);
        bindService(startService, serviceConnection, BIND_AUTO_CREATE);
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
                if (myIBinder.startSearch()) {
                    hinttv.setVisibility(View.GONE);
                    deviceList.setVisibility(View.VISIBLE);
                    mBluetoothDeviceList.clear();
                    List<BluetoothDevice> deviceList  = myIBinder.getDevice();
                    for (BluetoothDevice d : deviceList){
                        mBluetoothDeviceList.add(d);
                    }
                    mDeviceListAdapter.notifyDataSetChanged();
                }
            }
        });
        mDeviceListAdapter.setItemClick(new DeviceListAdapter.ItemClick() {
            @Override
            public void onItemClick(BluetoothDevice device) {
                DialogUtil.showDialog(MainActivity.this,"状态","正在连接。。。",null);
                myIBinder.connectionDevice(device);
            }
        });


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
        unbindService(serviceConnection);
        super.onDestroy();
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
                    boolean isHave = false;
                    for(BluetoothDevice mDevice : mBluetoothDeviceList){
                        if(mDevice.getAddress().equals(device.getAddress())){
                            isHave = true;
                        }
                    }
                    if(!isHave){
                        mBluetoothDeviceList.add(device);
                        mDeviceListAdapter.notifyItemRangeChanged(mBluetoothDeviceList.size(), mBluetoothDeviceList.size()+1);
                    }

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
