package com.storm.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.storm.mobileplayer.IMusicPlayService;
import com.storm.mobileplayer.R;
import com.storm.mobileplayer.activity.MusicPlayerActivity;
import com.storm.mobileplayer.bean.LocalAudioBean;
import com.storm.mobileplayer.utils.LogUtils;
import com.storm.mobileplayer.utils.RxBus;
import com.storm.mobileplayer.utils.SPUtils;

import java.io.IOException;
import java.util.ArrayList;

import io.vov.vitamio.MediaPlayer;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.storm.mobileplayer.MyApplication.mContext;

/**
 * Created by Storm on 2017/5/24.
 */

public class MusicPlayService extends Service {

    public static final int NOTIFICATION_MUSIC_CODE = 1;

    /**
     * 顺序播放
     */
    public static final int MODEL_PLAY_NORMAL = 1;
    /**
     * 全部循环
     */
    public static final int MODEL_PLAY_ALL = 3;
    /**
     * 单曲循环
     */
    public static final int MODEL_PLAY_SINGLE = 2;

    /**
     * 当前播放模式
     */
    public int CURRENT_PLAY_MODEL = 1;

    private int mPosition; // 当前播放的位置
    private ArrayList<LocalAudioBean> audioList;
    private int position; // 传递的位置
    private LocalAudioBean musicBean;

    private MediaPlayer mMediaPlayer;
    private NotificationManager nm;
    private SPUtils spUtils;
    private boolean isLooping = false;
    private boolean isPlaying = true;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }


    /**
     * 根据一个位置打开音频and 播放
     *
     * @param position
     */
    public void openAudio(int position) {
        this.position = position;
        if (audioList != null && audioList.size() > 0) {
            if (position < audioList.size()) {

                musicBean = audioList.get(position);

                if (mMediaPlayer != null) {
                    mMediaPlayer.reset();
                    mMediaPlayer = null;
                }

                mMediaPlayer = new MediaPlayer(this);
                //设置播放地址
                try {

                    mMediaPlayer.setDataSource(musicBean.getData());
                    mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
                    mMediaPlayer.setOnErrorListener(mOnErrorListener);
                    mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
                    mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);

                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        } else {

            Toast.makeText(this, "音乐没有加载完成", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * 拖动完成
     */
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(MediaPlayer mp) {
            mMediaPlayer.start();
        }
    };

    /**
     * 播放的准备监听
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 准备完成 开始播放
            onStart();
            // 发送数据
            sendMusicPlayingMessage();

            updateNotification();
        }
    };

    /**
     * 更新任务栏信息
     */
    private void updateNotification() {

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("音乐")
                .setContentText("正在播放" + getMusicName())
                .setContentIntent(pendingIntent)
                .build();

        nm.notify(NOTIFICATION_MUSIC_CODE, notification);
    }

    /**
     * 音乐开始播放的监听
     */
    private void sendMusicPlayingMessage() {

        RxBus.getInstance().send(1001);

    }

    /**
     * 播放出错的监听
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

    };

    /**
     * 播放完成的监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

            if (!mMediaPlayer.isLooping()) {
                onNext();
            }

            if (position == audioList.size() - 1) {
                mMediaPlayer.stop();
            }

        }
    };


    /**
     * 点击开始播放
     */
    public void onStart() {
        if (isLooping) {
            mMediaPlayer.setLooping(true);

        } else {
            mMediaPlayer.setLooping(false);

        }
        mMediaPlayer.start();

    }

    /**
     * 暂停
     */
    public void onPause() {

        mMediaPlayer.pause();

    }

    /**
     * 下一首
     **/
    public void onNext() {
        position += 1;
        setModelOrToPositon();
        startNextMusic();

    }

    private void setModelOrToPositon() {

        int playModel = getPlayModel();

        mMediaPlayer.setLooping(false);
        if (playModel == MusicPlayService.MODEL_PLAY_ALL) {
            isLooping = false;
            if (position > audioList.size() - 1) {
                position = 0;
            }
            if (position < 0) {
                position = audioList.size() - 1;
            }
        } else if (playModel == MusicPlayService.MODEL_PLAY_SINGLE) {
            isLooping = true;
        } else if (playModel == MusicPlayService.MODEL_PLAY_NORMAL) {
            isLooping = false;
        }


    }

    private void startNextMusic() {

        if (position < audioList.size() && position > 0) {
            openAudio(position);
        }



    }

    /**
     * 上一首
     */
    public void onPre() {
        position -= 1;
        setModelOrToPositon();
        startNextMusic();
    }

    /**
     * 开始播放下一首
     */
    private void startPreMusic() {

        if (position > 0) {
            openAudio(position);
        }

    }

    /**
     * 获取当前播放的名字
     * <p>
     * return
     */
    public String getMusicName() {
        String musicName = musicBean.getName();
        musicName = musicName.substring(0, musicName.lastIndexOf("."));
        return musicName;
    }

    /**
     * 获取当前音乐的演唱者
     *
     * @return 当前音乐的演唱者
     */
    public String getArtist() {
        return musicBean.getArtst();
    }


    /**
     * 获取当前的进度
     *
     * @return
     */
    public int currentDuration() {
        return (int) mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取总的进度
     *
     * @return
     */
    public int getDuration() {
        return (int) mMediaPlayer.getDuration();
    }

    /**
     * 获取当前的播放模式
     *
     * @return
     */
    public int getPlayModel() {
        return CURRENT_PLAY_MODEL;
    }

    /**
     * 设置播放模式
     *
     * @param playModel
     */
    public void setPlayModel(int playModel) {
        //设置模式
        this.CURRENT_PLAY_MODEL = playModel;
        if (CURRENT_PLAY_MODEL == MusicPlayService.MODEL_PLAY_SINGLE) {
            isLooping = true;
            mMediaPlayer.setLooping(true);
        } else {
            isLooping = false;
            mMediaPlayer.setLooping(false);

        }
        //存储到数据库中
        spUtils.put("play_model", CURRENT_PLAY_MODEL);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        spUtils = new SPUtils();
        spUtils.getSharedPreferences(this, "music_palyer");
        //获取本地音乐的实例
        getMusicData();

        RxBus.getInstance().toObserverable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {
                if (1 == (int) o) {
                    LogUtils.d("获取数据成功" + o);

                }
            }
        });


    }

    /**
     * 获取是本地音乐的实例
     */
    public void getMusicData() {

        Observable.create(new Observable.OnSubscribe<ArrayList<LocalAudioBean>>() {
            @Override
            public void call(Subscriber<? super ArrayList<LocalAudioBean>> subscriber) {

                ArrayList<LocalAudioBean> beans = new ArrayList<LocalAudioBean>();
                String[] audioTags = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//sdcard上文件的名称
                        MediaStore.Audio.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小，bytes
                        MediaStore.Audio.Media.DATA,//视频的播放地址
                        MediaStore.Audio.Media.ARTIST//演唱者
                };

                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = mContext.getContentResolver();
                Cursor cursor = resolver.query(uri, audioTags, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                        // String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                        // LogUtils.d("获取的歌手" + artist == null);
                        LogUtils.d("运行的线程" + name);

                        if (duration > 10 * 1000) {

                            LocalAudioBean bean = new LocalAudioBean(name, duration, size, data, "noSinger");
                            beans.add(bean);
                        }

                    }
                    cursor.close();

                }
                subscriber.onNext(beans);
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ArrayList<LocalAudioBean>>() {
                    @Override
                    public void call(ArrayList<LocalAudioBean> localAudioBeen) {

                        audioList = localAudioBeen;
                        RxBus.getInstance().send(1);

                    }
                });

    }

    private IBinder iBinder = new IMusicPlayService.Stub() {
        MusicPlayService service = MusicPlayService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {


        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void onStart() throws RemoteException {

            service.onStart();
        }

        @Override
        public void onPause() throws RemoteException {
            service.onPause();

        }

        @Override
        public void onNext() throws RemoteException {
            service.onNext();

        }

        @Override
        public void onPre() throws RemoteException {
            service.onPre();

        }

        @Override
        public String getMusicName() throws RemoteException {

            return service.getMusicName();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public int currentDuration() throws RemoteException {
            return service.currentDuration();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public int getPlayModel() throws RemoteException {
            return service.getPlayModel();
        }

        @Override
        public void setPlayModel(int playModel) throws RemoteException {

            service.setPlayModel(playModel);

        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mMediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int progerss) throws RemoteException {
            service.seekTo(progerss);

        }
    };

    private void seekTo(int progerss) {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(progerss);
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
}

