package com.storm.mobileplayer.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.storm.mobileplayer.R;
import com.storm.mobileplayer.ViewHolder.ViewHolder;
import com.storm.mobileplayer.bean.NetVideoBean;
import com.storm.mobileplayer.utils.TimeUtils;

import java.util.ArrayList;

/**
 * Created by Storm on 2017/5/22.
 */

public class NetVideoAdapter extends BaseAdapter {


    private Context mContext;
    private ArrayList<NetVideoBean.TrailersBean> mDatas;
    private TimeUtils timeUtils;

    public NetVideoAdapter(Context context) {
        this.mContext = context;
        mDatas = new ArrayList<>();
        timeUtils = new TimeUtils();
    }

    @Override
    public int getCount() {
        return mDatas != null? mDatas.size():0;
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
            convertView = View.inflate(mContext, R.layout.item_net_video, null);
        }

        ImageView movieIcon = ViewHolder.getView(convertView, R.id.iv_movie_icon);
        TextView movieName = ViewHolder.getView(convertView, R.id.tv_movie_name);
        TextView movieContent = ViewHolder.getView(convertView, R.id.tv_movie_content);
        TextView movieLength = ViewHolder.getView(convertView, R.id.tv_movie_length);

        // 设置数据

        movieName.setText(mDatas.get(position).getMovieName());
        movieContent.setText(mDatas.get(position).getSummary());
        movieLength.setText(timeUtils.stringForTime(mDatas.get(position).getVideoLength() * 1000));

        Picasso.with(mContext).load(mDatas.get(position).getCoverImg())
                .placeholder(R.drawable.bg_player_loading_background).into(movieIcon);

        return convertView;
    }

    public void refreshData(NetVideoBean netVideoBean) {
        if (netVideoBean != null && netVideoBean.getTrailers().size() > 0) {
            mDatas.clear();
            mDatas.addAll(netVideoBean.getTrailers());
            notifyDataSetChanged();
        }
    }
}
