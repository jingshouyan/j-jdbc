package com.github.jingshouyan.jdbc.starter.bean;

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
    private String idType;
    private Long seed;
}
