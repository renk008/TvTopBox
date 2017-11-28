package top.renk.tvtopbox.service;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import top.renk.tvtopbox.R;
import top.renk.tvtopbox.app.MyApplication;
import top.renk.tvtopbox.rxbus.MySubscribe;
import top.renk.tvtopbox.rxbus.RxBus;
import top.renk.tvtopbox.rxbus.ThreadMode;
import top.renk.tvtopbox.thread.ReadThread;
import top.renk.tvtopbox.ui.PlayActivity;

/**
 * Created by renk on 2017/11/27
 * .
 */

public class WorkService extends Service {
    private int retval;
    private boolean isOpen;

    @Override
    public void onCreate() {
        initData();
        initListener();
        getAllMedia();
        Log.e("renk", "onCreateService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unRegister(this);
    }

    private void initData() {
        MyApplication.setDriver(new CH34xUARTDriver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, MyApplication.ACTION_USB_PERMISSION));
        if (!MyApplication.getDriver().UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("您的设备不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    RxBus.get().send(9527);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void initListener() {
        RxBus.get().register(this);
        retval = MyApplication.getDriver().ResumeUsbList();
        if (!isOpen) {
            if (retval < 0) {// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                Toast.makeText(this, "打开设备失败!", Toast.LENGTH_SHORT).show();
                MyApplication.getDriver().CloseDevice();
            } else if (retval == 0) {
                if (!MyApplication.getDriver().UartInit()) {//对串口设备进行初始化操作
                    Toast.makeText(this, "设备初始化失败!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "打开设备失败!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, "打开设备成功!", Toast.LENGTH_SHORT).show();
                isOpen = true;
                //开启线程读
                new ReadThread().setOpen(true).start();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("未授权限");
                builder.setMessage("确认退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RxBus.get().send(9527);
                    }
                });
                builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        } else {
            MyApplication.getDriver().CloseDevice();
            isOpen = false;
        }
    }

    public void getAllMedia() {
        String[] projection = {
                MediaStore.Video.Media.DATA,//：视频文件路径；
                MediaStore.Video.Media.DISPLAY_NAME,// : 视频文件名，如 testVideo.mp4
                MediaStore.Video.Media.TITLE,//: 视频标题 : testVideo
        };
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor c = this.getContentResolver().query(uri, projection,
                null, null, null);
        if (c != null && c.moveToFirst()) {
            List<String> paths = new ArrayList<>();
            do {
                String url = c.getString(0);
                paths.add(url);
            } while (c.moveToNext());
            Log.e("renk", paths.toString());
            RxBus.get().send(33333, paths);
            c.close();
        }
    }

    private void writeString(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }
        byte[] to_send = toByteArray(content);
        //写数据，第一个参数为需要发送的字节数组，第二个参数为需要发送的字节长度，返回实际发送的字节长度
        int retval = MyApplication.getDriver().WriteData(to_send, to_send.length);
        if (retval < 0)
            Toast.makeText(this, "写失败!", Toast.LENGTH_SHORT).show();
    }

    private byte[] toByteArray(String arg) {
        if (arg != null) {
            /* 1.先去除String中的' '，然后将String转换为char数组 */
            char[] NewArray = new char[1000];
            char[] array = arg.toCharArray();
            int length = 0;
            for (char anArray : array) {
                if (anArray != ' ') {
                    NewArray[length] = anArray;
                    length++;
                }
            }
            /* 将char数组中的值转成一个实际的十进制数组 */
            int EvenLength = (length % 2 == 0) ? length : length + 1;
            if (EvenLength != 0) {
                int[] data = new int[EvenLength];
                data[EvenLength - 1] = 0;
                for (int i = 0; i < length; i++) {
                    if (NewArray[i] >= '0' && NewArray[i] <= '9') {
                        data[i] = NewArray[i] - '0';
                    } else if (NewArray[i] >= 'a' && NewArray[i] <= 'f') {
                        data[i] = NewArray[i] - 'a' + 10;
                    } else if (NewArray[i] >= 'A' && NewArray[i] <= 'F') {
                        data[i] = NewArray[i] - 'A' + 10;
                    }
                }
                /* 将 每个char的值每两个组成一个16进制数据 */
                byte[] byteArray = new byte[EvenLength / 2];
                for (int i = 0; i < EvenLength / 2; i++) {
                    byteArray[i] = (byte) (data[i * 2] * 16 + data[i * 2 + 1]);
                }
                return byteArray;
            }
        }
        return new byte[]{};
    }
}
