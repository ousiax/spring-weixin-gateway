package me.codefarm.gateway.weixin.mp.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.encoders.Hex;

/**
 * 开放数据校验与解密
 * 
 * @see https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html
 */
public class OpenData {
    /**
     * 数据签名校验
     * 
     * @param signature
     * @param rawData
     * @param sessionKey
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static boolean verify(
            String signature,
            String rawData,
            String sessionKey) throws NoSuchAlgorithmException {
        if (signature == null) {
            throw new IllegalArgumentException("signature is null.");
        }
        if (rawData == null) {
            throw new IllegalArgumentException("rawData is null.");
        }
        if (sessionKey == null) {
            throw new IllegalArgumentException("sessionKey is null.");
        }

        var md = MessageDigest.getInstance("SHA-1");
        var message = (rawData + sessionKey);
        var dgst = md.digest(message.getBytes()); // StandardCharsets.UTF_8?
        var hex = Hex.toHexString(dgst);
        return signature.equals(hex);
    }

    /**
     * 加密数据解密
     * 
     * @param encryptedData
     * @param iv
     * @param sessionKey
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    public static byte[] decrypt(
            String encryptedData,
            String iv,
            String sessionKey)
            throws NoSuchAlgorithmException,
            NoSuchPaddingException,
            InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        if (encryptedData == null) {
            throw new IllegalArgumentException("encryptedData is null.");
        }
        if (sessionKey == null) {
            throw new IllegalArgumentException("sessionKey is null.");
        }
        if (iv == null) {
            throw new IllegalArgumentException("iv is null.");
        }

        var base64Decoder = Base64.getDecoder();
        var skSpec = new SecretKeySpec(base64Decoder.decode(sessionKey), "AES");
        var ivSpec = new IvParameterSpec(base64Decoder.decode(iv));
        var ciper = Cipher.getInstance("AES/CBC/PKCS5PADDING"); // PKCS7PADDING
        ciper.init(Cipher.DECRYPT_MODE, skSpec, ivSpec);
        var data = ciper.doFinal(base64Decoder.decode(encryptedData));
        return data;
    }
}