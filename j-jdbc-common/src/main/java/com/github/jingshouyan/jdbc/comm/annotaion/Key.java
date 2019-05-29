package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

/**
 * 主键
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
