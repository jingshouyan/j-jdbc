package com.github.jingshouyan.jdbc.core.encryption;
/**
 * @author jingshouyan
 * #date 2019/1/17 16:58
 */

public interface Encryption {
    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return 加密后字符串
     */
    String encrypt(String content, String password);
    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return 解密后字符串
     */
    String decrypt(String content, String password);
}
