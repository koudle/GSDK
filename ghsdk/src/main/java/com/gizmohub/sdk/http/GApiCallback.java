package com.gizmohub.sdk.http;

/**
 * Created by kl on 18-3-18.
 */

public interface GApiCallback {
    void onReceive(String content);
    void onError(int code);
}
