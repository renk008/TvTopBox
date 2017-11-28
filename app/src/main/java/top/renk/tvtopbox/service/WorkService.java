package top.renk.tvtopbox.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import top.renk.tvtopbox.rxbus.MySubscribe;
import top.renk.tvtopbox.rxbus.RxBus;
import top.renk.tvtopbox.rxbus.ThreadMode;
import top.renk.tvtopbox.thread.ReadThread;

/**
 * Created by renk on 2017/11/27.
 *
 */

public class WorkService extends Service {

    @Override
    public void onCreate() {
        RxBus.get().register(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllMedia();
            }
        }).start();
        Log.e("renk","onCreateService");
    }

    @MySubscribe(code = 1000)
    public void getReadStart(boolean isOpen) {
        if (isOpen) {
            new ReadThread().setOpen(true).start();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getAllMedia() {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor c = this.getContentResolver().query(uri, null, null, null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int vidsCount = 0;
        Log.e("renk","getAllMedia");
        if (c != null) {
            Log.e("renk","Cursor");
            vidsCount = c.getCount();
            Log.e("renk","CursorCount："+vidsCount);
            while (c.moveToNext()) {
                String[] s = c.getColumnNames();
                for (int i = 0; i < s.length; i++) {
                    Log.e("renk", s[i] + " =====" + c.getString(i));
                }
                Log.e("renk", c.getString(0));
            }
            c.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RxBus.get().unRegister(this);
    }
}
