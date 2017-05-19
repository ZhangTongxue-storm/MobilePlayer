package com.storm.mobileplayer.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.base.BaseFragment;

/**
 * Created by Storm on 2017/5/19.
 * 网络视频
 */

public class NetVideoPager extends BaseFragment {

    @Override
    protected void bindView() {

    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_net_video, container, false);
    }
}
