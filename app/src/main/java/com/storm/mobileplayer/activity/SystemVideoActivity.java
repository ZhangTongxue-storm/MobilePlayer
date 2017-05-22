package com.storm.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

    public static final int HIDE_MEDIACONTROLLER = 2;

    public static final int DEFAULT_SCREEN = 0;

    public static final int FULL_SCREEN = 1;

    @BindView(R.id.video_player)
    com.storm.mobileplayer.custom.VideoView videoPlayer;
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
    @BindView(R.id.ll_top)
    LinearLayout llTop;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    private boolean isPlayer = true;            //当前视频是否播放
    private boolean isFullScreen = false;       // 当前视频是否全屏
    private boolean isShowController = false; // 是否隐藏控制面板

    private ArrayList<LocalVideoBean> videoLists;

    private TimeUtils timeUtils;
    private BroadcastReceiver batteryReceiver;
    private int position;

    //注冊手势识别器
    private GestureDetector mDetector;

    private Uri uri;
    private boolean isFullScrenn;
    private int screenwidth;
    private int screenHeight;
    private int videoWidth;
    private int videoHeight;
    private boolean isMaxVoice = false;
    private AudioManager am;
    private int currentVoice;
    private int maxVoice;

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
                case HIDE_MEDIACONTROLLER:

                    //隐藏控制面板
                    hideMediaController();

                    break;
            }
        }
    };


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

        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMaxVoice = !isMaxVoice;
                setButtonVoice();

            }
        });

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setScreenState();
            }
        });


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
                    videoPlayer.pause();
                    videoPlayer.seekTo(progress);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacksAndMessages(null);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                videoPlayer.start();
                mHandler.sendEmptyMessage(PROGRESS);
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
            }
        });

        /*
         *
         * 获取系统的声音
         */
        sbVoice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateVoice(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(HIDE_MEDIACONTROLLER);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
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
                //  isPlayer = !isPlayer;
                setPlayOrPauseState();
                LogUtils.d("点击暂停按钮");
            }
        });


    }

    /**
     * @param progress
     */
    private void updateVoice(int progress) {
        currentVoice = progress;
        am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
        sbVoice.setProgress(currentVoice);

        if (currentVoice <= 0) {
            isMaxVoice = false;
        } else {
            isMaxVoice = true;
        }

    }

    /**
     * 点击button 音量的切换
     */
    private void setButtonVoice() {
        if (isMaxVoice) {

            am.setStreamVolume(AudioManager.STREAM_MUSIC, currentVoice, 0);
            sbVoice.setProgress(currentVoice);

        } else {
            //静音
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            sbVoice.setProgress(0);

        }

    }


    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {

        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
        isShowController = false;
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

        // 设声音
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        currentVoice = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sbVoice.setProgress(currentVoice);
        sbVoice.setMax(maxVoice);


        mDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {

            /**
             * 长按
             * @param e
             */
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                setPlayOrPauseState();
            }

            /**
             * 双击的回到
             * @param e
             * @return
             */
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                isFullScreen = !isFullScreen;
                setScreenState();
                return super.onDoubleTap(e);
            }


            //单机
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

                if (isShowController) {
                    hideMediaController();
                    mHandler.removeMessages(HIDE_MEDIACONTROLLER);
                } else {
                    showMediaController();
                    mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);
                }

                return super.onSingleTapConfirmed(e);
            }
        });


        // 获取屏幕的宽高
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenwidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {

        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
        isShowController = true;

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
            setVideoScrennType(FULL_SCREEN);

        } else {
            btnFullscreen.setBackgroundResource(R.drawable.unfull_screen_selector);
            setVideoScrennType(DEFAULT_SCREEN);
        }
    }

    /**
     * 判断播放的状态
     */
    private void setPlayOrPauseState() {

        LogUtils.d("点击播放暂停按钮");
        if (!videoPlayer.isPlaying()) {
            // 正在  播放
            btnPause.setBackgroundResource(R.drawable.pause_selector);
            mHandler.sendEmptyMessage(PROGRESS);
            videoPlayer.start();
        } else {
            btnPause.setBackgroundResource(R.drawable.player_selector);
            mHandler.removeMessages(PROGRESS);
            videoPlayer.pause();
        }

    }

    private void setVideoPlayer() {

        // 前期准备
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();

                int duration = videoPlayer.getDuration();
                tvVideoTime.setText(timeUtils.stringForTime(duration));
                sbDuration.setMax(duration);
                videoPlayer.start();
                mHandler.sendEmptyMessage(PROGRESS);
                // 发送隐藏控制面板
                mHandler.sendEmptyMessageDelayed(HIDE_MEDIACONTROLLER, 4000);

                setVideoScrennType(DEFAULT_SCREEN);

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

    /**
     * 设置屏幕的类型
     *
     * @param screenType
     */
    private void setVideoScrennType(int screenType) {
        switch (screenType) {
            case DEFAULT_SCREEN:
                isFullScrenn = false;
                btnFullscreen.setBackgroundResource(R.drawable.unfull_screen_selector);
                //
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;


                int width = screenwidth;
                int height = screenHeight;


                //
                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }

                videoPlayer.setVideoSize(width, height);
                break;
            case FULL_SCREEN:

                isFullScreen = true;
                btnFullscreen.setBackgroundResource(R.drawable.full_screen_selector);

                videoPlayer.setVideoSize(screenwidth, screenHeight);
                break;
        }
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
