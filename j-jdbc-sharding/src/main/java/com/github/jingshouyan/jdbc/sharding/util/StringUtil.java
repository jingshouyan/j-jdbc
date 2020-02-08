package com.github.jingshouyan.jdbc.sharding.util;

/**
 * @author jingshouyan
 * #date 2020/02/08 09:45
 */
public class StringUtil {

    public static final int NUMBER_MAX_LENGTH = 18;

    /**
     * 获取字符串中第一个连续数字,最多18位
     *
     * @param str 字符串
     * @return 连续数字
     */
    public static long getNumber(String str) {
        boolean isStart = false;
        int start = 0;
        int end = 0;
        for (int i = 0; i < str.length(); i++) {
            boolean isDigit = Character.isDigit(str.charAt(i));
            if (!isStart && isDigit) {
                isStart = true;
                start = i;
            }
            if (isStart && !isDigit) {
                end = i;
                break;
            }
        }
        if (start == end) {
            return -1;
        }
        if (end - start > NUMBER_MAX_LENGTH) {
            end = start + NUMBER_MAX_LENGTH;
        }
        String subStr = str.substring(start, end);
        return Long.parseLong(subStr);
    }

    public static String getShardingSuffix(long value){
        return String.format("_%02d", value);
    }

}
