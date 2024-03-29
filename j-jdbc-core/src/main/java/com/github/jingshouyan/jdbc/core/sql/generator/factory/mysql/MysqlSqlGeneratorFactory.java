package com.github.jingshouyan.jdbc.core.sql.generator.factory.mysql;

import com.github.jingshouyan.jdbc.comm.entity.Record;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Mysql;
import com.github.jingshouyan.jdbc.core.sql.generator.factory.SqlGeneratorFactory;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author jingshouyan
 * #date 2019/5/29 18:32
 */

public class MysqlSqlGeneratorFactory implements SqlGeneratorFactory {
    private static final Map<Class<?>, SqlGenerator<?>> GENERATOR_CACHE = Maps.newConcurrentMap();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Record> SqlGenerator<T> sqlGenerator(Class<T> clazz) {
        return (SqlGenerator<T>) GENERATOR_CACHE.computeIfAbsent(clazz,
                (c) -> new SqlGenerator4Mysql(c)
        );
    }
}
