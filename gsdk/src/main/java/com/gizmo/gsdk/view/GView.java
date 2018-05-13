package com.gizmo.gsdk.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gizmo.gsdk.BuildConfig;
import com.gizmo.gsdk.R;
import com.gizmo.gsdk.cacheWebView.CacheWebView;
import com.gizmo.gsdk.cacheWebView.WebViewCache;
import com.gizmo.gsdk.config.AppConfig;
import com.gizmo.gsdk.parameter.BaseParameter;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;

/**
 * Created by kl on 18-3-18.
 */

public class GView extends LinearLayout {

    private CacheWebView webView;
    private ProgressBar progressBar;
    private volatile boolean isFinish = false;
    private volatile boolean isToggleAr = false;

    public GView(Context context) {
        this(context,null);
    }

    public GView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @TargetApi(21)
    public GView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initGView();
    }

    private void initGView(){
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(Color.TRANSPARENT);
        View.inflate(getContext(), R.layout.gview, this);
        webView = (CacheWebView) findViewById(R.id.web_view);
        webView.setCacheStrategy(WebViewCache.CacheStrategy.FORCE);
        CacheWebView.getCacheConfig().init(getContext(),getContext().getExternalCacheDir()+File.separator+"web",1024*1024*100,1024*1024*10)
                .enableDebug(true);//100M 磁盘缓存空间,10M 内存缓存空间
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initWebSettings();
    }

    protected void initWebSettings() {
        webView.setWebViewClient(new GViewClient());
        webView.setWebChromeClient(new GChromeClient());
        webView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings = webView.getSettings();
        if (webSettings == null) {
            return;
        }

        int apiLevel = android.os.Build.VERSION.SDK_INT;
        if (apiLevel >= android.os.Build.VERSION_CODES.FROYO) {
            webSettings.setPluginState(WebSettings.PluginState.ON);
        }
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        this.setVerticalScrollBarEnabled(false);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setAllowContentAccess(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCachePath(this.getContext().getCacheDir().toString());
        webSettings.setAppCacheEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        String rawUserAgent = webView.getSettings().getUserAgentString();
        webSettings.setUserAgentString(rawUserAgent + " " + AppConfig.UA_TYPE + "/" + BuildConfig.VERSION_NAME + "_" + Build.VERSION.RELEASE);
//        webSettings.setUserAgentString(rawUserAgent + " Now/" + versionName + "_" + Build.VERSION.RELEASE);
        webSettings.setMixedContentMode(2);


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

    }

    public void load3D(BaseParameter parameters){
        if(parameters == null || webView == null) return;
        String url = parameters.toURL();
        webView.loadUrl(url);
    }

    public void load3D(String url){
        if(TextUtils.isEmpty(url)) return;
        webView.loadUrl(url);
    }

    public void toggleAr(){
        if(isFinish) {
            webView.loadUrl("javascript:toggleAr()");
        }else {
            isToggleAr = true;
        }
    }

    public boolean goBack(){
        if(webView == null) return false;
        if(webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        if(webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            this.removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDetachedFromWindow();
    }

    class GChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(progressBar == null) return;
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else {
                if (progressBar.getVisibility() == GONE) progressBar.setVisibility(VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    class GViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String s) {
            //        String url = request.getUrl().toString();
////        if(TextUtils.isEmpty(url) || !url.contains(AppConfig.HOST)){
////            loadErrorPage(view,"不能加载"+AppConfig.HOST+"以外的页面");
////            return true;
////        }
            if(s.startsWith("http")) {
                webView.loadUrl(s);
            }
            return true;
        }


        private void loadErrorPage(WebView view,String tips){
            String errorHtml = "<html><body style='background-color:#e5e5e5;'><h1>%s</h1></body></html>";
            errorHtml = String.format(errorHtml,tips);
            view.loadData(errorHtml, "text/html", "UTF-8");
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            isFinish = true;
            if(isToggleAr) {
                webView.loadUrl("javascript:toggleAr()");
                isToggleAr = false;
            }

        }
    }
}
