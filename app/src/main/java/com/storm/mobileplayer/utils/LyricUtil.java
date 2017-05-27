package com.storm.mobileplayer.utils;

import com.storm.mobileplayer.bean.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Storm on 2017/5/26.
 */

public class LyricUtil {


    private boolean isLyric = false;

    private ArrayList<Lyric> lyrics;

    /**
     * 读取文件
     *
     * @param file
     */
    public void read(File file) {
        if (file == null || !file.exists()) {
            isLyric = false;
        } else {
            //cunzai
            lyrics = new ArrayList<>();
            isLyric = true;

            //读取文件
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader bufferedReader = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis, "GBK");
                bufferedReader = new BufferedReader(isr);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    analyzeLyric(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                CloseUtils.CloseIO(fis, isr, bufferedReader);
            }


            //把结果排列
            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric o1, Lyric o2) {
                    if (o1.getTimePoint() < o2.getTimePoint()) {
                        return -1;
                    } else if (o1.getTimePoint() > o2.getTimePoint()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });

            for (int i = 0; i < lyrics.size(); i++) {
                Lyric lyricOne = lyrics.get(i);

                if (i + 1 < lyrics.size()) {
                    Lyric two = lyrics.get(i + 1);
                    lyricOne.setLightTime(two.getTimePoint() - lyricOne.getTimePoint());
                }
            }

        }


    }

    /**
     * 解析一行 [02:04.12][03:37.32][00:59.73]我在这里欢笑
     *
     * @param line
     */
    private void analyzeLyric(String line) {

        int pos1 = line.indexOf("[");
        int pos2 = line.indexOf("]");

        if (pos1 == 0 && pos2 != -1) {
            //   "[ffjsl[]"
            long[] timeLongs = new long[getCountTag(line)];
            String timeStr = line.substring(pos1 + 1, pos2);

            // 解析第0句
            timeLongs[0] = String2Long(timeStr);

            if (timeLongs[0] == -1) {
                return;
            }

            int i = 1;

            //   [00:59.73]我在这里欢笑
            String content = line;
            //[00:59.73]我在这里欢笑
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);
                pos1 = content.indexOf("[");
                pos2 = content.indexOf("]");

                if (pos1 == 0 && pos2 != -1) {

                    timeStr = content.substring(pos1 + 1, pos2);
                    timeLongs[i] = String2Long(timeStr);

                    if (timeLongs[i] == -1) {
                        return;
                    }
                    i++;
                }
            }

            for (int j = 0; j < timeLongs.length; j++) {
                if (timeLongs[j] != 0) {
                    Lyric lyric = new Lyric();
                    lyric.setTimePoint(timeLongs[j]);

                    lyric.setContent(content);

                    lyrics.add(lyric);
                }
            }

        }


    }

    /**
     * 解析转化时间按
     *
     * @param timeStr 02:04.12
     * @return
     */
    private long String2Long(String timeStr) {

        long result = -1;
        try {
            String[] s1 = timeStr.split(":");
            String[] s2 = s1[1].split("\\.");
            // 开始转换
            long min = Long.valueOf(s1[0]);

            long second = Long.valueOf(s2[0]);

            long mill = Long.valueOf(s2[1]);

            result = min * 60 * 1000 + second * 1000 + mill * 10;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断有多少句歌词 [02:04.12]我在这里欢笑
     *
     * @param line
     * @return
     */
    private int getCountTag(String line) {
        int result = 1;// 至少有一句歌词
        String[] s1 = line.split("\\[");
        String[] s2 = line.split("\\]");
        if (s1.length == 0 && s2.length == 0) {
            result = 1;
        } else if (s1.length > s2.length) {
            result = s1.length;
        } else {
            result = s2.length;
        }

        return result;

    }

    public ArrayList<Lyric> getLyrics() {


        return lyrics;


    }

    public boolean isLyric() {
        return isLyric;
    }

}
