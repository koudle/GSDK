package com.gizmohub.sdk.cacheWebView;

import android.util.Log;

import com.gizmohub.sdk.cacheWebView.config.CacheConfig;



class CacheWebViewLog {
    private static final String TAG="CacheWebView";


    public static void d(String log){
        if (CacheConfig.getInstance().isDebug()){
            Log.d(TAG,log);
        }
    }
}