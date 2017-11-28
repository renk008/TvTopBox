package top.renk.tvtopbox.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import top.renk.tvtopbox.R;
import top.renk.tvtopbox.adapter.HomeTvAdapter;
import top.renk.tvtopbox.app.MyApplication;
import top.renk.tvtopbox.rxbus.RxBus;
import top.renk.tvtopbox.service.WorkService;
import top.renk.tvtopbox.widget.CustomRecyclerView;

public class MainActivity extends AppCompatActivity {


    private byte[] writeBuffer;
    private byte[] readBuffer;
    private int retval;
    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // 保持常亮的屏幕的状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startService(new Intent(this, WorkService.class));
        initData();
        initListener();
    }

    private void initData() {


        MyApplication.setDriver(new CH34xUARTDriver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, MyApplication.ACTION_USB_PERMISSION));
        if (!MyApplication.getDriver().UsbFeatureSupported())// 判断系统是否支持USB HOST
        {
            Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("提示")
                    .setMessage("您的手机不支持USB HOST，请更换其他手机再试！")
                    .setPositiveButton("确认",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0,
                                                    int arg1) {
                                    System.exit(0);
                                }
                            }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        writeBuffer = new byte[512];
        readBuffer = new byte[512];
    }

    private void initListener() {
        retval = MyApplication.getDriver().ResumeUsbList();

        if (!isOpen) {
            if (retval < 0) {// ResumeUsbList方法用于枚举CH34X设备以及打开相关设备
                Toast.makeText(MainActivity.this, "打开设备失败!",
                        Toast.LENGTH_SHORT).show();
                MyApplication.getDriver().CloseDevice();
            } else if (retval == 0) {
                if (!MyApplication.getDriver().UartInit()) {//对串口设备进行初始化操作
                    Toast.makeText(MainActivity.this, "设备初始化失败!",
                            Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "打开设备失败!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this, "打开设备成功!",
                        Toast.LENGTH_SHORT).show();
                isOpen = true;
                //开启线程读
                RxBus.get().send(1000,isOpen);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("未授权限");
                builder.setMessage("确认退出吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                        System.exit(0);
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




}
