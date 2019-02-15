package com.github.jingshouyan.jdbc.comm.annotaion;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ListQueryFields {
    String[] value();
}
