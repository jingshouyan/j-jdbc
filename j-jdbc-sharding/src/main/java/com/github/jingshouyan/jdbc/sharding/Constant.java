package com.github.jingshouyan.jdbc.sharding;

/**
 * @author jingshouyan
 * #date 2020/2/9 17:11
 */
public interface Constant {

    int DATA_SOURCE_TYPE_NORMAL = 0;
    int DATA_SOURCE_TYPE_SHARDING = 1;
    int DATA_SOURCE_TYPE_MASTER_SLAVE = 2;
    int DATA_SOURCE_TYPE_SHARDING_MASTER_SLAVE = 3;
}
