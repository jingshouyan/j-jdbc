package com.github.jingshouyan.jdbc.core.sql.generator.factory;

import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Mysql;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jingshouyan
 * 11/28/18 11:05 AM
 */
public class SqlGeneratorFactoryUtil {

    public static final SqlGeneratorFactory MYSQL = SqlGenerator4Mysql::new;

    @Getter@Setter
    private static SqlGeneratorFactory sqlGeneratorFactory = MYSQL;

}
