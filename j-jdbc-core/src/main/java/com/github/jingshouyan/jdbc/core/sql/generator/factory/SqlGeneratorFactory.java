package com.github.jingshouyan.jdbc.core.sql.generator.factory;

import com.github.jingshouyan.jdbc.comm.entity.Record;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator;

/**
 * @author jingshouyan
 * 11/27/18 6:07 PM
 */
public interface SqlGeneratorFactory {

    /**
     * 获取 sql 生成器
     *
     * @param clazz 类型
     * @param <T>   T
     * @return sql 生成器
     */
    <T extends Record> SqlGenerator<T> sqlGenerator(Class<T> clazz);
}
