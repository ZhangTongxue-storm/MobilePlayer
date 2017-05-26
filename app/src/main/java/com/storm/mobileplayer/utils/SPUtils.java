package com.storm.mobileplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Storm on 2017/5/26.
 * sp工具类
 */

public class SPUtils {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public void getSharedPreferences(Context context, String spName) {
        sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }


    /**
     * String 类型
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        editor.putString(key, value).commit();
    }

    public String readString(String key) {
        return sp.getString(key, null);
    }

    /**
     * '
     * int 类型
     *
     * @param key
     * @param value
     */
    public void put(String key, int value) {
        editor.putInt(key, value).commit();
    }

    public int readInt(String key) {
        return sp.getInt(key, -1);
    }

    /**
     * boolean 类型的值
     *
     * @param key
     * @param value
     */
    public void put(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public boolean readBoolean(String key) {
        return sp.getBoolean(key, false);
    }

}
