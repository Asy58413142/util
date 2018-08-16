package com.ann.http.cache;

import android.content.Context;

import com.ann.http.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by anliyuan on 2017/11/22.
 */

public class GetCacheIntercept implements Interceptor {

    private Context context;
    private int cacheTime;

    public GetCacheIntercept(Context context, int cacheTime) {
        this.context = context;
        this.cacheTime = cacheTime;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //将请求拦截下来
        Request request = chain.request();
        //没网，强制取缓存
        if (!NetworkUtils.isAvailable(context)) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        } else {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_NETWORK)
                    .build();
        }

        Response response = chain.proceed(request);
        //网络可用，设置缓存的过期时间
        if (NetworkUtils.isAvailable(context)) {
            response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + cacheTime)
                    .build();
        } else {
            //没网
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale没网的时候，设置缓存过期时间4周
            response.newBuilder().removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }

        return response;
    }
}
