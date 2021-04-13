package com.github.jingshouyan.jdbc.core.encryption.offset;

import com.github.jingshouyan.jdbc.core.encryption.Encryption;

/**
 * @author jingshouyan
 * #date 2020/8/3 21:33
 */
public class OffsetEncryption implements Encryption {


    public OffsetEncryption() {

    }

    @Override
    public String encrypt(String content, String password) {
        return genOffsetEncrypt(password).encrypt(content);
    }

    @Override
    public String decrypt(String content, String password) {
        return genOffsetEncrypt(password).decrypt(content);
    }

    private OffsetEncrypt genOffsetEncrypt(String password) {
        int hash = String.valueOf(password).hashCode();
        int offset = Math.abs(hash) % 256 + 1024;
        int[] range = new int[]{
                0x21, 0x25,
                0x30, 0x3A,
                0x40, 0x5B,
                0x61, 0x7B,
                0x80, 0xdc00
        };
        OffsetCalc calc = new OffsetCalc(range, offset);
        OffsetEncrypt offsetEncrypt = new OffsetEncrypt();
        offsetEncrypt.addCalc(calc);
        return offsetEncrypt;
    }

    public static void main(String[] args) {
        String password = "U33683@abc";
        int hash = String.valueOf(password).hashCode();
        int offset = Math.abs(hash) % 256 + 1024;
        System.out.println(offset);
    }
}
