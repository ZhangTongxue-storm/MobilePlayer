package com.storm.mobileplayer.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Storm on 2017/5/21.
 * 自定义 videoView;
 */

public class VideoView extends android.widget.VideoView {

    private Context mContext;

    public VideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }


    public void setVideoSize(int width, int height) {

        ViewGroup.LayoutParams params =
                getLayoutParams();

        params.width = width;
        params.height = height;
        setLayoutParams(params);

    }
}
