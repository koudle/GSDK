package com.gizmo.gsdk.cacheWebView.jsbridge;

public interface WebViewJavascriptBridge {

    public void send(String data);
    public void send(String data, CallBackFunction responseCallback);


}
