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
     * 1.fixed:abc 以 abc为密钥进行加密
     * 2.flied:id  以 bean 属性 id 的值为密钥进行加密
     */
    String encryptKey() default "";

    /**
     * @return 是否创建索引
     */
    boolean index() default false;

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
