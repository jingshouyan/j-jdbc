package com.github.jingshouyan.jdbc.comm.entity;


import java.util.List;

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

    /**
     * 更新时将以下字段更新为 null
     *
     * @return 需要更新为null的字段
     */
    default List<String> updateNullFields() {
        return null;
    }
}
