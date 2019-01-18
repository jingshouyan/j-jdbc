package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;

import java.util.Collection;
import java.util.List;

/**
 * sql 生成器
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
public interface SqlGenerator<T> {

    /**
     * 条件查询语句生成
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared query(List<Condition> conditions, Collection<String> fields);

    /**
     * 带分页的条件查询语句生成
     * @param conditions 条件
     * @param page 分页信息
     * @return sql语句和参数
     */
    SqlPrepared queryLimit(List<Condition> conditions, Page<T> page, Collection<String> fields);

    /**
     *  条件查询计数语句生成
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared count(List<Condition> conditions);

    /**
     *  数据插入语句生成
     * @param beans 需要插入的对象
     * @return sql语句和参数
     */
    SqlPrepared insert(List<T> beans);

    /**
     * 数据更新语句生成
     * @param bean 需要更新的属性
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared update(T bean, List<Condition> conditions);

    /**
     * 条件删除语句生成
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared delete(List<Condition> conditions);

    /**
     * 建表语句生成
     * @return sql语句和参数
     */
    SqlPrepared createTableSql();

    /**
     *  删表语句生成
     * @return sql语句和参数
     */
    SqlPrepared dropTableSql();

    /**
     *  查询空行
     * @return SqlPrepared
     */
    SqlPrepared selectNull();

    SqlPrepared addColumn(ColumnInfo columnInfo);
}
