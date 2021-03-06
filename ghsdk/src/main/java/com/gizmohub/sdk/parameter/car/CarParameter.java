package com.gizmohub.sdk.parameter.car;

import com.gizmohub.sdk.GHSDK;
import com.gizmohub.sdk.parameter.BaseParameter;
import com.gizmohub.sdk.utils.SignatureUtil;

public class CarParameter extends BaseParameter {
    public static String BASE_URL = "https://gizmohub.com/viewer/ycyh/i/";
//    public static String BASE_URL = "http://192.168.1.181:9999/";

    public String uid;
    public CarStateInfo carStateInfo;
//    public boolean forceUpdate;
    private String timestamp;


    public CarParameter(String uid,CarStateInfo carStateInfo){
        this.uid = uid;
        this.carStateInfo = carStateInfo;
//        this.forceUpdate = forceUpdate;
        this.timestamp = String.valueOf(System.currentTimeMillis()/1000);
    }

    @Override
    public String toOnLineURL() throws NullPointerException {
        String url;
        if(carStateInfo == null){
            url = BASE_URL + uid +"?";
        }else {
            url = BASE_URL + uid +"?postInitialize=1&";
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(url)
                .append(ACCESSKEY).append("=").append(GHSDK.sAccessKey)
                .append("&").append(TIMESTAMP).append("=").append(timestamp)
                .append("&").append(SIGNATURE).append("=").append(SignatureUtil.signature(GHSDK.sAccessKey, GHSDK.sToken,timestamp));

        return stringBuffer.toString();
    }
}
