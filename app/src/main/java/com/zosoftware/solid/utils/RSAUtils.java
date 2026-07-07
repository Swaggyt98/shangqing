package com.zosoftware.solid.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtils {

    static String publickey_str =  "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+mEDzYLbKNB9rbOuvGgwdBUpPaHryRGarxBQppkOzlj+ouep8MMq1Xg7NBkjLOV2vnn4E5AVvX0XVOmBg8W5eNQ1uS1HCG2fie8BpXGgl1pWj/HYIrA2d/U7xxvMO8UMhAGfMdaGrPrGdZTr95pzL/q+VJZOcqSAgux/YEdu11wIDAQAB"
            ;
    public static String encryptWithPublicKey(String text) {
        try {
            // 将公钥Base64编码转换为字节
            byte[] publicKeyBytes = Base64.decode(publickey_str, Base64.DEFAULT);
            // 将公钥字节转换为Key对象
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // 使用公钥加密
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(text.getBytes());

            // 将加密后的数据转换为Base64编码的字符串
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
