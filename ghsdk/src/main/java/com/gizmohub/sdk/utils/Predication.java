package com.gizmohub.sdk.utils;

import android.text.TextUtils;

/**
 * Created by kl on 18-3-18.
 */

public class Predication {
    public static void checkNotNullString(String key, String value) throws NullPointerException{
        if(TextUtils.isEmpty(value)) throw new NullPointerException(key + "is null");
    }

    public static void checkNotNull(String key, Object value) throws NullPointerException{
        if(value == null) throw new NullPointerException(key + "is null");
    }
}
