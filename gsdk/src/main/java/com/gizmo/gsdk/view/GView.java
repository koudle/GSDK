package com.gizmo.gsdk.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.gizmo.gsdk.BuildConfig;
import com.gizmo.gsdk.R;
import com.gizmo.gsdk.config.AppConfig;
import com.gizmo.gsdk.parameter.BaseParameter;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by kl on 18-3-18.
 */

public class GView extends LinearLayout {

    private WebView webView;
    private ProgressBar progressBar;

    public GView(Context context) {
        this(context,null);
    }

    public GView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public GView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initGView();
    }

    private void initGView(){
        this.setOrientation(VERTICAL);
        this.setBackgroundColor(Color.TRANSPARENT);
        View.inflate(getContext(), R.layout.gview, this);
        webView = (WebView) findViewById(R.id.web_view);
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
}
