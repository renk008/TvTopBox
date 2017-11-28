package top.renk.tvtopbox.thread;

import android.util.Log;

import top.renk.tvtopbox.app.MyApplication;
import top.renk.tvtopbox.rxbus.RxBus;

/**
 * Created by renk on 2017/11/28.
 */

public class ReadThread extends Thread {

    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public ReadThread setOpen(boolean open) {
        isOpen = open;
        return this;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4096];
        Log.e("renk", "startThread");
        while (isOpen) {
            int length = MyApplication.getDriver().ReadData(buffer, 4096);
            if (length > 0) {
                //String recv = new String(buffer, 0, length);
                String string = toHexString(buffer, length);
                if (string.startsWith("MP")) {
                    String content = string.substring(2, string.length() - 1);
                    if (string.endsWith("L")) {
                        //循环播放
                    } else if (string.endsWith("S")) {
                        //停止播放
                    } else if (string.endsWith("P")) {
                        //暂停播放
                    } else {
                        //播放一次
                    }
                } else if (string.startsWith("MI")) {
                    //发送POST
                } else {
                    //
                }

                RxBus.get().send(22222, string);
            }
        }
    }

    private String toHexString(byte[] arg, int length) {
        StringBuilder result = new StringBuilder();
        if (arg != null) {
            for (int i = 0; i < length; i++) {
                result.append(Integer.toHexString(
                        arg[i] < 0 ? arg[i] + 256 : arg[i]).length() == 1 ? "0"
                        + Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])
                        : Integer.toHexString(arg[i] < 0 ? arg[i] + 256
                        : arg[i])).append(" ");
            }
            return result.toString();
        }
        return "";
    }


}
