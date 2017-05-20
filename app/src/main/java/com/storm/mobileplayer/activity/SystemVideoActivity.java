package com.storm.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.bean.LocalVideoBean;
import com.storm.mobileplayer.utils.LogUtils;
import com.storm.mobileplayer.utils.TimeUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemVideoActivity extends AppCompatActivity {

    public static final String TAG = "SystemVideoActivity";

    @BindView(R.id.video_player)
    VideoView videoPlayer;
    @BindView(R.id.tv_video_name)
    TextView tvVideoName;
    @BindView(R.id.iv_battery)
    ImageView ivBattery;
    @BindView(R.id.tv_system_time)
    TextView tvSystemTime;
    @BindView(R.id.btn_voice)
    Button btnVoice;
    @BindView(R.id.sb_voice)
    SeekBar sbVoice;
    @BindView(R.id.btn_switch)
    Button btnSwitch;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.sb_duration)
    SeekBar sbDuration;
    @BindView(R.id.tv_video_time)
    TextView tvVideoTime;
    @BindView(R.id.btn_exit)
    Button btnExit;
    @BindView(R.id.btn_pre)
    Button btnPre;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_fullscreen)
    Button btnFullscreen;

    private boolean isPlayer = true;            //当前视频是否播放
    private boolean isFullScreen = false;       // 当前视频是否全屏
    private ArrayList<LocalVideoBean> videoLists;

    private TimeUtils timeUtils;
    private BroadcastReceiver batteryReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_system_video);
        ButterKnife.bind(this);
        initData();
        setVideoPlayer();
        setListener();

    }

    private void initData() {

        videoLists = (ArrayList<LocalVideoBean>) getIntent().getSerializableExtra("videoList");
        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            videoPlayer.setVideoURI(Uri.parse(videoLists.get(position).getData()));
            videoPlayer.start();
        }

        timeUtils = new TimeUtils();

        // 设置电池的状态
        setBatteryState();
        int duration = videoPlayer.getDuration();
        LogUtils.d("视屏的总时长" + duration);

        tvVideoTime.setText(timeUtils.stringForTime(duration));

    }

    /**
     * 设置电池的状态
     */
    private void setBatteryState() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                int level = intent.getIntExtra("level", 0);
                setvideoBatterState(level);

            }
        };

        registerReceiver(batteryReceiver, intentFilter);

    }

    /**
     * 设置电池的状态
     *
     * @param level
     */
    private void setvideoBatterState(int level) {

        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level > 0 && level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (level > 10 && level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (level > 20 && level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (level > 40 && level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);

        } else if (level > 60 && level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);

        } else if (level > 80 && level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);

        }

    }

    private void setListener() {


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlayer.stopPlayback();
                finish();
            }
        });
        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFullScreen = !isFullScreen;
                setScreenState();
            }
        });


        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlayer = !isPlayer;
                setPlayOrPauseState();
            }
        });


    }

    /**
     * 设置切换是否全屏的状态
     */
    private void setScreenState() {

        if (isFullScreen) {
            // 全屏状态
            btnFullscreen.setBackgroundResource(R.drawable.full_screen_selector);

        } else {
            btnFullscreen.setBackgroundResource(R.drawable.unfull_screen_selector);
        }
    }

    /**
     * 判断播放的状态
     */
    private void setPlayOrPauseState() {

        if (isPlayer) {
            // 正在  播放
            btnPause.setBackgroundResource(R.drawable.pause_selector);
            videoPlayer.start();
        } else {
            btnPause.setBackgroundResource(R.drawable.player_selector);
            videoPlayer.pause();

        }

    }

    private void setVideoPlayer() {

        // videoPlayer.setMediaController(new MediaController(this));

        // 前期准备
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                videoPlayer.start();
            }
        });

        videoPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }

        });

        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryReceiver);
        batteryReceiver = null;
    }


}
