package com.storm.mobileplayer.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Storm on 2017/5/19.
 * fragment 的基类
 */

public abstract class BaseFragment extends Fragment {


    protected Context mContext;
    protected View rootView;
    protected Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = onCreateRootView(inflater, container);
        unbinder = ButterKnife.bind(this, rootView);
        bindView();
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    protected void initData() {
    }

    ;

    /**
     * 绑定view
     */
    protected abstract void bindView();

    /**
     * 子类需要实现的方法
     *
     * @param inflater
     * @param container
     * @return
     */
    protected abstract View onCreateRootView(LayoutInflater inflater, ViewGroup container);

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();

    }
}
