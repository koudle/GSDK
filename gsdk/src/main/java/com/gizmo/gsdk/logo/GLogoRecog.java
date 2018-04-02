package com.gizmo.gsdk.logo;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.HmacSHA1Signature;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.gizmo.gsdk.utils.DeviceUtils;
import com.gizmo.gsdk.utils.FileUtils;
import com.gizmo.gsdk.utils.ImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GLogoRecog {
    private static final int GET_RESULT_DELAY_TIME = 200;
    private static final int UPLOAD_PIC_INTERVAL = 1000;

    private ExecutorService executorService;
    private Context context;
    private Handler handler;
    private LogoResultListener logoResultListener;
    private long previewTime = 0;
    private OkHttpClient client ;


    private class InternalHandler extends Handler {
        private Context context;
        public InternalHandler(Context context) {
            super(Looper.getMainLooper());
            this.context = context;
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(logoResultListener != null) {
                        logoResultListener.onRecoging();
                    }
                    break;
                case 1:
                    if(msg.obj instanceof LogoResult) {
                        LogoResult logoResult = (LogoResult) msg.obj;
                        if(logoResult != null && logoResultListener!= null) {
                            logoResultListener.onRecogSuceess(logoResult);
                        }
                    }
                    break;
            }
        }
    }

    public GLogoRecog(Context context){
        this.context = context;
        client = new OkHttpClient();
        executorService = Executors.newFixedThreadPool(DeviceUtils.getNumberOfCPUCores()*2);
        handler = new InternalHandler(context);
    }

    public void recognize(final Bitmap bitmap) {
        if(shouldAbandon()) return;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.uptimeMillis();
                recognize(startTime,ImageUtils.savaBitmap(bitmap,context));
            }
        });
    }

    public void recognize(final byte[] data, final Camera camera){
        if(shouldAbandon()) return;
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                long startTime = SystemClock.uptimeMillis();
                recognize(startTime,ImageUtils.savaBitmap(ImageUtils.byteToBitmap(data,camera),context));
            }
        });
    }

    public void setLogoResultListener(LogoResultListener listener) {
        this.logoResultListener = listener;
    }

    private boolean shouldAbandon(){
        if(previewTime <= 0) {
            previewTime = SystemClock.uptimeMillis();
            Log.d("recogn","shouldAbandon:false");
            return false;
        }else {
            if(SystemClock.uptimeMillis() - previewTime >= UPLOAD_PIC_INTERVAL) {
                Log.d("recogn","shouldAbandon:false");
                return false;
            }else {
                Log.d("recogn","shouldAbandon:true");
                return true;
            }
        }
    }


    private void recognize(final long startTime,final String imageName) {

        if(TextUtils.isEmpty(imageName)) return;

        String objectKey = null;
        String sign = null;
        String AccessKeyId = null;
        final JSONObject callback;

        Request request = new Request.Builder()
                .url(String.format("http://logorec.gizmocloud.cn/%s/upload_policy?md5=%s.jpg", FileUtils.getImei(context),imageName))
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject object = new JSONObject(response.body().string());
            sign = object.getString("signature");
            objectKey = object.getString("dir")+imageName+".jpg";
            AccessKeyId = object.getString("accessid");
            callback = new JSONObject(new String(Base64.decode(object.getString("callback"),Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        String endpoint = "http://oss-cn-shenzhen.aliyuncs.com";
        OSSCustomSignerCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 您需要在这里依照OSS规定的签名算法，实现加签一串字符内容，并把得到的签名传拼接上AccessKeyId后返回
                // 一般实现是，将字符内容post到您的业务服务器，然后返回签名
                // 如果因为某种原因加签失败，描述error信息后，返回nil

                // 以下是用本地算法进行的演示
                HmacSHA1Signature signature = new HmacSHA1Signature();
                return "OSS " + "LTAIQtfCFtRLRmn1" + ":" + signature.computeSignature("TMrwPDw3Ani1gHQMJxJpE1QXlyNR6j",content) ;
//                        return "OSS " + AccessKeyId +":" + sign;
            }
        };

        OSS oss = new OSSClient(context, endpoint, credentialProvider);
        PutObjectRequest put = new PutObjectRequest("gizmohub", objectKey, FileUtils.getPicPath(context,imageName));

        put.setCallbackParam(new HashMap<String, String>() {
            {
                try {
                    put("callbackUrl", callback.getString("callbackUrl"));
                    put("callbackHost", callback.getString("callbackHost"));
                    put("callbackBodyType", callback.getString("callbackBodyType"));
                    put("callbackBody", callback.getString("callbackBody"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                // 只有设置了servercallback，这个值才有数据
                String serverCallbackReturnJson = result.getServerCallbackReturnBody();
                Log.d("recogn", serverCallbackReturnJson);
                FileUtils.deleteFile(context,imageName);
                asyncGetRecognResult(startTime);
            }
            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 异常处理
            }
        });

    }

    private void asyncGetRecognResult(long startTime){
        Log.d("recogn","asyncGetRecognResult");

        try {
            Thread.sleep(GET_RESULT_DELAY_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Request request = new Request.Builder()
                .url(String.format("http://logorec.gizmocloud.cn/result/%s", FileUtils.getImei(context)))
                .build();
        try {
            Response response = client.newCall(request).execute();
            JSONObject object = new JSONObject(response.body().string());
            if(object.has("rv")) {
                String name = object.getJSONObject("rv").getString("name");
                String id = object.getJSONObject("rv").getString("id");
                LogoResult logoResult = new LogoResult(name,id);
                logoResult.setCost(SystemClock.uptimeMillis() - startTime);
                Message message = Message.obtain();
                message.what = 1;
                message.obj = logoResult;
                handler.sendMessage(message);
            }else {
                Message message = Message.obtain();
                message.what = 0;
                handler.sendMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
