package com.storm.mobileplayer.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Storm on 2017/5/26.
 */

public class LyricView extends android.support.v7.widget.AppCompatTextView {

    public static final String TAG = "LyricView";

    private Paint mPaint;

    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setColor(Color.GREEN);
        mPaint.setTextSize(40);
        canvas.drawText("没有歌词,,,", getWidth() / 2, getHeight() / 2, mPaint);
    }
}
