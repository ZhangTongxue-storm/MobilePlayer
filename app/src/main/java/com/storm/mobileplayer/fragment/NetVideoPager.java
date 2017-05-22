package com.storm.mobileplayer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.storm.mobileplayer.R;
import com.storm.mobileplayer.activity.SystemVideoActivity;
import com.storm.mobileplayer.adapter.NetVideoAdapter;
import com.storm.mobileplayer.base.BaseFragment;
import com.storm.mobileplayer.base.BaseSubscriber;
import com.storm.mobileplayer.bean.NetVideoBean;
import com.storm.mobileplayer.httpclient.CommonTransformer;
import com.storm.mobileplayer.httpclient.RetrofitClientManager;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by Storm on 2017/5/19.
 * 网络视频
 */

public class NetVideoPager extends BaseFragment {


    @BindView(R.id.lv_net_video)
    ListView lvNetVideo;

    private NetVideoAdapter mAdapter;


    @Override
    protected void bindView() {

    }

    @Override
    protected View onCreateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_net_video, container, false);
    }

    @Override
    protected void initData() {
        mAdapter = new NetVideoAdapter(mContext);
        getData();

        lvNetVideo.setAdapter(mAdapter);
        setListener();


    }

    private void setListener() {
        lvNetVideo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NetVideoBean.TrailersBean item = (NetVideoBean.TrailersBean) mAdapter.getItem(position);

                Intent intent = new Intent(mContext, SystemVideoActivity.class);
                intent.setData(Uri.parse(item.getUrl()));

                startActivity(intent);
            }
        });
    }

    public void getData() {

        Observable<NetVideoBean> netVideoService =
                RetrofitClientManager.getClientSerivice().getNetVideoService();
        netVideoService.compose(new CommonTransformer<NetVideoBean>())
                .subscribe(new BaseSubscriber<NetVideoBean>(mContext) {

                    @Override
                    public void onNext(NetVideoBean netVideoBean) {


                        mAdapter.refreshData(netVideoBean);
                    }
               });

    }

}

