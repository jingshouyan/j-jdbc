package com.github.jingshouyan.jdbc.comm.bean;

import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/3/5 17:38
 */
@Data
public class ForeignInfo {
    private boolean collection;
    private Class<?> collectionType;
    private Class<?> foreignType;
    private String thisKey;
    private String thatKey;
    private List<String> fields;
}
