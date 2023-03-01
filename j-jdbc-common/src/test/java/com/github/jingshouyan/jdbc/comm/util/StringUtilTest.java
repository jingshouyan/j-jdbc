package com.github.jingshouyan.jdbc.comm.util;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @org.junit.jupiter.api.Test
    void isNullOrEmpty() {
        String s1 = "";
        String s2 = null;
        String s3 = "abc";
        assertTrue(StringUtil.isNullOrEmpty(s1));
        assertTrue(StringUtil.isNullOrEmpty(s2));
        assertFalse(StringUtil.isNullOrEmpty(s3));
    }

    @org.junit.jupiter.api.Test
    void toSnakeCase() {
        String s1 = "HTTPRequest";
        String r1 = "http_request";
        String s2 = "userIDType";
        String r2 = "user_id_type";

        assertEquals(StringUtil.toSnakeCase(s1), r1);
        assertEquals(StringUtil.toSnakeCase(s2), r2);
    }
}