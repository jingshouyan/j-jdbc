package com.github.jingshouyan.jdbc.comm.annotaion;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;

import java.lang.annotation.*;

/**
 * @author jingshouyan
 * 11/22/18 4:48 PM
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    /**
     * @return 列名
     */
    String value() default "";

    /**
     * @return 是否为不可变字段, 更新时不会更新该字段.
     */
    boolean immutable() default false;

    /**
     * @return id更新时作为条件, 主要用作sharding路由.
     */
    boolean router() default false;

    /**
     * @return 字段长度
     */
    int length() default Constant.COLUMN_LENGTH_DEFAULT;

    /**
     * @return json 格式存储
     */
    boolean json() default false;

    /**
     * @return 是否需要加密 默认否
     */
    EncryptType encryptType() default EncryptType.NONE;

    /**
     * @return 加密密钥
     */
    String encryptKey() default "";

    /**
     * @return 默认值
     */
    String defaultData() default "";

    /**
     * @return 备注
     */
    String comment() default "";

    /**
     * @return 列排序
     */
    int order() default Constant.COLUMN_ORDER_DEFAULT;

}
