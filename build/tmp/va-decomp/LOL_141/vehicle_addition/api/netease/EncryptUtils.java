/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.api.netease;

import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    public static String encryptedParam(String text) throws Exception {
        if (text == null) {
            return "params=null&encSecKey=null";
        }
        String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
        String nonce = "0CoJUm6Qyw8W8jud";
        String pubKey = "010001";
        String secKey = EncryptUtils.getRandomString();
        String encText = EncryptUtils.aesEncrypt(EncryptUtils.aesEncrypt(text, nonce), secKey);
        String encSecKey = EncryptUtils.rsaEncrypt(secKey, pubKey, modulus);
        return "params=" + URLEncoder.encode(encText, "UTF-8") + "&encSecKey=" + URLEncoder.encode(encSecKey, "UTF-8");
    }

    private static String aesEncrypt(String text, String key) throws Exception {
        IvParameterSpec ivParameterSpec = new IvParameterSpec("0102030405060708".getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, (Key)secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private static String rsaEncrypt(String text, String pubKey, String modulus) {
        BigInteger mod;
        BigInteger exp;
        BigInteger val = new BigInteger(1, (text = new StringBuilder(text).reverse().toString()).getBytes());
        StringBuilder hexString = new StringBuilder(val.modPow(exp = new BigInteger(pubKey, 16), mod = new BigInteger(modulus, 16)).toString(16));
        if (hexString.length() >= 256) {
            return hexString.substring(hexString.length() - 256);
        }
        while (hexString.length() < 256) {
            hexString.insert(0, "0");
        }
        return hexString.toString();
    }

    private static String getRandomString() {
        StringBuilder stringBuffer = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < 16; ++i) {
            stringBuffer.append(Integer.toHexString(r.nextInt(16)));
        }
        return stringBuffer.toString();
    }
}

