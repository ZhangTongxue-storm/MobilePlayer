package com.storm.mobileplayer.utils;

import android.content.Context;
import android.net.TrafficStats;

/**
 * Created by Storm on 2017/5/22.
 */

public class NetUtils {
    private static long lastTotalRxBytes = 0;
    private static long lastTimeStamp = 0;

    public static boolean isNeturl(String data) {
        boolean isNetUri = false;

        if (data != null) {
            if (data.toLowerCase().startsWith("http") || data.toLowerCase().startsWith("mms")
                    || data.toLowerCase().startsWith("rtsp")) {
                isNetUri = true;
            }
        }
        return isNetUri;
    }


    public static String getNetSpeed(Context context) {

        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB;
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;
        String msg = String.valueOf(speed) + " kb/s";
        return msg;

    }
}
