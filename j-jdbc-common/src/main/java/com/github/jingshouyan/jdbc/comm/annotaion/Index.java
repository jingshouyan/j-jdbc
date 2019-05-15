package com.github.jingshouyan.jdbc.comm.annotaion;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Indices.class)
@Documented
public @interface Index {
    String[] value() default {};

    boolean unique() default false;
}
