package com.gizmohub.sdk.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

public class FileUtils {
    private static String IMEI = "";

    public static String getFileMD5(String filePath) {
        File file = new File(filePath);
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public static String getDataRootDir(Context context) {
        Predication.checkNotNull("context", context);
        return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();
    }

    public static String getPicPath(Context context,String md5) {
        String path = getDataRootDir(context);
        return path + File.separator + md5 + ".jpg";
    }

    public static void renameFile(String oldFilePath, String newFilePath) {
        File oldFile = new File(oldFilePath);
        File newFile = new File(newFilePath);
        oldFile.renameTo(newFile);
    }

    public static String getImei(Context context) {
        if(!TextUtils.isEmpty(IMEI)) return IMEI;
        if (context == null) return "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }else {
            IMEI = telephonyManager.getDeviceId();
            return IMEI;
        }
    }

    public static void deleteFile(Context context,String md5){
        if(context == null || TextUtils.isEmpty(md5)) return;
        File file = new File(getPicPath(context,md5));
        file.delete();
    }
}
