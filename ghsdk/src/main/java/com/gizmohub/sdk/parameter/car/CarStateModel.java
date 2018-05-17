package com.gizmohub.sdk.parameter.car;

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

