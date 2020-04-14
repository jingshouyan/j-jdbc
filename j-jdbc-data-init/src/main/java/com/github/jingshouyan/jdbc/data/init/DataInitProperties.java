package com.github.jingshouyan.jdbc.data.init;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:06
 */
@Data
@ConfigurationProperties(prefix = "j-jdbc.init")
public class DataInitProperties {

    private List<String> versions;
}
