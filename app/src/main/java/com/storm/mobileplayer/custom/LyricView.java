package com.storm.mobileplayer.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.storm.mobileplayer.bean.Lyric;

import java.util.ArrayList;

/**
 * Created by Storm on 2017/5/26.
 */

public class LyricView extends android.support.v7.widget.AppCompatTextView {

    public static final String TAG = "LyricView";

    private Paint mCurrentPaint;
    private Paint mWritePaint;

    private int width;
    private int height;
    private ArrayList<Lyric> lyrics;
    private int index;
    private float textHeight = 40;
    private int currentDuration;


    public LyricView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }


    private void initView() {
        mCurrentPaint = new Paint();
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setTextAlign(Paint.Align.CENTER);
        mCurrentPaint.setTextSize(35);
        mCurrentPaint.setColor(Color.GREEN);

        mWritePaint = new Paint();
        mWritePaint.setAntiAlias(true);
        mWritePaint.setTextAlign(Paint.Align.CENTER);
        mWritePaint.setTextSize(35);
        mWritePaint.setColor(Color.WHITE);

        // 准备歌词
        lyrics = new ArrayList<>();
        // Lyric lyric = new Lyric();

//        for (int i = 0; i < 300; i++) {
//            lyric.setContent("aaaaaaaaa_" + i);
//            lyric.setTimePoint(2000 * i);
//            lyric.setLightTime(2000);
//
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {
            //歌词存在
            String currentContent = lyrics.get(index).getContent();
            canvas.drawText(currentContent, width / 2, height / 2, mCurrentPaint);

            float tempY = height / 2;

            // 绘制前面部分
            for (int i = index - 1; i > 0; i--) {
                // 获取前面的内容
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;

                canvas.drawText(preContent, width / 2, tempY, mWritePaint);
            }

            tempY = height / 2; // 等高线种重制

            for (int i = index + 1; i < lyrics.size(); i++) {

                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, mWritePaint);
            }

        } else {
            canvas.drawText("没有歌词,,,", width / 2, height / 2, mCurrentPaint);
        }


    }


    public void setNextShowLyric(int currentDuration) {
        this.currentDuration = currentDuration;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {

            if (this.currentDuration < lyrics.get(i).getTimePoint()) {
                int timpIndex = i - 1;
                if (this.currentDuration >= lyrics.get(timpIndex).getTimePoint()) {
                    index = timpIndex;
                }
            }

        }
        invalidate();

    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        if (lyrics != null) {
            this.lyrics.clear();
            this.lyrics.addAll(lyrics);

        }
    }
}
