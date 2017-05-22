package com.storm.mobileplayer.httpclient;


import com.storm.mobileplayer.bean.NetVideoBean;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Storm on 2017/4/30.]
 */

public interface ClientSerivice {


    @GET("PageSubArea/TrailerList.api/")
    Observable<NetVideoBean> getNetVideoService();



}
