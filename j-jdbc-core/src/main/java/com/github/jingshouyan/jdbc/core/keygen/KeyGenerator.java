package com.github.jingshouyan.jdbc.core.keygen;

/**
 * @author jingshouyan
 * 11/29/18 10:53 AM
 */
public interface KeyGenerator {

    /**
     * generate key
     * @param type key type
     * @return generate key
     */
    long generateKey(String type);
}
