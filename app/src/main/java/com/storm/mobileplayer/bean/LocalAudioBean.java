package com.storm.mobileplayer.bean;

import java.io.Serializable;

/**
 * Created by Storm on 2017/5/23.
 * 本地音乐 bean类
 */

public class LocalAudioBean implements Serializable {

    private String name; // 名字
    private long duration; // 时长
    private long size; //大小
    private String data; // 地址
    private String artst;  // 歌手名字

    public LocalAudioBean() {

    }

    public LocalAudioBean(String name, long duration, long size, String data, String artst) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artst = artst;
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

    public String getArtst() {
        return artst;
    }

    public void setArtst(String artst) {
        this.artst = artst;
    }

    @Override
    public String toString() {
        return "LocalAudioBean{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artst='" + artst + '\'' +
                '}';
    }
}

