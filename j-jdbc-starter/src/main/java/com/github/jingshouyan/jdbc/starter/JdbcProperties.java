package com.github.jingshouyan.jdbc.starter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author jingshouyan
 * 11/29/18 4:53 PM
 */
@Data
@ConfigurationProperties(prefix = "j-jdbc")
public class JdbcProperties {

    private int tableInit = 0;
}
