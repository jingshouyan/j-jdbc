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
        boolean findNum = false;
        int start = 0;
        int end = 0;
        for (int i = 0; i < str.length(); i++) {
            boolean isDigit = Character.isDigit(str.charAt(i));
            if (!findNum && isDigit) {
                // 碰到第一个数字
                findNum = true;
                start = i;
            }
            if (findNum && !isDigit) {
                // 找到数字后碰到的第一个非数字的位置
                end = i;
                break;
            }
        }
        // 没找到数字
        if (!findNum) {
            return -1;
        }
        // 找到了数字并且未找到数字后的非数字
        if(end == 0) {
            end  = str.length();
        }
        // 防止数字过长
        if (end - start > NUMBER_MAX_LENGTH) {
            end = start + NUMBER_MAX_LENGTH;
        }

        String subStr = str.substring(start, end);
        return Long.parseLong(subStr);
    }

    public static String getShardingSuffix(long index) {
        return String.format("_%02d", index);
    }

    public static String getActualName(String logicName, long index) {
        return logicName + getShardingSuffix(index);
    }


    public static void main(String[] args) {
        long num = getNumber("1243334");
        System.out.println(num);
        System.out.println(Long.MAX_VALUE);
    }
}
