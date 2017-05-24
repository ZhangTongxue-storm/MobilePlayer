package com.storm.mobileplayer.activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.storm.mobileplayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MusicPlayerActivity extends AppCompatActivity {

    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_music_name)
    TextView tvMusicName;
    @BindView(R.id.tv_artist)
    TextView tvArtist;
    @BindView(R.id.tv_video_time)
    TextView tvVideoTime;
    @BindView(R.id.sb_duration)
    SeekBar sbDuration;
    @BindView(R.id.btn_play_model)
    Button btnPlayModel;
    @BindView(R.id.btn_pre)
    Button btnPre;
    @BindView(R.id.btn_pause)
    Button btnPause;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_lyrics)
    Button btnLyrics;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        // 开启动
        AnimationDrawable  background = (AnimationDrawable) ivIcon.getBackground();
        background.start();

    }


}
