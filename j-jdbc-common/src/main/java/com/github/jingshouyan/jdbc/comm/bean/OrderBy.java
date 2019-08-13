package com.github.jingshouyan.jdbc.comm.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * OrderBy
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
@ToString
@AllArgsConstructor()
@NoArgsConstructor
public class OrderBy {
    @Getter
    private String key;
    @Getter
    private boolean asc = true;
}
