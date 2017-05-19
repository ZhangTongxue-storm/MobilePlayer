package com.storm.mobileplayer.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.storm.mobileplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Storm on 2017/5/19.
 * titlebar 自定义
 */

public class TitleBar extends LinearLayout {

    @BindView(R.id.tv_serach)
    TextView tv_serach;

    @BindView(R.id.tv_game)
    TextView tv_game;

    @BindView(R.id.game_hot)
    View game_hot;

    @BindView(R.id.tv_record)
    ImageView tv_record;

    private Context mContext;

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    /**
     * 加载布局的时候
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        setListener();
    }

    private void setListener() {

        tv_record.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击时间", Toast.LENGTH_SHORT).show();
            }
        });

        tv_serach.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "点击搜索框", Toast.LENGTH_SHORT).show();

            }
        });

        tv_game.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "点击游戏", Toast.LENGTH_SHORT).show();
            }
        });



    }


}
