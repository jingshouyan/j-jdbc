package com.github.jingshouyan.jdbc.comm.entity;


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
