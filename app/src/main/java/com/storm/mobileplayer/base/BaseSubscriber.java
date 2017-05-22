package com.storm.mobileplayer.base;

import android.app.ProgressDialog;
import android.content.Context;

import rx.Subscriber;

/**
 * Created by Storm on 2017/4/30.
 * 设置加载前的dialog
 *
 */

public abstract  class BaseSubscriber<T> extends Subscriber<T> {

    private Context mContext;
    public ProgressDialog dialog ;
    public BaseSubscriber(Context context) {
        mContext = context;
        dialog = new ProgressDialog(context);
        dialog.setMessage("loading....");
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog.show();

    }

    @Override
    public void onError(Throwable e) {
        dialog.dismiss();
    }

    @Override
    public void onCompleted() {
        dialog.dismiss();
    }

    @Override
    public abstract void onNext(T t);
}
