package com.gizmohub.sdk.parameter.car;

import com.gizmohub.sdk.parameter.BaseParameter;

public class CarParameter extends BaseParameter {
    //public static String BASE_URL = "https://gizmohub.com/models/";
    public static String BASE_URL = "http://192.168.1.181:9999/";

    private String uid;
    public CarStateInfo carStateInfo;
    public boolean forceUpdate;
//    private String timestamp;


    public CarParameter(String uid,CarStateInfo carStateInfo,boolean forceUpdate){
        this.uid = uid;
        this.carStateInfo = carStateInfo;
        this.forceUpdate = forceUpdate;
//        this.timestamp = String.valueOf(System.currentTimeMillis()/1000);
    }

    @Override
    public String toURL() throws NullPointerException {
        String url;
        if(carStateInfo == null){
            url = BASE_URL + uid +"/embed";
        }else {
            url = BASE_URL + uid +"/embed?postInitialize=1";
        }

        return url;
    }
}
