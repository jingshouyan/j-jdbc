package com.github.jingshouyan.jdbc.comm.annotaion;


import java.lang.annotation.*;

/**
 * 索引,用于生成建表sql语句
 * 可以添加在类上,也可以添加在属性上
 * 在类上需要设置 value,值为属性名列表(不是数据库中字段名),多个属性名表示联合索引
 * 在属性上 value 无意义,等同与在类上添加 value 为属性名
 * @author jingshouyan
 * 05/21/19 11:02 AM
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Indices.class)
@Documented
public @interface Index {
    String[] value() default {};

    /**
     * @return 是否为唯一性索引
     */
    boolean unique() default false;
}
