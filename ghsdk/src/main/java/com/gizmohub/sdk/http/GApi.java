package com.gizmohub.sdk.http;

import com.gizmohub.sdk.parameter.BaseParameter;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kl on 18-3-18.
 */

public class GApi {
    //同步获取接口
    public static String getModleListaDataSync(BaseParameter parameter) {
        if(parameter == null) return null;

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request =   new Request.Builder()
                .url(parameter.toURL())
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()) {
                return response.body().toString();
            }else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void getModleListaDataAsync(BaseParameter parameter, final GApiCallback callback) {
        if(parameter == null || callback == null) return ;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request =   new Request.Builder()
                .url(parameter.toURL())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if(callback != null) {
                    callback.onError(-1);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()) {
                    if(callback != null) {
                        callback.onReceive(response.body().toString());
                    }
                }else {
                    if(callback != null) {
                        callback.onError(response.code());
                    }
                }
            }
        });

    }

}
