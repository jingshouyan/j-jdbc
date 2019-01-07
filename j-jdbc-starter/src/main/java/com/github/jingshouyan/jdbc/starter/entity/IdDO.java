package com.github.jingshouyan.jdbc.starter.entity;

import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * 11/29/18 4:21 PM
 */
@Getter@Setter@ToString
public class IdDO extends BaseDO {
    @Key
    @Column(length = 50,comment = "种子类型")
    private String idType;
    private Long seed;
}
