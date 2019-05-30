package com.github.jingshouyan.jdbc.starter.entity;

import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.annotaion.Table;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * 11/29/18 4:21 PM
 */
@Getter
@Setter
@ToString
@Table("ID_SEED")
public class IdDO extends BaseDO {
    @Key
    @Column(value = "ID_TYPE", length = 50, comment = "种子类型")
    private String idType;
    @Column(value = "SEED", comment = "种子")
    private Long seed;
}
