package top.renk.tvtopbox.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.listener.StandardVideoAllCallBack;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.wch.ch34xuartdriver.CH34xUARTDriver;
import top.renk.tvtopbox.R;
import top.renk.tvtopbox.app.MyApplication;
import top.renk.tvtopbox.rxbus.MySubscribe;
import top.renk.tvtopbox.rxbus.RxBus;
import top.renk.tvtopbox.rxbus.ThreadMode;
import top.renk.tvtopbox.service.WorkService;
import top.renk.tvtopbox.thread.ReadThread;
import top.renk.tvtopbox.video.VideoPlayer;

public class PlayActivity extends AppCompatActivity implements StandardVideoAllCallBack {

    @BindView(R.id.video_player)
    VideoPlayer videoPlayer;
    @BindView(R.id.player_image)
    ImageView imageView;

    String url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
    List<String> paths;

    boolean isLoop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        // 保持常亮的屏幕的状态
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        RxBus.get().register(this);
        startService(new Intent(this, WorkService.class));
        init();
    }

    private void init() {

        paths = new ArrayList<>();
        //增加封面
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(R.drawable.default_poster);
        videoPlayer.setThumbImageView(imageView);
        videoPlayer.setStandardVideoAllCallBack(this);
//        startPlay();
    }

    @MySubscribe(code = 33333, threadMode = ThreadMode.MAIN)
    public void getScanSdcardPath(List<String> list) {
        if (!list.isEmpty()) {
            paths.clear();
            paths.addAll(list);
        }
    }

    @MySubscribe(code = 9527, threadMode = ThreadMode.MAIN)
    public void exitOut() {
        PlayActivity.this.finish();
        System.exit(0);
    }

    @MySubscribe(code = 22222, threadMode = ThreadMode.MAIN)
    public void playOnce(String content) {
            if (!paths.isEmpty()) {
                for (String path : paths) {
                    if (getName(path).startsWith(content)) {
                        url = path;
                        isLoop = true;
                        startPlay();
                        break;
                    }
                }
            }
    }
    @MySubscribe(code = 22223, threadMode = ThreadMode.MAIN)
    public void stop(String content) {
        videoPlayer.onVideoReset();
    }
    @MySubscribe(code = 22224, threadMode = ThreadMode.MAIN)
    public void pause(String content) {
        videoPlayer.onVideoPause();
    }

    private String getName(String string) {
        return string.substring(string.indexOf("/") + 1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayer.onVideoPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.onVideoResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.release();
        RxBus.get().unRegister(this);
    }

    @Override
    public void onBackPressed() {
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
        new Handler().postDelayed(new Runnable() {
            @SuppressLint("PrivateResource")
            @Override
            public void run() {
                finish();
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        }, 250);
    }

//    @MySubscribe(code = 44444)
    private void startPlay() {
        videoPlayer.setUp(url, true, "");
        videoPlayer.startPlayLogic();
    }


    @Override
    public void onPrepared(String url, Object... objects) {

    }

    @Override
    public void onClickStartIcon(String url, Object... objects) {

    }

    @Override
    public void onClickStartError(String url, Object... objects) {

    }

    @Override
    public void onClickStop(String url, Object... objects) {

    }

    @Override
    public void onClickStopFullscreen(String url, Object... objects) {

    }

    @Override
    public void onClickResume(String url, Object... objects) {

    }

    @Override
    public void onClickResumeFullscreen(String url, Object... objects) {

    }

    @Override
    public void onClickSeekbar(String url, Object... objects) {

    }

    @Override
    public void onClickSeekbarFullscreen(String url, Object... objects) {

    }

    @Override
    public void onAutoComplete(String url, Object... objects) {
        if(isLoop){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //
                    startPlay();
                }
            }, 1000);
        }
    }

    @Override
    public void onEnterFullscreen(String url, Object... objects) {

    }

    @Override
    public void onQuitFullscreen(String url, Object... objects) {

    }

    @Override
    public void onQuitSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onEnterSmallWidget(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekVolume(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekPosition(String url, Object... objects) {

    }

    @Override
    public void onTouchScreenSeekLight(String url, Object... objects) {

    }

    @Override
    public void onPlayError(String url, Object... objects) {
        //TODO
    }

    @Override
    public void onClickStartThumb(String url, Object... objects) {

    }

    @Override
    public void onClickBlank(String url, Object... objects) {

    }

    @Override
    public void onClickBlankFullscreen(String url, Object... objects) {

    }
}
