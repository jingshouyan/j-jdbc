package com.github.jingshouyan.jdbc.comm.bean;

/**
 * @author jingshouyan
 * #date 2019/12/23 16:46
 */
public enum DataType {
    /**
     * 目前支持的类型,以mysql类型标注,其他数据库,按照对应转换
     */
    TINYINT,
    SMALLINT,
    INT,
    BIGINT,
    DOUBLE,
    TIME,
    DATETIME,
    JSON,
    VARCHAR,
    DECIMAL,
    TEXT,
    MEDIUMTEXT,
    LONGTEXT,
    /**
     * 类型推断
     */
    TYPE_INFERENCE
}
