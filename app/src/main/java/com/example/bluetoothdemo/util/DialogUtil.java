package com.example.bluetoothdemo.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

/**
 * Created by CHEN on 2016/3/17.
 */
public class DialogUtil {
    private static AlertDialog dialog;
    private static ProgressDialog progressDialog;//进度条dilaog

    /**
     * 显示提示dialog
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showToastDialog(Context context, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//设置标题
        builder.setMessage(msg);//设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        dialog.setCancelable(false);//返回键不能关闭
        dialog.show();
    }
    
    
    /**
     * 显示提示dialog
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showDialog(Context context, String title, String msg,DialogInterface.OnClickListener yes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//设置标题
        builder.setMessage(msg);//设置内容
        builder.setPositiveButton("确定", yes);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        dialog.setCancelable(false);//返回键不能关闭
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    /**
     * 显示提示dialog
     *
     * @param context
     * @param title
     * @param msg
     */
    public static void showYesDialog(Context context, String title, String msg,DialogInterface.OnClickListener yes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);//设置标题
        builder.setMessage(msg);//设置内容
        builder.setPositiveButton("确定", yes);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);//点击屏幕不消失
        dialog.setCancelable(false);//返回键不能关闭
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }


    /**
     *显示进度条dialog
     */
    public static void showPressDialog(Context context,String msg){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
        progressDialog.show();
    }
    


    /**
     * 使dialog消失
     */
    public static void dimissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }

    }


    /**
     * 是进度条dialog消失
     */
    public static void  dimissProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}