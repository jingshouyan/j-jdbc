package com.github.jingshouyan.jdbc.core.util.aes;

import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * aes 工具类
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
public class AesUtil {

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return 加密后字符串
     */
    @SneakyThrows
    public static String encrypt(String content, String password) {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kgen.init(128,random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        byte[] byteContent = content.getBytes("utf-8");
        // 初始化
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 加密
        byte[] result = cipher.doFinal(byteContent);
        return base64Encode(result);
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return 解密后字符串
     */
    @SneakyThrows
    public static String decrypt(String content, String password) {
        byte[] buf = base64Decode(content);
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(password.getBytes());
        kgen.init(128,random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, key);
        //解密
        byte[] result = cipher.doFinal(buf);
        return new String(result, "utf-8");
    }


    private static String base64Encode(byte[] buf) {
        return BaseEncoding.base64().encode(buf);
    }

    private static byte[] base64Decode(String content) {
        return BaseEncoding.base64().decode(content);
    }

}
