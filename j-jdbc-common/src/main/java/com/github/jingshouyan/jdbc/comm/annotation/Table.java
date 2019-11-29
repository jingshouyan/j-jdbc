package com.github.jingshouyan.jdbc.comm.annotation;

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
     * @return 数据库中映射的表名,为空或不添加此注解则为类名
     */
    String value() default "";

    /**
     * @return 备注,用于建表sql 表备注
     */
    String comment() default "";
}
