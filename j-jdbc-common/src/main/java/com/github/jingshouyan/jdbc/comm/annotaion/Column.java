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
     * @return 属性数据库中映射的列名,为空或不添加此注解为属性名
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
     * @return 字段长度,建表sql用.
     * 主要用于String类型,如果长度设置<VARCHAR_MAX_LENGTH(5000),则使用varchar,否则为text
     */
    int length() default Constant.COLUMN_LENGTH_DEFAULT;

    /**
     * @return json 格式存储
     * 主要用于非基本类型存储,建表sql数据库列类型为json(mysql)
     * 入库时会转为 json 字符串,读取时会转成 java 对象
     * 目前对这种类型的查询支持不足,只能作为字符串比较
     */
    boolean json() default false;

    /**
     * @return 是否需要加密 默认否
     * 数据库字段加密
     * FIXED 使用固定值作为加密密钥,查询支持 eq,neq,in,notIn,输入条件不用加密,框架处理加密并比较
     * FIELD 使用其他属性的值作为加密密钥
     */
    EncryptType encryptType() default EncryptType.NONE;

    /**
     * @return 加密密钥
     * encryptType 为 FIXED 时,加密密钥
     * encryptType 为 FIELD 时,加密密钥属性名
     */
    String encryptKey() default "";

    /**
     * @return 默认值
     * 建表sql用
     */
    String defaultData() default "";

    /**
     * @return 备注
     */
    String comment() default "";

    /**
     * @return 列排序
     * 建表sql 列的排序
     */
    int order() default Constant.COLUMN_ORDER_DEFAULT;

}
