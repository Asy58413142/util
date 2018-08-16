package com.ann.http.cache;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by anliyuan on 2017/11/19.
 */

public class CacheManager {
    private static String SNAME = "cache_info";
    private static SharedPreferences sharedPreferences;

    //header
    public static void setCache(Context context, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SNAME,
                    Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getCache(Context context, String key, String defValue) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SNAME,
                    Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(key, defValue);
    }


    public static void cleanCacheInfo(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SNAME,
                    Context.MODE_PRIVATE);
        }

        sharedPreferences.edit().clear().commit();
    }

}
