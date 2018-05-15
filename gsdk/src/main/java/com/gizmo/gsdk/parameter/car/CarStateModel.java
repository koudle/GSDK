package com.gizmo.gsdk.parameter.car;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CarStateModel {
    public String uid;
    public String name;

    public CarStateModel(){
    }

    public CarStateModel setUid(String uid){
        this.uid = uid;
        return this;
    }

    public CarStateModel setName(String name){
        this.name = name;
        return this;
    }

    public static CarStateModel build(){
        return new CarStateModel();
    }

}

