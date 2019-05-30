package com.github.jingshouyan.jdbc.core.encryption.aes;

import com.github.jingshouyan.jdbc.core.encryption.Encryption;
import com.google.common.io.BaseEncoding;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * @author jingshouyan
 * #date 2019/1/17 16:58
 */

public class Aes implements Encryption {

    public static final String CHARSET = "utf-8";
    public static final String CIPHER_ALGORITHM = "Aes";
    public static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return 加密后字符串
     */
    @Override
    @SneakyThrows
    public String encrypt(String content, String password) {
        KeyGenerator kgen = KeyGenerator.getInstance(CIPHER_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        random.setSeed(password.getBytes(CHARSET));
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, CIPHER_ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] byteContent = content.getBytes(CHARSET);
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
    @Override
    @SneakyThrows
    public String decrypt(String content, String password) {
        byte[] buf = base64Decode(content);
        KeyGenerator kgen = KeyGenerator.getInstance(CIPHER_ALGORITHM);
        SecureRandom random = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
        random.setSeed(password.getBytes(CHARSET));
        kgen.init(128, random);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, CIPHER_ALGORITHM);
        // 创建密码器
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        // 初始化
        cipher.init(Cipher.DECRYPT_MODE, key);
        //解密
        byte[] result = cipher.doFinal(buf);
        return new String(result, CHARSET);
    }


    private static String base64Encode(byte[] buf) {
        return BaseEncoding.base64().encode(buf);
    }

    private static byte[] base64Decode(String content) {
        return BaseEncoding.base64().decode(content);
    }
}
