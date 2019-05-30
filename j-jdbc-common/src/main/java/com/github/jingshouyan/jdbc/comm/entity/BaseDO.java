package com.github.jingshouyan.jdbc.comm.entity;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author jingshouyan
 * 11/22/18 5:04 PM
 */
@Getter
@Setter
@ToString
public abstract class BaseDO implements Serializable {

    @Column(order = 1001, comment = "创建时间")
    private Long createdAt;
    @Column(order = 1002, comment = "修改时间")
    private Long updatedAt;
    @Column(order = 1003, comment = "删除时间")
    private Long deletedAt;

    /**
     * 自动生成Id时,String 类型的前缀
     *
     * @return 前缀
     */
    public String idPrefix() {
        return "";
    }

    /**
     * 自动生成Id时,String 类型的后缀
     *
     * @return 后缀
     */
    public String idSuffix() {
        return "";
    }


    public void forCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        deletedAt = Constant.NO_DELETE;
    }

    public void forUpdate() {
        long now = System.currentTimeMillis();
        createdAt = null;
        updatedAt = now;
    }

    public void forDelete() {
        long now = System.currentTimeMillis();
        createdAt = null;
        updatedAt = now;
        deletedAt = now;
    }

    public void forUndelete() {
        long now = System.currentTimeMillis();
        createdAt = null;
        updatedAt = now;
        deletedAt = Constant.NO_DELETE;
        ;
    }


    public boolean deleted() {
        return deletedAt != null && deletedAt != Constant.NO_DELETE;
    }

    public static boolean deleted(BaseDO bean) {
        return bean == null || bean.deleted();
    }

}

