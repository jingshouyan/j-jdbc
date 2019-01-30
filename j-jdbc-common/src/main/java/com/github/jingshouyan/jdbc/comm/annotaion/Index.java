package com.github.jingshouyan.jdbc.comm.annotaion;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(indices.class)
@Documented
public @interface Index {
    String[] value();
}
