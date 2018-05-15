package com.gizmo.gsdk.cacheWebView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.gizmo.gsdk.cacheWebView.config.CacheConfig;
import com.gizmo.gsdk.cacheWebView.jsbridge.BridgeHandler;
import com.gizmo.gsdk.cacheWebView.jsbridge.BridgeUtil;
import com.gizmo.gsdk.cacheWebView.jsbridge.CallBackFunction;
import com.gizmo.gsdk.cacheWebView.jsbridge.DefaultHandler;
import com.gizmo.gsdk.cacheWebView.jsbridge.Message;
import com.gizmo.gsdk.cacheWebView.utils.FileUtil;
import com.gizmo.gsdk.cacheWebView.utils.NetworkUtils;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class CacheWebView extends WebView {

    private static final String CACHE_NAME = "CacheWebView";
    private static final int CACHE_SIZE = 200*1024*1024;
    private String mAppCachePath = "";
    private CacheWebViewClient mCacheWebViewClient;

    public static final String toLoadJs = "WebViewJavascriptBridge.js";
    Map<String, CallBackFunction> responseCallbacks = new HashMap<String, CallBackFunction>();
    Map<String, BridgeHandler> messageHandlers = new HashMap<String, BridgeHandler>();
    BridgeHandler defaultHandler = new DefaultHandler();

    private List<Message> startupMessage = new ArrayList<Message>();

    public List<Message> getStartupMessage() {
        return startupMessage;
    }

    public void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;


    private WebViewCache mWebViewCache;

    public CacheWebView(Context context) {
        super(context);
        init();
    }

    public CacheWebView(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    public CacheWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    /**
     * 获取到CallBackFunction data执行调用并且从数据集移除
     * @param url
     */
    void handlerReturnData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
            return;
        }
    }


    public void send(String data) {
        send(data, null);
    }

    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * 保存message到消息队列
     * @param handlerName handlerName
     * @param data data
     * @param responseCallback CallBackFunction
     */
    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    /**
     * list<message> != null 添加到消息集合否则分发消息
     * @param m Message
     */
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    /**
     * 分发message 必须在主线程才分发成功
     * @param m Message
     */
    void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string  为json字符串转义特殊字符
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            this.loadUrl(javascriptCommand);
        }
    }

    /**
     * 刷新消息队列
     */
    void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

                @Override
                public void onCallBack(String data) {
                    // deserializeMessage 反序列化消息
                    List<Message> list = null;
                    try {
                        list = Message.toArrayList(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                    if (list == null || list.size() == 0) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        Message m = list.get(i);
                        String responseId = m.getResponseId();
                        // 是否是response  CallBackFunction
                        if (!TextUtils.isEmpty(responseId)) {
                            CallBackFunction function = responseCallbacks.get(responseId);
                            String responseData = m.getResponseData();
                            function.onCallBack(responseData);
                            responseCallbacks.remove(responseId);
                        } else {
                            CallBackFunction responseFunction = null;
                            // if had callbackId 如果有回调Id
                            final String callbackId = m.getCallbackId();
                            if (!TextUtils.isEmpty(callbackId)) {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        Message responseMsg = new Message();
                                        responseMsg.setResponseId(callbackId);
                                        responseMsg.setResponseData(data);
                                        queueMessage(responseMsg);
                                    }
                                };
                            } else {
                                responseFunction = new CallBackFunction() {
                                    @Override
                                    public void onCallBack(String data) {
                                        // do nothing
                                    }
                                };
                            }
                            // BridgeHandler执行
                            BridgeHandler handler;
                            if (!TextUtils.isEmpty(m.getHandlerName())) {
                                handler = messageHandlers.get(m.getHandlerName());
                            } else {
                                handler = defaultHandler;
                            }
                            if (handler != null){
                                handler.handler(m.getData(), responseFunction);
                            }
                        }
                    }
                }
            });
        }
    }


    public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        // 添加至 Map<String, CallBackFunction>
        responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can call it
     * 注册处理程序,以便javascript调用它
     * @param handlerName handlerName
     * @param handler BridgeHandler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            // 添加至 Map<String, BridgeHandler>
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * unregister handler
     *
     * @param handlerName
     */
    public void unregisterHandler(String handlerName) {
        if (handlerName != null) {
            messageHandlers.remove(handlerName);
        }
    }

    /**
     * call javascript registered handler
     * 调用javascript处理程序注册
     * @param handlerName handlerName
     * @param data data
     * @param callBack CallBackFunction
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }


    private void init(){
        initData();
        initSettings();
        initWebViewClient();
    }

    private void initData() {

        mWebViewCache = new WebViewCache();
        File cacheFile = new File(getContext().getCacheDir(),CACHE_NAME);
        try {
            mWebViewCache.openCache(getContext(),cacheFile.getAbsolutePath(),CACHE_SIZE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEncoding(String encoding){
        if (TextUtils.isEmpty(encoding)){
            encoding = "UTF-8";
        }
        mCacheWebViewClient.setEncoding(encoding);
    }
    public void setCacheInterceptor(CacheInterceptor interceptor){
        mCacheWebViewClient.setCacheInterceptor(interceptor);
    }

    public static CacheConfig getCacheConfig(){
        return CacheConfig.getInstance();
    }

    public WebViewCache getWebViewCache(){
        return mWebViewCache;
    }

    public void setWebViewClient(WebViewClient client){
        mCacheWebViewClient.setCustomWebViewClient(client);
    }

    private void initWebViewClient() {
        mCacheWebViewClient = new CacheWebViewClient();
        super.setWebViewClient(mCacheWebViewClient);
        mCacheWebViewClient.setUserAgent(this.getSettings().getUserAgentString());
        mCacheWebViewClient.setWebViewCache(mWebViewCache);
    }

    public void setCacheStrategy(WebViewCache.CacheStrategy cacheStrategy){
        mCacheWebViewClient.setCacheStrategy(cacheStrategy);
        if (cacheStrategy == WebViewCache.CacheStrategy.NO_CACHE){
            setWebViewDefaultNoCache();
        }else{
            setWebViewDefaultCacheMode();
        }
    }

    public static CacheWebView cacheWebView(Context context){
        return new CacheWebView(context);
    }
    public static void servicePreload(Context context,String url){
        servicePreload(context,url,null);
    }
    public static void servicePreload(Context context,String url,HashMap<String,String> headerMap){
        if (context==null||TextUtils.isEmpty(url)){
            return;
        }
        Intent intent = new Intent(context, CachePreLoadService.class);
        intent.putExtra(CachePreLoadService.KEY_URL,url);
        if (headerMap!=null){
            intent.putExtra(CachePreLoadService.KEY_URL_HEADER,headerMap);
        }
        context.startService(intent);
    }

    public void setEnableCache(boolean enableCache){
        mCacheWebViewClient.setEnableCache(enableCache);
    }
    public void loadUrl(String url){
        if (url.startsWith("http")){
            mCacheWebViewClient.addVisitUrl(url);
        }

        super.loadUrl(url);
    }
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        mCacheWebViewClient.addVisitUrl(url);
        if (additionalHttpHeaders!=null){
            mCacheWebViewClient.addHeaderMap(url,additionalHttpHeaders);
            super.loadUrl(url,additionalHttpHeaders);
        }else{
            super.loadUrl(url);
        }

    }
    public void setBlockNetworkImage(boolean isBlock){
        mCacheWebViewClient.setBlockNetworkImage(isBlock);
    }

    private void initSettings(){
        WebSettings webSettings = this.getSettings();

        webSettings.setJavaScriptEnabled(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        webSettings.setDefaultTextEncodingName("UTF-8");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptThirdPartyCookies(this,true);
        }
        setWebViewDefaultCacheMode();
        setCachePath();

    }
    private void setWebViewDefaultNoCache(){
        WebSettings webSettings = this.getSettings();
        webSettings.setCacheMode(
                WebSettings.LOAD_NO_CACHE);
    }
    private void setWebViewDefaultCacheMode(){
        WebSettings webSettings = this.getSettings();
        if (NetworkUtils.isConnected(this.getContext()) ){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webSettings.setCacheMode(
                    WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }
    public String getUserAgent(){
        return  this.getSettings().getUserAgentString();
    }

    public void setUserAgent(String userAgent){
        WebSettings webSettings = this.getSettings();
        webSettings.setUserAgentString(userAgent);
        mCacheWebViewClient.setUserAgent(userAgent);
    }

    private void setCachePath(){

        File  cacheFile = new File(this.getContext().getCacheDir(),CACHE_NAME);
        String path = cacheFile.getAbsolutePath();
        mAppCachePath = path;

        File file = new File(path);
        if (!file.exists()){
            file.mkdirs();
        }

        WebSettings webSettings = this.getSettings();
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDatabasePath(path);
    }

    public void clearCache(){
        CacheWebViewLog.d("clearCache");
        this.stopLoading();
        clearCache(true);
        FileUtil.deleteDirs(mAppCachePath,false);
        mWebViewCache.clean();

    }

    public void destroy(){

        CacheWebViewLog.d("destroy");
        mCacheWebViewClient.clear();
        mWebViewCache.release();

        this.stopLoading();
        this.getSettings().setJavaScriptEnabled(false);
        this.clearHistory();
        this.removeAllViews();

        ViewParent viewParent = this.getParent();

        if (viewParent == null){
            super.destroy();
            return ;
        }
        ViewGroup parent = (ViewGroup) viewParent;
        parent.removeView(this);
        super.destroy();
    }

    @Override
    public void goBack() {
        if (canGoBack()){
            mCacheWebViewClient.clearLastUrl();
            super.goBack();
        }
    }

    public void evaluateJS(String strJsFunction){
        this.evaluateJS(strJsFunction,null);
    }
    public void evaluateJS(String strJsFunction,ValueCallback valueCallback){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT&&valueCallback!=null) {
            this.evaluateJavascript("javascript:"+strJsFunction, valueCallback);
        } else {
            this.loadUrl("javascript:"+strJsFunction);
        }
    }

}