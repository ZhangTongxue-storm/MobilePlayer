package com.storm.mobileplayer.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Storm on 2017/4/19.
 */


public class CloseUtils {


    public CloseUtils() {
        throw new UnsupportedOperationException(" u can't instantiate me.....");
    }


    /**
     * 关闭 io
     *
     * @param closeables
     */
    public static void CloseIO(Closeable... closeables) {
        if (closeables == null) return;

        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
