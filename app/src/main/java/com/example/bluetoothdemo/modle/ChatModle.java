package com.example.bluetoothdemo.modle;

/**
 * Created by Administrator on 2016/5/17.
 */
public class ChatModle {
    private static int right = 0;
    private static int left = 1;
    private String message;//消息
    private type t;//类型

    public ChatModle(String message, type t) {
        this.message = message;
        this.t = t;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public type getT() {
        return t;
    }

    public void setT(type t) {
        this.t = t;
    }


    public enum type {
        right, left
    }
}
