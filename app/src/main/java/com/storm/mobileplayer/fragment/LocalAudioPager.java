package com.storm.mobileplayer.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.activity.MusicPlayerActivity;
import com.storm.mobileplayer.adapter.LocalAudioAdapter;
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

    @BindView(R.id.tv_nodata)
    TextView tvNoData;

    private ArrayList<LocalAudioBean> audioBeen;
    private LocalAudioAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (audioBeen != null && audioBeen.size() > 0) {

                        mAdapter.refreshData(audioBeen);
                        lvLocalAudio.setAdapter(mAdapter);

                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);

                    }
                    break;
            }
        }
    };

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
        mAdapter = new LocalAudioAdapter(mContext);
        setListener();

    }

    private void setListener() {
        lvLocalAudio.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(mContext, MusicPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("music", audioBeen);
                intent.putExtra("position", position);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
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

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
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

                        if (duration > 10 * 1000) {

                            LocalAudioBean bean = new LocalAudioBean(name, duration, size, data, "noSinger");
                            audioBeen.add(bean);
                        }

                    }
                    cursor.close();
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();


    }
}
