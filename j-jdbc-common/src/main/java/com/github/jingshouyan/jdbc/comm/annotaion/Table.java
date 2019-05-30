package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

/**
 * 表
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {
    /**
     * @return 表名
     */
    String value() default "";

    /**
     * @return 备注
     */
    String comment() default "";
}
