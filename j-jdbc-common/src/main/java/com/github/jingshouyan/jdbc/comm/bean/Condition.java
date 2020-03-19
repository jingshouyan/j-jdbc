package com.github.jingshouyan.jdbc.comm.bean;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author jingshouyan
 * 11/27/18 4:23 PM
 */
@Data
@Slf4j
public class Condition {

    /**
     * 属性名
     */
    private String field;

    /**
     * 等于
     */
    private Object eq;

    /**
     * 不等于
     */
    private Object ne;

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
     * between
     */
    private Between between;

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


    public Condition() {
    }

    public Condition(String field, Object eq) {
        this.field = field;
        if (eq == null) {
            log.warn("{}.eq is set to null", field);
        }
        this.eq = eq;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setEq(Object eq) {
        if (eq == null) {
            log.warn("{}.eq is set to null", field);
        }
        this.eq = eq;
    }

    public void setNe(Object ne) {
        if (ne == null) {
            log.warn("{}.ne is set to null", field);
        }
        this.ne = ne;
    }

    public void setLike(String like) {
        if (like == null) {
            log.warn("{}.like is set to null", field);
        }
        this.like = like;
    }

    public void setGt(Object gt) {
        if (gt == null) {
            log.warn("{}.gt is set to null", field);
        }
        this.gt = gt;
    }

    public void setLt(Object lt) {
        if (lt == null) {
            log.warn("{}.lt is set to null", field);
        }
        this.lt = lt;
    }

    public void setGte(Object gte) {
        if (gte == null) {
            log.warn("{}.gte is set to null", field);
        }
        this.gte = gte;
    }

    public void setLte(Object lte) {
        if (lte == null) {
            log.warn("{}.lte is set to null", field);
        }
        this.lte = lte;
    }

    public void setBetween(Between between) {
        if (between == null) {
            log.warn("{}.between is set to null", field);
        }
        this.between = between;
    }

    public void setIn(Collection<?> in) {
        if (in == null) {
            log.warn("{}.in is set to null", field);
        }
        this.in = in;
    }

    public void setNotIn(Collection<?> notIn) {
        if (notIn == null) {
            log.warn("{}.notIn is set to null", field);
        }
        this.notIn = notIn;
    }

    public void setEmpty(Boolean empty) {
        if (empty == null) {
            log.warn("{}.empty is set to null", field);
        }
        this.empty = empty;
    }
}
