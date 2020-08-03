package com.github.jingshouyan.jdbc.core.encryption.offset;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author 29017
 */
public class OffsetEncrypt {

    private final List<OffsetCalc> calcs = Lists.newArrayList();

    public void addCalc(OffsetCalc calc) {
        calcs.add(calc);
    }

    public String encrypt(String in) {
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int iv = chars[i];
            for (OffsetCalc calc : calcs) {
                iv = calc.encode(iv);
            }
            chars[i] = (char) iv;
        }
        return new String(chars);
    }

    public String decrypt(String in) {
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int iv = chars[i];
            for (int j = calcs.size() - 1; j >= 0; j--) {
                iv = calcs.get(j).decode(iv);
            }
            chars[i] = (char) iv;
        }
        return new String(chars);
    }

}
