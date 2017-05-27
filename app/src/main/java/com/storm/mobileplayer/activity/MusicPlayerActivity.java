package com.storm.mobileplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSeekBar;
import com.jakewharton.rxbinding.widget.SeekBarChangeEvent;
import com.jakewharton.rxbinding.widget.SeekBarProgressChangeEvent;
import com.jakewharton.rxbinding.widget.SeekBarStartChangeEvent;
import com.storm.mobileplayer.IMusicPlayService;
import com.storm.mobileplayer.R;
import com.storm.mobileplayer.bean.Lyric;
import com.storm.mobileplayer.custom.LyricView;
import com.storm.mobileplayer.service.MusicPlayService;
import com.storm.mobileplayer.utils.LogUtils;
import com.storm.mobileplayer.utils.LyricUtil;
import com.storm.mobileplayer.utils.RxBus;
import com.storm.mobileplayer.utils.TimeUtils;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MusicPlayerActivity extends RxAppCompatActivity {


    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_music_name)
    TextView tvMusicName;
    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.tv_audio_time)
    TextView tvAudioTime;
    @BindView(R.id.sb_duration)
    SeekBar sbDuration;
    @BindView(R.id.btn_play_model)
    Button btnPlayModel;
    @BindView(R.id.btn_pre)
    Button btnPre;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_lyrics)
    Button btnLyrics;
    @BindView(R.id.lyric_show_view)
    LyricView lyricShowView;

    private int position; //当前的播放位置


    private IMusicPlayService service;
    private TimeUtils timeUtils;
    // 计时更新时间
    private Subscription subscribe;
    private CompositeSubscription compositeSubscription;
    private boolean notification;  // 是否为通知栏打开界面
    private Subscription updateLrySub;
    //    private NotificationManager nm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        timeUtils = new TimeUtils();
        compositeSubscription = new CompositeSubscription();
        // 开启动
        AnimationDrawable background = (AnimationDrawable) ivIcon.getBackground();
        background.start();
        getIntentDataAndBindService();
        getisPlayingState();
        setListener();

    }


    private void setListener() {

        RxView.clicks(btnPlayModel)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        setPlayModel();
                    }
                });


        Subscription seekToSub = RxSeekBar.changeEvents(sbDuration).subscribe(new Action1<SeekBarChangeEvent>() {
            @Override
            public void call(SeekBarChangeEvent seekBarChangeEvent) {
                if (seekBarChangeEvent instanceof SeekBarStartChangeEvent) {
                    LogUtils.d("拖动手指放开的时候执行这里");
                    btnPause.setBackgroundResource(R.drawable.music_pause_selector);
                } else if (seekBarChangeEvent instanceof SeekBarProgressChangeEvent) {
                    SeekBarProgressChangeEvent seekBarChangeEvent1 = (SeekBarProgressChangeEvent) seekBarChangeEvent;
                    if (seekBarChangeEvent1.fromUser()) {
                        int progress = seekBarChangeEvent1.progress();
                        if (service != null) {
                            try {
                                service.seekTo(progress);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }
            }
        });


        compositeSubscription.add(seekToSub);

        // 关于
        RxView.clicks(btnPre)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {

                        if (service != null) {
                            try {
                                service.onPre();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });

        RxView.clicks(btnNext)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        //点击播放下一首
                        if (service != null) {
                            try {
                                service.onNext();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        // 点击暂停按钮
        RxView.clicks(btnPause)
                .throttleFirst(200, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        try {
                            if (service.isPlaying()) {
                                service.onPause();
                            } else {
                                service.onStart();
                            }
                            //设置播放状态
                            setPlayingState();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 设置当播放模式
     */
    private void setPlayModel() {
        // 获取当前的播放模式
        if (service != null) {
            try {
                int playModel = service.getPlayModel();

                if (playModel == MusicPlayService.MODEL_PLAY_NORMAL) {
                    playModel = MusicPlayService.MODEL_PLAY_ALL;
                } else if (playModel == MusicPlayService.MODEL_PLAY_ALL) {
                    playModel = MusicPlayService.MODEL_PLAY_SINGLE;
                } else if (playModel == MusicPlayService.MODEL_PLAY_SINGLE) {
                    playModel = MusicPlayService.MODEL_PLAY_NORMAL;
                }

                service.setPlayModel(playModel);

                changePlayModelState();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据模式修改视图
     */
    private void changePlayModelState() {
        //从根源更改
        if (service != null) {

            try {
                final int playModel = service.getPlayModel();
                RxView.draws(btnPlayModel).subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (playModel == MusicPlayService.MODEL_PLAY_NORMAL) {

                            btnPlayModel.setBackgroundResource(R.drawable.music_model_order_selector);

                        } else if (playModel == MusicPlayService.MODEL_PLAY_ALL) {
                            btnPlayModel.setBackgroundResource(R.drawable.music_model_all_selector);

                        } else if (playModel == MusicPlayService.MODEL_PLAY_SINGLE) {
                            btnPlayModel.setBackgroundResource(R.drawable.music_model_singlerepeat_selector);
                        }
                    }
                });


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 设置播放状态
     */
    private void setPlayingState() {
        if (service != null) {
            try {
                if (service.isPlaying()) {
                    btnPause.setBackgroundResource(R.drawable.music_pause_selector);

                } else {
                    btnPause.setBackgroundResource(R.drawable.music_playing_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 获取数据并绑定
     */
    public void getIntentDataAndBindService() {

        position = getIntent().getIntExtra("position", 0);
        notification = getIntent().getBooleanExtra("notification", false);
//       nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//

        Intent intent = new Intent(MusicPlayerActivity.this, MusicPlayService.class);
        // 绑定并且实例化 start 避免重复创建
        bindService(intent, conn, BIND_AUTO_CREATE);
        startService(intent);

    }

    private ServiceConnection conn = new ServiceConnection() {

        /**
         *  绑定成功后的回调
         * @param name
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {

            service = IMusicPlayService.Stub.asInterface(iBinder);
            try {

                if (!notification) {

                    service.openAudio(position);
                } else {
                    getMusicInfo();
                }


            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (service != null) {
                service = null;
            }
        }
    };


    /**
     * 设置基本歌曲的信息
     */
    public void getMusicInfo() {
        try {
            String artist = service.getArtist();
            String musicName = service.getMusicName();
            tvMusicName.setText(musicName);
            tvArtist.setText(artist);
            sbDuration.setMax(service.getDuration());
            loadingLyrics();
            updateDuration();


        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载歌词
     */
    private void loadingLyrics() throws RemoteException {

        String lyricPath = service.getMusicPath();
        // 获取歌词的路径
        lyricPath = lyricPath.substring(0, lyricPath.lastIndexOf("."));

        File file = new File(lyricPath + ".lrc");
        if (!file.exists()) {
            file = new File(lyricPath + ".txt");
        }

        LyricUtil lyricUtil = new LyricUtil();
        lyricUtil.read(file);

        //LogUtils.d("获取的各台词 ----------" + lyricUtil.getLyrics().size());
        ArrayList<Lyric> lyrics = lyricUtil.getLyrics();
        // LogUtils.d("获取的各台词 ----------" + lyrics.size());

        if(lyricUtil.isLyric()) {
            lyricShowView.setLyrics(lyrics);
            updateLyrics();
        }

    }

    /**
     * 更新歌词
     */
    private void updateLyrics()  {
        updateLrySub = Observable.interval(0,1, TimeUnit.SECONDS, Schedulers.io())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {

                        LogUtils.d("持续的发送事件");

                        try {
                            if (service.isPlaying()) {
                                int currentDuration = service.currentDuration();
                                lyricShowView.setNextShowLyric(currentDuration);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }


                    }
                });
        compositeSubscription.add(updateLrySub);

    }

    //更新seek
    private void updateDuration() {
        subscribe = Observable.interval(0, 1, TimeUnit.SECONDS, Schedulers.io())
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        //LogUtils.d("获取增加的数值" + o);

                        try {
                            if (service.isPlaying()) {

                                String audioTime = timeUtils.stringForTime(service.getDuration());

                                String currentTime = timeUtils.stringForTime(service.currentDuration());

                                tvAudioTime.setText(currentTime + "/" + audioTime);
                                sbDuration.setProgress(service.currentDuration());

                            }

                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        compositeSubscription.unsubscribe();

    }

    public void getisPlayingState() {
        RxBus.getInstance().toObserverable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (1001 == (int) o) {
                    LogUtils.d("检测到开始播放---------------");
                    getMusicInfo();
                }
            }
        });
    }
}
