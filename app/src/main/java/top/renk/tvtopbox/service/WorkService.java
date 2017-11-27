package top.renk.tvtopbox.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by renk on 2017/11/27.
 *
 */

public class WorkService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getAllMedia(){
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor c = this.getContentResolver().query(uri, null, null, null,
                MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            while (c.moveToNext()) {
                String[] s = c.getColumnNames();
                for (int i = 0; i < s.length; i++) {
                    Log.e("video", " " + s[i] + " =====" + c.getString(i));
                }
                Log.e("VIDEO", c.getString(0));
            }
            c.close();
        }
    }
}
