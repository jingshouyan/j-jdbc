package com.github.jingshouyan.jdbc.core.encryption.hex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HexTest {

    @Test
    void encrypt() {
        Hex hex = new Hex();
        String a = "张三,呵呵哒!dflkjj";
        String b = hex.encrypt(a, "");
        System.out.println(b);
        String c = hex.decrypt(b, "");
        System.out.println(c);
        assertEquals(a, c);
    }

}