package com.github.jingshouyan.jdbc.sharding.entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2020/2/9 15:12
 */
@Data
@ToString(exclude = "password")
public class DatabaseLinkInfo {
    private String driver;
    private String url;
    private String username;
    private String password;

    private int maxPoolSize = 20;
    private int minIdle = 5;


    private List<DatabaseLinkInfo> slaves;
}
