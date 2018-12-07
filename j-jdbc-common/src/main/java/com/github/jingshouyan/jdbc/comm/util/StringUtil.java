package com.github.jingshouyan.jdbc.comm.util;

/**
 * @author jingshouyan
 * 11/28/18 4:18 PM
 */
public class StringUtil {
    public static boolean isNullOrEmpty(String str){
        return str == null || "".equals(str);
    }
}
