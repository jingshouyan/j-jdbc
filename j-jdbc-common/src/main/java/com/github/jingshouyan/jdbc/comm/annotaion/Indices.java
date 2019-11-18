package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

/**
 * @author jingshouyan
 * 05/21/19 11:02 AM
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Indices {
    Index[] value();
}
