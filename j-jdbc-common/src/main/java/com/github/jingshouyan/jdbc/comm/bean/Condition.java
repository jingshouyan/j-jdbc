package com.github.jingshouyan.jdbc.comm.bean;

import lombok.Data;

import java.util.Collection;

/**
 * @author jingshouyan
 * 11/27/18 4:23 PM
 */
@Data
public class Condition {
    /**
     * 属性名
     */
    private String field;

    /**
     * 模糊匹配，需要包含 % 等占位符
     */
    private String like;

    /**
     * 大于
     */
    private Object gt;

    /**
     * 小于
     */
    private Object lt;

    /**
     * 大于等于
     */
    private Object gte;

    /**
     * 小于等于
     */
    private Object lte;

    /**
     * 等于
     */
    private Object eq;

    /**
     * 不等于
     */
    private Object ne;

    /**
     * 在范围
     */
    private Collection<?> in;

    /**
     * 不在范围
     */
    private Collection<?> notIn;

    /**
     * 是否为空
     */
    private Boolean empty;

    /**
     * between
     */
    private Between between;

    public Condition(){}

    public Condition(String field, Object eq){
        this.field = field;
        this.eq = eq;
    }
}
