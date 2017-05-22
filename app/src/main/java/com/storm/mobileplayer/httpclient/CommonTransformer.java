package com.storm.mobileplayer.httpclient;


import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Storm on 2017/4/30.
 */

public class CommonTransformer<T> implements Observable.Transformer<T, T> {


    @Override
    public Observable<T> call(Observable<T> tObservable) {
        return tObservable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
