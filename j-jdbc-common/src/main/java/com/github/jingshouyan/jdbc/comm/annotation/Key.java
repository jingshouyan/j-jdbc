package com.github.jingshouyan.jdbc.comm.annotation;

import java.lang.annotation.*;

/**
 * 主键,一个类只能有一个
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Key {
    /**
     * @return 是否生成主键
     */
    boolean generatorIfNotSet() default true;
}
