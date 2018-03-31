package com.gizmo.gsdk.utils;

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
            return  Base64.encodeToString(digest.digest(),Base64.DEFAULT);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
