package com.github.jingshouyan.jdbc.core.sql.generator.factory;

import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Mysql;
import com.github.jingshouyan.jdbc.core.sql.generator.factory.mysql.MysqlSqlGeneratorFactory;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jingshouyan
 * 11/28/18 11:05 AM
 */
public class SqlGeneratorFactoryUtil {

    public static final SqlGeneratorFactory MYSQL = new MysqlSqlGeneratorFactory();

    @Getter@Setter
    private static SqlGeneratorFactory sqlGeneratorFactory = MYSQL;

}
