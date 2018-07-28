package com.gizmohub.sdk.parameter.model;

import com.gizmohub.sdk.GHSDK;
import com.gizmohub.sdk.parameter.BaseParameter;
import com.gizmohub.sdk.utils.Predication;
import com.gizmohub.sdk.utils.SignatureUtil;

/**
 * Created by kl on 18-3-18.
 */

public class ModelParameters extends BaseParameter {
    //模型浏览接口
    private static final String DISPLAY_URL = "https://gizmohub.com/models/%s/embed?tools=00000";
    private static final String AUTOSTART = "autostart";
    private static final String AUTOLOAD = "autoload";

    private String content_id;
    private String timestamp;
    private String autostart;
    private String autoload;

    public ModelParameters(String content_id, boolean autostart, boolean autoload){
        this.content_id = content_id;
        this.timestamp = String.valueOf(System.currentTimeMillis()/1000);
        this.autostart = autostart ? "1" : "0";
        this.autoload = autoload ? "1" : "0";
    }

    @Override
    public String toOnLineURL() throws NullPointerException{
        Predication.checkNotNullString(CONTENT_ID,content_id);
        Predication.checkNotNullString(ACCESSKEY, GHSDK.sAccessKey);
        Predication.checkNotNullString(TIMESTAMP,timestamp);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.format(DISPLAY_URL,content_id))
                .append("&").append(ACCESSKEY).append("=").append(GHSDK.sAccessKey)
                .append("&").append(TIMESTAMP).append("=").append(timestamp)
                .append("&").append(SIGNATURE).append("=").append(SignatureUtil.signature(GHSDK.sAccessKey, GHSDK.sToken,timestamp))
                .append("&").append(AUTOSTART).append("=").append(this.autostart)
                .append("&").append(AUTOLOAD).append("=").append(this.autoload);
        return stringBuffer.toString();
    }

}
