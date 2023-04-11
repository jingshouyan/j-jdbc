package com.github.jingshouyan.jdbc.comm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionUtilTest {

    @Test
    void compareVersion() {

        assertThrows(IllegalArgumentException.class, () -> VersionUtil.compareVersion(null, "1.0.1"));
        assertThrows(IllegalArgumentException.class, () -> VersionUtil.compareVersion("1.0.1", null));
        assertEquals(0, VersionUtil.compareVersion("1.0.1", "1.0.1"));
        assertTrue(VersionUtil.compareVersion("1.0.1", "1.0.2") < 0);
        assertTrue(VersionUtil.compareVersion("1.0.1", "1.0.1.1") < 0);
        assertTrue(VersionUtil.compareVersion("1.0.2", "1.0.1.2") > 0);
        assertTrue(VersionUtil.compareVersion("1.1.1.1", "1.0.1.2") > 0);
    }
}