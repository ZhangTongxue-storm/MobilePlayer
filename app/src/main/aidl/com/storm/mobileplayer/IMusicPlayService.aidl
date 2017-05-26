// IMusicPlayService.aidl
package com.storm.mobileplayer;

// Declare any non-default types here with import statements

interface IMusicPlayService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

  /**
     * 根据一个位置打开音频and 播放
     *
     * @param position
     */
     void openAudio(int position);


    /**
     * 点击开始播放
     */
     void onStart() ;

    /**
     * 暂停
     */
     void onPause();



    /**
     * 下一首
     **/
     void onNext();

    /**
     * 上一首
     */
     void onPre();

    /**
     * 获取当前播放的名字
     *
     * @return
     */
     String getMusicName();

    /**
     * 获取当前音乐的演唱者
     *
     * @return 当前音乐的演唱者
     */
     String getArtist();


    /**
     * 获取当前的进度
     *
     * @return
     */
     int currentDuration();

    /**
     * 获取总的进度
     *
     * @return
     */
     int getDuration();

    /**
     * 获取当前的播放模式
     *
     * @return
     */
     int getPlayModel();

    /**
     * 设置播放模式
     */
     void setPlayModel(int playModel);

    /**
     * 是否正在播放
     */
     boolean isPlaying();
    /**
     * 更改拖动
     */
     void seekTo(int progerss);

}
