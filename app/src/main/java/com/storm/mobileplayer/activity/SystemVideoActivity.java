package com.storm.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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

    public static final int PROGRESS = 1;

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
    private int position;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    int currentPosition = videoPlayer.getCurrentPosition();
                    sbDuration.setProgress(currentPosition);
                    tvDuration.setText(timeUtils.stringForTime(currentPosition));
                    // updata systemtiem
                    tvSystemTime.setText(timeUtils.getSystemTime());

                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    LogUtils.d("循环发送消息");
                    break;
            }
        }
    };
    private Uri uri;


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

    private void setListener() {


        //播放上一个
        btnPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreVideo();
            }
        });

        //点击下一个
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextVideo();
            }
        });

        sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mHandler.removeCallbacksAndMessages(null);
                    isPlayer = false;
                    setPlayOrPauseState();
                    videoPlayer.seekTo(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlayer = true;
                setPlayOrPauseState();
                mHandler.sendEmptyMessage(PROGRESS);

            }
        });

        /*
         *
         * 获取系统的声音
         */
        sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                LogUtils.d("点击暂停按钮");
            }
        });


    }

    /**
     * 播放上一个视频
     */
    private void playPreVideo() {

        position -= 1;
        if (position < 0) {
            // Toast.makeText(this, "已经到第一个了", Toast.LENGTH_SHORT).show();
            finish();
        }

        LocalVideoBean videoBean = videoLists.get(position);
        videoPlayer.setVideoPath(videoBean.getData());
        tvVideoName.setText(videoBean.getName());
        setButtonState();
    }

    /**
     * 播放下一个视频
     */
    private void playNextVideo() {
        position += 1;
        if (position > videoLists.size() - 1) {
            Toast.makeText(this, "已经到了最后一个了", Toast.LENGTH_SHORT).show();
            finish();
        }
        LocalVideoBean videoBean = videoLists.get(position);
        videoPlayer.setVideoPath(videoBean.getData());
        tvVideoName.setText(videoBean.getName());
        setButtonState();

    }

    /**
     * 设置点击状态
     */
    private void setButtonState() {
        if (videoLists != null && videoLists.size() > 0) {
            setEnabled(true);

            if (position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
                btnPre.setEnabled(false);

            }

            if (position == videoLists.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);

            }
        } else if (uri != null) {
            //下个和上个都不可以点击
            setEnabled(false);

        }

    }


    private void setEnabled(boolean b) {

        if (b) {
            btnPre.setBackgroundResource(R.drawable.pre_selector);
            btnNext.setBackgroundResource(R.drawable.next_selector);

        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);

        }

        btnPre.setEnabled(b);
        btnNext.setEnabled(b);

    }


    private void initData() {
        timeUtils = new TimeUtils();
        videoLists = (ArrayList<LocalVideoBean>) getIntent().getSerializableExtra("videoList");
        position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            uri = Uri.parse(videoLists.get(position).getData());
            videoPlayer.setVideoURI(uri);

        }
        // 设置系统时间
        tvSystemTime.setText(timeUtils.getSystemTime());
        tvVideoName.setText(videoLists.get(position).getName());
        // 设置电池的状态
        setBatteryState();


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
     * @param level 电量
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

        LogUtils.d("点击播放暂停按钮");
        if (isPlayer) {
            // 正在  播放
            btnPause.setBackgroundResource(R.drawable.pause_selector);
            mHandler.sendEmptyMessage(PROGRESS);
            videoPlayer.start();
        } else {
            btnPause.setBackgroundResource(R.drawable.player_selector);
            mHandler.removeCallbacksAndMessages(null);

            videoPlayer.pause();

        }

    }

    private void setVideoPlayer() {

        // 前期准备
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int duration = videoPlayer.getDuration();
                tvVideoTime.setText(timeUtils.stringForTime(duration));
                sbDuration.setMax(duration);

                videoPlayer.start();

                mHandler.sendEmptyMessage(PROGRESS);
            }
        });

        //监听异常
        videoPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }

        });
        // 监听完成
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

        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
        }
        batteryReceiver = null;

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }


}
