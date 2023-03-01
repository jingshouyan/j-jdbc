package com.github.jingshouyan.jdbc.comm.util;

/**
 * @author jingshouyan
 * 11/28/18 4:18 PM
 */
public class StringUtil {
    public static boolean isNullOrEmpty(String str) {
        return str == null || "".equals(str);
    }


    public static String toSnakeCase(String input) {
        if (input == null) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i++) {
            char c = input.charAt(i);
            if (i > 0 && Character.isUpperCase(c)) {
                boolean isPrevCharLowCase = Character.isLowerCase(input.charAt(i - 1));
                boolean isNextCharLowCase = (i < len - 1 && Character.isLowerCase(input.charAt(i + 1)));
                if (isPrevCharLowCase || isNextCharLowCase) {
                    out.append('_');
                }
            }
            out.append(Character.toLowerCase(c));
        }
        return out.toString();
    }


}
