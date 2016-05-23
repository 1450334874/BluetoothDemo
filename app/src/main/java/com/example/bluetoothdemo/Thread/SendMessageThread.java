package com.example.bluetoothdemo.Thread;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2016/5/23.
 */
public class SendMessageThread extends Thread {
    private OutputStream outputStream;
    private InputStream inputStream;
    private Get get;
    public SendMessageThread(OutputStream outputStream,InputStream inputStream,Get get){
        this.outputStream = outputStream;
        this.inputStream = inputStream;
        this.get = get;
    }


    @Override
    public void run() {
        byte[] bytes = new byte[1024];
        while (true){
            try {
                inputStream.read(bytes);// 读取发送过来的消息
                get.getData(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void write(String s){
        try {
            outputStream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface  Get{
        void getData(byte[] bytes);
    }
}
