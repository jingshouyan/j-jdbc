package com.github.jingshouyan.jdbc.data.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:05
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DataInitProperties.class)
@ComponentScan()
@Order(Ordered.LOWEST_PRECEDENCE)
public class DataInitAutoConfiguration implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
