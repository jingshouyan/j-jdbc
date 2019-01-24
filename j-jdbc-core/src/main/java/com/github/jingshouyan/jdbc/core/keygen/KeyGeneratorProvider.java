package com.github.jingshouyan.jdbc.core.keygen;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jingshouyan
 * 11/29/18 1:50 PM
 */
public class KeyGeneratorProvider {
    public static final KeyGenerator DEFAULT = new DefaultKeyGenerator();

    @Getter@Setter
    private static KeyGenerator keyGenerator = DEFAULT;
}
