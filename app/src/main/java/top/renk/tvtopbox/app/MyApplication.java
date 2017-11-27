package top.renk.tvtopbox.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.hardware.usb.UsbManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import cn.wch.ch34xuartdriver.CH34xUARTDriver;

/**
 * Created by renk on 2017/11/27.
 */

public class MyApplication extends Application {
    private static volatile MyApplication myApplication;
    @SuppressLint("StaticFieldLeak")
    private static CH34xUARTDriver driver;
    public static final String ACTION_USB_PERMISSION = "cn.wch.wchusbdriver.USB_PERMISSION";

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
    }

    public static CH34xUARTDriver getDriver() {
        return driver;
    }

    public static void setDriver(CH34xUARTDriver driver) {
        MyApplication.driver = driver;
    }

    public static MyApplication getMyApplication() {
        return myApplication;
    }


}
