package com.github.jingshouyan.jdbc.comm.entity;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Ignore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 数据库对象基类
 *
 * @author jingshouyan
 * 11/22/18 5:04 PM
 */
@ToString
public abstract class BaseDO implements Serializable, Record {

    @Getter
    @Setter
    @Column(order = 1001, comment = "创建时间", immutable = true)
    private Long createdAt;
    @Getter
    @Setter
    @Column(order = 1002, comment = "修改时间")
    private Long updatedAt;
    @Getter
    @Setter
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


    @Override
    public void forCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        deletedAt = Constant.NO_DELETE;
    }

    @Override
    public void forUpdate() {
        long now = System.currentTimeMillis();
        updatedAt = now;
    }

    @Override
    public void forDelete() {
        deletedAt = System.currentTimeMillis();
    }

    @Override
    public void forUndelete() {
        deletedAt = Constant.NO_DELETE;
    }


    public boolean deleted() {
        return deletedAt != null && deletedAt != Constant.NO_DELETE;
    }

    public static boolean deleted(BaseDO bean) {
        return bean == null || bean.deleted();
    }

}

