package com.storm.mobileplayer.httpclient;


import com.storm.mobileplayer.utils.Api;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Storm on 2017/4/29.
 * 网络请求的工具类
 */

public class RetrofitClientManager {


    private static OkHttpClient httpClient;
    private static volatile Retrofit mRetrofit;
    private static ClientSerivice clientSerivice;




    public static Retrofit getRetrofit(){
        if (mRetrofit == null) {
            synchronized (RetrofitClientManager.class) {
                if (mRetrofit == null) {
                    HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    //设置okhttp
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(httpLoggingInterceptor)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();
                    //获取retrofit 的实例
                    mRetrofit = new Retrofit
                            .Builder()
                            .baseUrl(Api.BASE_URL)
                            .client(httpClient)
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                }
            }
        }
        return mRetrofit;
    }

    public static ClientSerivice getClientSerivice(){
        if (clientSerivice == null) {
            clientSerivice = getRetrofit().create(ClientSerivice.class);
        }
        return  clientSerivice;
    }

//    public static Retrofit getNewsRetrofit(){
//        Retrofit retrofit = getRetrofit();
//        retrofit = retrofit.newBuilder().baseUrl(Api.NEWS_RUL).build();
//        return retrofit;
//    }




}
