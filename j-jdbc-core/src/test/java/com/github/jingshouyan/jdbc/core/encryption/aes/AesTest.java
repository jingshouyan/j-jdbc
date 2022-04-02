package com.github.jingshouyan.jdbc.core.encryption.aes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author jingshouyan
 * 2021-12-21 13:17
 */
class AesTest {

    Aes aes = new Aes();

    @Test
    void encrypt() {
        String name = "张三封";
        String namePrefix = "张三";
        String key = "abc123456";
        String encryptedName = aes.encrypt(name, key);
        String encryptedNamePrefix = aes.encrypt(namePrefix, key);
        System.out.println(encryptedName);
        System.out.println(encryptedNamePrefix);
        assertFalse(encryptedName.startsWith(encryptedNamePrefix));
        String name2 = aes.decrypt(encryptedName, key);
        assertEquals(name, name2);
    }

}