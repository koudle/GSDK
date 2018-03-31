package com.gizmo.gsdk.view;

import android.text.TextUtils;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;


import com.gizmo.gsdk.config.AppConfig;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by kl on 18-3-18.
 */

public class GViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, String s) {
        //        String url = request.getUrl().toString();
////        if(TextUtils.isEmpty(url) || !url.contains(AppConfig.HOST)){
////            loadErrorPage(view,"不能加载"+AppConfig.HOST+"以外的页面");
////            return true;
////        }
        webView.loadUrl(s);
        return true;
    }


    private void loadErrorPage(WebView view,String tips){
        String errorHtml = "<html><body style='background-color:#e5e5e5;'><h1>%s</h1></body></html>";
        errorHtml = String.format(errorHtml,tips);
        view.loadData(errorHtml, "text/html", "UTF-8");
    }
}
