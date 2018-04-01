package com.gizmo.gsdk.parameter;

import android.os.Parcel;

import com.gizmo.gsdk.GSDK;
import com.gizmo.gsdk.utils.Predication;
import com.gizmo.gsdk.utils.SignatureUtil;

/**
 * Created by kl on 18-3-18.
 */

public class ModelParameters extends BaseParameter {
    //模型浏览接口
    private static final String DISPLAY_URL = "https://staging.gizmohub.com/models/%s/embed?";

    private String content_id;
    private String timestamp;


    public ModelParameters(String content_id, String timestamp){
        this.content_id = content_id;
        this.timestamp = timestamp;
    }

    @Override
    public String toURL() throws NullPointerException{
        Predication.checkNotNullString(CONTENT_ID,content_id);
        Predication.checkNotNullString(EMAIL,GSDK.sEmail);
        Predication.checkNotNullString(TIMESTAMP,timestamp);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.format(DISPLAY_URL,content_id))
                .append(EMAIL).append("=").append(GSDK.sEmail)
                .append("&").append(TIMESTAMP).append("=").append(timestamp)
                .append("&").append(SIGNATURE).append("=").append(SignatureUtil.signature(GSDK.sEmail,GSDK.sToken,timestamp));
        return stringBuffer.toString();
    }

}
