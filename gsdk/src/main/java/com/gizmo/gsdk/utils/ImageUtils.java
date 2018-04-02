package com.gizmo.gsdk.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public class ImageUtils {

    private static final int DEST_WIDTH = 360;
    /**
     * 旋转图片
     * @param bitmap
     * @param rotation
     * @Return
     */
    public static Bitmap getRotatedBitmap(Bitmap bitmap, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    /**
     * 镜像翻转图片
     * @param bitmap
     * @Return
     */
    public static Bitmap getFlipBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.setScale(-1, 1);
        matrix.postTranslate(bitmap.getWidth(), 0);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, false);
    }

    public static Bitmap byteToBitmap(byte[] data, Camera camera){
        if(data == null || camera == null) return null;
        Camera.Size size = camera.getParameters().getPreviewSize();
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width,
                    size.height, null);
            if (image != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, size.width, size.height),
                        80, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(
                        stream.toByteArray(), 0, stream.size());
                stream.close();
                return bmp;
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static String savaBitmap(Bitmap bitmap, Context context) {
        if(bitmap == null || context == null) return null;
        //缩放
        int scaleWidth = 0;
        int scaleX = 0;
        int scaleY = 0;
        float scale = 1;
        if(bitmap.getWidth() > bitmap.getHeight()){
            scaleWidth = bitmap.getHeight() * 2 / 3 ;
        }else {
            scaleWidth = bitmap.getHeight() * 2 / 3 ;
        }
        scaleX = (bitmap.getWidth() - scaleWidth)/2;
        scaleY = (bitmap.getHeight() - scaleWidth)/2;
        scale =(float)360/ (float)scaleWidth;
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap,scaleX,scaleY,scaleWidth,scaleWidth,matrix,true);
        String path = FileUtils.getDataRootDir(context) + File.separator + SystemClock.uptimeMillis() +".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            fout.close();

            String MD5 = FileUtils.getFileMD5(path);
            String newPath = FileUtils.getDataRootDir(context) + File.separator + MD5 + ".jpg";
            FileUtils.renameFile(path,newPath);
            return MD5;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}

