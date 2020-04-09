package com.github.jingshouyan.jdbc.comm.annotation;

import java.lang.annotation.*;

/**
 * @author jingshouyan
 * #date 2019/1/30 11:53
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Foreign {
    String thisKey();

    String thatKey();

    String[] value() default {};
}
