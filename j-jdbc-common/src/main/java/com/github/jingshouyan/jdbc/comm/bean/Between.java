package com.github.jingshouyan.jdbc.comm.bean;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jingshouyan
 * #date 2019/5/28 16:46
 */
@Data
@AllArgsConstructor
public class Between {
    /**
     * 开始
     */
    private Object start;
    /**
     * 结束
     */
    private Object end;
}
