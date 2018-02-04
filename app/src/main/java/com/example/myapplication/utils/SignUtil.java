package com.example.myapplication.utils;

import java.security.MessageDigest;

public class SignUtil {
    public static final String KEY = "3FAE198D505B6B14AE9F361EDCBE323C";

    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");

            messageDigest.reset();

            messageDigest.update(str.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();

            StringBuilder md5StrBuff = new StringBuilder();

            for (byte aByteArray : byteArray) {
                if (Integer.toHexString(0xFF & aByteArray).length() == 1)
                    md5StrBuff.append("0").append(
                            Integer.toHexString(0xFF & aByteArray));
                else
                    md5StrBuff.append(Integer.toHexString(0xFF & aByteArray));
            }
            return md5StrBuff.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //sign加密规则，Md5(json+key);
    public static String jsonToMd5(String json) {
        String str = getMD5Str(json + KEY);
        return str;
    }
}
