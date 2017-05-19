package com.storm.mobileplayer.bean;

/**
 * Created by Storm on 2017/5/19.
 *
 * 本地视频的bean类
 */

public class LocalVideoBean {

    private String name; // 视频的名字
    private long duration;// 视屏的时长
    private long size;// 视屏的大小
    private String data; // 视频的地址

    public LocalVideoBean() {
    }

    public LocalVideoBean(String name, long duration, long size, String data) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LocalVideoBean{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}
