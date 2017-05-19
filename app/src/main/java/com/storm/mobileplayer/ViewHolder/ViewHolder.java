package com.storm.mobileplayer.ViewHolder;

import android.util.SparseArray;
import android.view.View;

/**
 * Created by Storm on 2017/5/19.\
 */

public class ViewHolder {

    public static <T extends View> T getView(View view, int viewId) {

        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<>();
            view.setTag(viewHolder);
        }

        View childView = viewHolder.get(viewId);
        if (childView == null) {
            childView = view.findViewById(viewId);
            viewHolder.put(viewId, childView);
        }
        return (T) childView;
    }
}
