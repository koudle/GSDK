package com.gizmohub.sdk.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kl on 18-3-18.
 */

public class SignatureUtil {

    public static String signature(String email,String token,String timeStamp){
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            String content = email+token+timeStamp;
            digest.update(content.getBytes());
            //获取字节数组
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return  Base64.encodeToString(hexString.toString().getBytes(),Base64.DEFAULT).replace("\n","");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
