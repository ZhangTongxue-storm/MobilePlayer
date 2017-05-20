package com.storm.mobileplayer.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.storm.mobileplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemVideoActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_system_video);
        ButterKnife.bind(this);
        setVideoPlayer();
        setListener();

    }

    private void setListener() {


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
        if (isFullScreen)
            // 全屏状态
            btnFullscreen.setBackgroundResource(R.drawable.full_screen_selector);
        else
            btnFullscreen.setBackgroundResource(R.drawable.unfull_screen_selector);

    }

    /**
     * 判断播放的状态
     */
    private void setPlayOrPauseState() {

        if (isPlayer)
            // 正在  播放
            btnPause.setBackgroundResource(R.drawable.pause_selector);
        else
            btnPause.setBackgroundResource(R.drawable.player_selector);


    }

    private void setVideoPlayer() {
        Uri uri = getIntent().getData();

        videoPlayer.setVideoURI(uri);
        videoPlayer.setMediaController(new MediaController(this));

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
}
