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
import com.storm.mobileplayer.activity.SystemVideoActivity;
import com.storm.mobileplayer.adapter.LocalVideoAdapter;
import com.storm.mobileplayer.base.BaseFragment;
import com.storm.mobileplayer.bean.LocalVideoBean;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * Created by Storm on 2017/5/19.
 * 本地视频
 */

public class LocalVideoPager extends BaseFragment {

    @BindView(R.id.lv_local_video)
    ListView lvLocalVideo;


    @BindView(R.id.tv_nodata)
    TextView tvNodata;


    private ArrayList<LocalVideoBean> videoBeans;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (videoBeans != null && videoBeans.size() > 0) {
                        tvNodata.setVisibility(View.GONE);
                        // 设置数据
                        adapter.refreshData(videoBeans);
                        lvLocalVideo.setAdapter(adapter);
                    } else {
                        tvNodata.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };
    private LocalVideoAdapter adapter;


    @Override
    protected void bindView() {
    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_local_video, container, false);
    }

    @Override
    protected void initData() {
        setDatas();
        adapter = new LocalVideoAdapter(mContext);
        setListener();


    }

    private void setListener() {
        lvLocalVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //   LocalVideoBean item = (LocalVideoBean) adapter.getItem(position);

                Intent intent = new Intent(mContext,SystemVideoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("videoList", videoBeans);
                intent.putExtra("position", position);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

    }

    private void setDatas() {
        videoBeans = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,
                        MediaStore.Video.Media.DURATION,
                        MediaStore.Video.Media.SIZE,
                        MediaStore.Video.Media.DATA,
                };

                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = mContext.getContentResolver();
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                        long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                        LocalVideoBean bean = new LocalVideoBean(name,duration,size,data);
                        videoBeans.add(bean);
                    }
                    cursor.close();
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }


}

