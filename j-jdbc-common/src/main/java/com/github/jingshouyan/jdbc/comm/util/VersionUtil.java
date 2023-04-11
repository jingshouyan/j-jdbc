package com.github.jingshouyan.jdbc.comm.util;

/**
 * @author jingshouyan
 * #date 2020/4/14 17:42
 */
public class VersionUtil {


    /**
     * 比较版本号的大小,前者大则返回一个正数,后者大返回一个负数,相等则返回0
     *
     * @param version1 version1
     * @param version2 version2
     * @return 结果
     */
    public static int compareVersion(String version1, String version2) {
        if (version1 == null || version2 == null) {
            throw new IllegalArgumentException("compareVersion error:illegal params.");
        }
        if (version1.equals(version2)) {
            return 0;
        }
        //注意此处为正则匹配，不能用"."；
        String[] versionArray1 = version1.split("\\.");
        String[] versionArray2 = version2.split("\\.");
        int idx = 0;
        //取最小长度值
        int minLength = Math.min(versionArray1.length, versionArray2.length);
        int diff = 0;
        while (idx < minLength
                //先比较长度
                && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0
                //再比较字符
                && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {
            ++idx;
        }
        //如果已经分出大小，则直接返回，如果未分出大小，则再比较位数，有子版本的为大；
        diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
        return diff;
    }

}
