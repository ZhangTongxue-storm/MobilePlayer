package com.storm.mobileplayer.bean;

/**
 * Created by Storm on 2017/5/26.
 * 歌词类
 */

public class Lyric {

    private String content; //歌词的内容
    private long timePoint; // 时间戳
    private long lightTime;  // 高亮时间


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getLightTime() {
        return lightTime;
    }

    public void setLightTime(long lightTime) {
        this.lightTime = lightTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", lightTime=" + lightTime +
                '}';
    }
}
