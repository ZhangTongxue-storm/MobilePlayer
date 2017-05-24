package com.storm.mobileplayer.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.ViewHolder.ViewHolder;
import com.storm.mobileplayer.bean.LocalAudioBean;
import com.storm.mobileplayer.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Storm on 2017/5/23.
 */

public class LocalAudioAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LocalAudioBean> mDatas;
    private TimeUtils timeUtils;
    public LocalAudioAdapter(Context context) {
        mContext = context;
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
            convertView = View.inflate(mContext, R.layout.item_local_audio, null);
        }

        TextView musicName = ViewHolder.getView(convertView, R.id.audio_name);
        TextView musicDuration = ViewHolder.getView(convertView, R.id.audio_duration);
        TextView musicSize = ViewHolder.getView(convertView, R.id.audio_size);

        musicName.setText(mDatas.get(position).getName());
        musicSize.setText(Formatter.formatFileSize(mContext, mDatas.get(position).getSize()));
        musicDuration.setText(timeUtils.stringForTime((int) mDatas.get(position).getDuration()));

        return convertView;
    }


    public void refreshData(ArrayList<LocalAudioBean> audioBeen) {
        if (audioBeen != null && audioBeen.size() > 0) {
            mDatas.clear();
            mDatas.addAll(audioBeen);
            notifyDataSetChanged();
        }
    }
}

