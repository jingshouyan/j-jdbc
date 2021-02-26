package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.annotation.Key;
import lombok.Data;

/**
 * key wrong type
 *
 * @author jingshouyan
 * 2021-02-26 17:48
 **/
@Data
public class IllegalKey {

    @Key
    private Integer id;

    private String name;
}
