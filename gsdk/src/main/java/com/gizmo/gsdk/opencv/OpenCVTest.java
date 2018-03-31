package com.gizmo.gsdk.opencv;

/**
 * Created by kl on 18-3-25.
 */

public class OpenCVTest {
    static {
        System.loadLibrary("OpenCV");
    }

    public static native int[] gray(int buf[],int w,int h);
}
