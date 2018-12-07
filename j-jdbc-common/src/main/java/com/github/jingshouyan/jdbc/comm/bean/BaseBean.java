package com.github.jingshouyan.jdbc.comm.bean;

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
public abstract class BaseBean implements Serializable {

    public static final long NO_DELETE = -1;

    @Column(order = 1001,comment = "创建时间")
    private Long createdAt;
    @Column(order = 1002,comment = "修改时间")
    private Long updatedAt;
    @Column(order = 1003,comment = "删除时间")
    private Long deletedAt;

    public String idPrefix(){
        return "";
    }


    public void forCreate() {
        long now = System.currentTimeMillis();
        createdAt = now;
        updatedAt = now;
        deletedAt = NO_DELETE;
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
        deletedAt = NO_DELETE;
    }


    public boolean deleted() {
        return deletedAt != null && deletedAt != NO_DELETE;
    }

    public static boolean deleted(BaseBean bean) {
        return bean == null || bean.deleted();
    }

}

