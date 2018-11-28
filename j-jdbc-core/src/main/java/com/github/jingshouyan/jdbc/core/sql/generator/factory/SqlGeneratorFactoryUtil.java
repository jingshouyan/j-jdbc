package com.github.jingshouyan.jdbc.core.sql.generator.factory;

import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Gbase;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Kingbase;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Mysql;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Oracle;
import lombok.Getter;
import lombok.Setter;

/**
 * @author jingshouyan
 * 11/28/18 11:05 AM
 */
public class SqlGeneratorFactoryUtil {

    public static final SqlGeneratorFactory MYSQL = SqlGenerator4Mysql::new;
    public static final SqlGeneratorFactory ORACLE = SqlGenerator4Oracle::new;
    public static final SqlGeneratorFactory G_BASE = SqlGenerator4Gbase::new;
    public static final SqlGeneratorFactory KING_BASE = SqlGenerator4Kingbase::new;

    @Getter@Setter
    public static SqlGeneratorFactory sqlGeneratorFactory = MYSQL;

}
