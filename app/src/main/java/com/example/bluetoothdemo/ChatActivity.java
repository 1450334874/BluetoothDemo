package com.example.bluetoothdemo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bluetoothdemo.adapter.ChatListAdapter;
import com.example.bluetoothdemo.modle.ChatModle;
import com.example.bluetoothdemo.service.BluetoothService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/16.
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;
    private EditText message_et;
    private Button send_btn;
    private BluetoothService.MyIBinder myIBinder;
    private BluetoothService bluetoothService;
    private List<ChatModle> modleList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    newData(new ChatModle((String) msg.obj, ChatModle.type.left));
                    break;
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myIBinder = (BluetoothService.MyIBinder) service;
            bluetoothService = myIBinder.getService();
            bluetoothService.setGetdata(new BluetoothService.Getdata() {
                @Override
                public void getData(byte[] bytes) {
                    String s = new String(bytes);
                    Message message = new Message();
                    message.what = 1;
                    message.obj = s;
                    handler.sendMessage(message);
                }
            });
            myIBinder.startChat();//开启聊天线程
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra("device_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = new Intent(ChatActivity.this, BluetoothService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        initView();
    }


    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.chat_list);
        message_et = (EditText) findViewById(R.id.chat_message_et);
        send_btn = (Button) findViewById(R.id.chat_message_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message_et.getText().toString().equals("")) {//如果输入框不为空
                    myIBinder.sendMess(message_et.getText().toString());
                    newData(new ChatModle(message_et.getText().toString(), ChatModle.type.right));
                    message_et.setText("");
                }
            }
        });
        chatListAdapter = new ChatListAdapter(modleList);
        recyclerView.setAdapter(chatListAdapter);

    }

    private void newData(ChatModle modle){
        modleList.add(modle);
        chatListAdapter.notifyItemRangeChanged(modleList.size(), modleList.size()+1);
//        chatListAdapter.notifyItemInserted(modleList.size());
//        modleList.add(modleList.size(), modle);
//        chatListAdapter.notifyItemRangeChanged(modleList.size(), chatListAdapter.getItemCount());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }
}
