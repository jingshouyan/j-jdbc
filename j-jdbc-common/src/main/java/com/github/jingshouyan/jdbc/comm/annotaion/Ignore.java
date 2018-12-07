package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

/**
 * 忽略字段（不入库）
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {
    String value() default "";
}
