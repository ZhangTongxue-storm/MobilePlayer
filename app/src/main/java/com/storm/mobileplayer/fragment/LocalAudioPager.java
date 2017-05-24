package com.storm.mobileplayer.fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.base.BaseFragment;
import com.storm.mobileplayer.bean.LocalAudioBean;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by Storm on 2017/5/19.
 * 本地音乐
 */

public class LocalAudioPager extends BaseFragment {

    @BindView(R.id.lv_local_audio)
    ListView lvLocalAudio;

    private ArrayList<LocalAudioBean> audioBeen;

    @Override
    protected void bindView() {

    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {

        return inflater.inflate(R.layout.fragment_local_audio, container, false);
    }

    @Override
    protected void initData() {
        getLocalMusicData();


    }


    /**
     * 获取本地音乐
     */
    public void getLocalMusicData() {
        audioBeen = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {

                 String[] audioTags = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//sdcard上文件的名称
                        MediaStore.Audio.Media.DURATION,//视频的时长,毫秒
                        MediaStore.Audio.Media.SIZE,//文件大小，bytes
                        MediaStore.Audio.Media.DATA,//视频的播放地址
                        MediaStore.Audio.Media.ARTIST//演唱者
                };

            }
        }).start();


    }
}
