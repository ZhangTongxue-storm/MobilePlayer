package com.storm.mobileplayer;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.storm.mobileplayer.base.BaseFragment;
import com.storm.mobileplayer.fragment.LocalAudioPager;
import com.storm.mobileplayer.fragment.LocalVideoPager;
import com.storm.mobileplayer.fragment.NetAudioPager;
import com.storm.mobileplayer.fragment.NetVideoPager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fl_main)
    FrameLayout flMain;
    @BindView(R.id.rb_local_video)
    RadioButton rbLocalVideo;
    @BindView(R.id.rb_local_audio)
    RadioButton rbLocalAudio;
    @BindView(R.id.rb_net_video)
    RadioButton rbNetVideo;
    @BindView(R.id.rb_net_audio)
    RadioButton rbNetAudio;
    @BindView(R.id.rg_navigation)
    RadioGroup rgNavigation;

    private ArrayList<BaseFragment> mFragments;
    private int position;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setFragment();
        setListener();

        rgNavigation.check(R.id.rb_local_video);

    }

    private void setListener() {

        rgNavigation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_local_video:
                        position = 0;
                        break;
                    case R.id.rb_local_audio:
                        position = 1;
                        break;
                    case R.id.rb_net_video:
                        position = 2;
                        break;
                    case R.id.rb_net_audio:
                        position = 3;
                        break;
                }
                BaseFragment targetFragment = mFragments.get(position);
                addOrHideFragment(targetFragment);
            }
        });

    }

    /**
     * 加载fragment
     *
     * @param targetFragment 目标fragment
     */
    private void addOrHideFragment(BaseFragment targetFragment) {
        if (currentFragment != targetFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (!targetFragment.isAdded()) {
                // 没有添加
                if (currentFragment != null) {
                    ft.hide(currentFragment);
                }
                ft.add(R.id.fl_main, targetFragment);
            } else {
                if (currentFragment != null) {
                    ft.hide(currentFragment);
                }
                ft.show(targetFragment);
            }

            ft.commit();
            currentFragment = targetFragment;
        }

    }

    /**
     * 设置 fragment
     */
    private void setFragment() {
        initFragment();


    }

    /**
     * 加载
     */
    private void initFragment() {
        mFragments = new ArrayList<>();
        mFragments.add(new LocalVideoPager());
        mFragments.add(new LocalAudioPager());
        mFragments.add(new NetVideoPager());
        mFragments.add(new NetAudioPager());
    }
}
