package top.renk.tvtopbox;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import top.renk.tvtopbox.service.WorkService;
import top.renk.tvtopbox.ui.MainActivity;

/**
 * Created by pcdalao on 2017/11/27.
 *
 */

public class StartReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        //启动app
        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        //启动服务
        Intent s = new Intent(context, WorkService.class);
        context.startService(s);

    }
}
