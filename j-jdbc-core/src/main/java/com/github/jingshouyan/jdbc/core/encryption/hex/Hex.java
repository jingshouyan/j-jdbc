package com.github.jingshouyan.jdbc.core.encryption.hex;

import com.github.jingshouyan.jdbc.core.encryption.Encryption;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author jingshouyan
 * #date 2019/3/18 16:40
 */

public class Hex implements Encryption {

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int HEX_ONE = 0x01;

    @Override
    public String encrypt(String content, String password) {
        byte[] bytes = content.getBytes(CHARSET);
        return encodeHexStr(bytes);
    }

    @Override
    public String decrypt(String content, String password) {
        char[] chars = content.toCharArray();
        byte[] bytes = decodeHex(chars);
        return new String(bytes, CHARSET);
    }


    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    private static String encodeHexStr(byte[] data) {
        return new String(encodeHex(data));
    }

    private static byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & HEX_ONE) != 0) {
            throw new RuntimeException("The length of characters must be even.");
        }
        byte[] out = new byte[len >> 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    private static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch
                    + " at index " + index);
        }
        return digit;
    }

}
