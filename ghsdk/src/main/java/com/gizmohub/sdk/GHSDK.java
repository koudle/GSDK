package com.gizmohub.sdk;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by kl on 18-3-18.
 */

public class GHSDK {
    public static String sEmail = "";
    public static String sToken = "";

    public static final void init(Context context,String email, String token){
        if(!TextUtils.isEmpty(sEmail) && !TextUtils.isEmpty(sToken)) return;
        sEmail = email;
        sToken = token;
        QbSdk.initX5Environment(context, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.d("app", " onViewInitFinished is " + b);
            }
        });
    }

}
