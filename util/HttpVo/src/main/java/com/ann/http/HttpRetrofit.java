package com.ann.http;

import android.content.Context;

import com.ann.http.cache.GetCacheIntercept;
import com.ann.http.cache.PostCacheInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求方法的集合类，该类初始化了Retrofit，封装了RxJava
 */
public class HttpRetrofit {


    /**
     * 超时时间 ：s
     */
    private  final int DEFAULT_TIMEOUT = 12;

    /**
     * baseurl
     */
    public static final String BASE_URL = "http://192.168.2.250:8018/";

//    public static final String BASE_URL = "http://fxxc.ldynet.cn/";

    /**
     * 单例模式
     * 私有化构造方法
     * 在构造方法中完成对OkHttpClient、Retrofit、HttpService的初始化
     * <p/>
     * <p/>
     * <li>{@link OkHttpClient}</li>
     * <li>{@link Retrofit}</li>
     * <li>{@link HttpLoggingInterceptor.Level}</li>
     * <li>{@link RxJava2CallAdapterFactory}</li>
     */
    public  Retrofit HttpMethods() {
        /*
        打印log
        以及log的级别
         */
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        /*OKHttpClient对象*/
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
//                .addInterceptor(new PostCacheInterceptor(context,cacheTime))
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();


        /*Retrofit对象*/
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();

        return retrofit;

    }

    public static HttpRetrofit getInstance() {
        return SingletonHolder.INSTANCE;
    }



    /**
     * 获取单例的静态内部类
     */
    private static class SingletonHolder {
        private static HttpRetrofit INSTANCE = new HttpRetrofit();
    }

}
