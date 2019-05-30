package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Foreign {
    String thisKey();

    String thatKey();

    String[] value() default {};
}
