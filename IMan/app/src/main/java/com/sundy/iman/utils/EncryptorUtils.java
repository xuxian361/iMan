package com.sundy.iman.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sundy on 17/9/20.
 */

public class EncryptorUtils {
    private static final byte[] iv = {9, 3, 2, 0, 0, 5, 24, -1}; // 长度必须为8
    private static final String desKey = "gdyj.key"; // 长度必须为8

    /**
     * MD5加密 16位算法
     *
     * @param plaintext
     * @return
     */
    public static String encryptByMD516(String plaintext) {

        if (plaintext != null) {
            try {
                plaintext = plaintext.toLowerCase();
                char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(plaintext.getBytes());
                byte tmp[] = md.digest();
                char str[] = new char[16 * 2];
                int k = 0;
                for (int i = 0; i < 16; i++) {
                    byte byte0 = tmp[i];
                    str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // >>>(无符号右移运算符),高位补0
                    str[k++] = hexDigits[byte0 & 0xf];
                }
                return new String(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * MD5加密，32位算法
     *
     * @param str
     * @return
     */
    public static String encryptByMD532(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();

        StringBuilder md5StrBuff = new StringBuilder();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        // 16位加密，从第9位到25位
        // return md5StrBuff.substring(8, 24).toString().toUpperCase();
        return md5StrBuff.toString();
    }

    public static String encryptByDES(String plaintext) {
        if (plaintext != null) {
            try {
                IvParameterSpec ips = new IvParameterSpec(iv);
                byte[] cipherByte = null;
                SecretKeySpec sks = new SecretKeySpec(desKey.getBytes(), "DES");
                Cipher cer = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cer.init(Cipher.ENCRYPT_MODE, sks, ips);
                cipherByte = cer.doFinal(plaintext.getBytes());
                return Base64.encodeToString(cipherByte, Base64.DEFAULT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String decryptByDES(String plaintext) {
        if (plaintext != null) {
            try {
                IvParameterSpec ips = new IvParameterSpec(iv);
                byte[] cipherByte = null;
                SecretKeySpec sks = new SecretKeySpec(desKey.getBytes(), "DES");
                Cipher cer = Cipher.getInstance("DES/CBC/PKCS5Padding");
                cer.init(Cipher.DECRYPT_MODE, sks, ips);
                cipherByte = Base64.decode(plaintext, Base64.DEFAULT);
                cipherByte = cer.doFinal(cipherByte);
                return new String(cipherByte);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * SHA加密
     */
    public static String encryptSHA(String data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance("SHA");
        return new BigInteger(sha.digest(data.getBytes())).toString(32);
    }
}
