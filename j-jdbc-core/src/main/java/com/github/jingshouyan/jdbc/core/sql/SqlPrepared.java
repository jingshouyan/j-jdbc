package com.github.jingshouyan.jdbc.core.sql;

import lombok.Data;

import java.util.Map;

/**
 * @author jingshouyan
 * 11/27/18 6:11 PM
 */
@Data
public class SqlPrepared {
    private String sql;
    private Map<String, Object> params;
}
