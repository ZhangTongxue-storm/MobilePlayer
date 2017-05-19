package com.storm.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.ViewHolder.ViewHolder;
import com.storm.mobileplayer.bean.LocalVideoBean;
import com.storm.mobileplayer.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Storm on 2017/5/19.
 */

public class LocalVideoAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<LocalVideoBean> mDatas;

    private TimeUtils timeUtils;

    public LocalVideoAdapter(Context context) {

        this.context = context;
        mDatas = new ArrayList<>();
        timeUtils = new TimeUtils();
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.itme_local_video, null);
        }

        TextView videoName = ViewHolder.getView(convertView, R.id.video_name);
        TextView videoDuration = ViewHolder.getView(convertView, R.id.video_duration);
        TextView videoSize = ViewHolder.getView(convertView, R.id.video_size);

        videoName.setText(mDatas.get(position).getName());
        videoSize.setText(Formatter.formatFileSize(context, mDatas.get(position).getSize()));
        videoDuration.setText(timeUtils.stringForTime((int) mDatas.get(position).getDuration()));
        return convertView;
    }

    public void refreshData(ArrayList<LocalVideoBean> videoBeans) {
        if (videoBeans != null && videoBeans.size() > 0) {
            mDatas.clear();
            mDatas.addAll(videoBeans);
            notifyDataSetChanged();
        }

    }
}
