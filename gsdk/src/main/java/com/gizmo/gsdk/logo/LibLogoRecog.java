package com.gizmo.gsdk.logo;

public class LibLogoRecog {
    static {
        System.loadLibrary("OpenCV");
    }

    public static native int loadLogos(String url,boolean clear);
    public static native int recognize(String imagePath);
    public static native void init();
}
