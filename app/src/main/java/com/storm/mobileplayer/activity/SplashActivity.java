package com.storm.mobileplayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.storm.mobileplayer.MainActivity;
import com.storm.mobileplayer.R;


public class SplashActivity extends AppCompatActivity {

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 2000);

    }

    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除消息
        handler.removeCallbacksAndMessages(null);

    }
}
