package com.github.jingshouyan.jdbc.core.keygen;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultKeyGeneratorTest {

    KeyGenerator keyGenerator = DefaultKeyGenerator.getInstance();

    @Test
    void generateKey() {
        int i = 100000;
        Set<Long> keys = Sets.newHashSetWithExpectedSize(i);
        for (int j = 0; j < i; j++) {
            long key = keyGenerator.generateKey("unused");
            keys.add(key);
        }
        assertEquals(i, keys.size());
    }
}