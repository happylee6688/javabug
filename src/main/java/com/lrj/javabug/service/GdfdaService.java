package com.lrj.javabug.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


/**
 * Created by Administrator on 2016/8/22.
 */
public interface GdfdaService {

    /**
     * 检测结果的list
     * @return
     */
    @POST("epout/command/ajax/com.inspur.epcommon.quicklyfactory.cmd.QuicklyQueryCmd/queryQuicklySampleInfo")
    Call<ResponseBody> getList();

    /**
     * 检测结果的详细信息
     * @return
     */
    @POST("epout/command/ajax/com.inspur.epcommon.quicklyfactory.cmd.QuicklyQueryCmd/queryQuicklySampleInfoById")
    Call<ResponseBody> getDetail();

    /**
     * 检测结果的抽检信息
     * @return
     */
    @POST("epout/command/ajax/com.inspur.epcommon.quicklyfactory.cmd.QuicklyQueryCmd/queryReport")
    Call<ResponseBody> getCheck();




    /**
     * 常用农产品list
     * @return
     */
    @POST("epout/command/ajax/com.inspur.epcommon.databasefactory.cmd.BasedataProductTypeQueryCmd/queryCommonProduct")
    Call<ResponseBody> getFramList(@Body RequestBody body);




}
