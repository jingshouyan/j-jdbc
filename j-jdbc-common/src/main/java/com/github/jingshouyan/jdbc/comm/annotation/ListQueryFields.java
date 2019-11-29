package com.github.jingshouyan.jdbc.comm.annotation;

import java.lang.annotation.*;

/**
 * 查询多行数据时,默认查询的字段
 * @author jingshouyan
 * 05/21/19 11:02 AM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListQueryFields {
    /**
     * @return 查询多行数据时,默认查询的字段
     */
    String[] value();
}
