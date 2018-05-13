package com.gizmo.gsdk.cacheWebView;

import android.util.Log;

import com.gizmo.gsdk.cacheWebView.config.CacheConfig;



class CacheWebViewLog {
    private static final String TAG="CacheWebView";


    public static void d(String log){
        if (CacheConfig.getInstance().isDebug()){
            Log.d(TAG,log);
        }
    }
}