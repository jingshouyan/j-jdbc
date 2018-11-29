package com.github.jingshouyan.jdbc.starter.bean;

import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jingshouyan
 * 11/29/18 4:21 PM
 */
@Getter@Setter@ToString
public class IdBean extends BaseBean {
    @Key
    private String idType;
    private Long seed;
}
