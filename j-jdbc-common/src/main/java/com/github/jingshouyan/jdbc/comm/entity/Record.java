package com.github.jingshouyan.jdbc.comm.entity;


/**
 * @author jingshouyan
 * 11/29/18 5:38 PM
 */
public interface Record {
    /**
     * 新增
     */
    default void forCreate() {
    }

    /**
     * 修改
     */
    default void forUpdate() {
    }

    /**
     * 伪删除
     */
    default void forDelete() {
    }

    /**
     * 取消伪删除
     */
    default void forUndelete() {
    }
}
