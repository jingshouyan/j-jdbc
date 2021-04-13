package com.github.jingshouyan.jdbc.core.encryption;

import com.github.jingshouyan.jdbc.core.encryption.offset.OffsetEncryption;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jingshouyan
 * #date 2019/1/17 16:19
 */

public class EncryptionProvider {

    @Getter
    @Setter
    private static Encryption encryption = new OffsetEncryption();

    public static String encrypt(String content, String password) {
        return encryption.encrypt(content, password);
    }

    public static String decrypt(String content, String password) {
        return encryption.decrypt(content, password);
    }
}
