package com.gizmo.gsdk.logo;

public class LogoResult {
    public String name;
    public String id;
    public long cost;

    public LogoResult(String name,String id){
        this.name = name;
        this.id = id;
    }

    public void setCost(long cost){
        this.cost =cost;
    }
}
