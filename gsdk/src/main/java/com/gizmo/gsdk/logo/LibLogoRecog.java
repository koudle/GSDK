package com.gizmo.gsdk.logo;

public class LibLogoRecog {
    public static native int loadLogos(String urls[],boolean clear);
    public static native int recognize(byte image[]);
    public static native void init(boolean _blur_det,float _blv,int _max_ft,int _low_sup,int _low_homo,float _dr);
}
