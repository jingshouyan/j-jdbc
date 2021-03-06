package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.entity.Record;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;

import java.util.Collection;
import java.util.List;

/**
 * sql 生成器
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
public interface SqlGenerator<T extends Record> {

    /**
     * 条件查询语句生成
     *
     * @param conditions 条件
     * @param fields     查询的列
     * @param distinct   是否为 distinct 查询
     * @return sql语句和参数
     */
    SqlPrepared query(List<Condition> conditions, Collection<String> fields, boolean distinct);


    /**
     * 带分页的条件查询语句生成
     *
     * @param conditions 条件
     * @param page       分页信息
     * @param fields     查询的列
     * @return sql语句和参数
     */
    SqlPrepared queryLimit(List<Condition> conditions, Page<T> page, Collection<String> fields);

    /**
     * 条件查询计数语句生成
     *
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared count(List<Condition> conditions);

    /**
     * 数据插入语句生成
     *
     * @param beans 需要插入的对象
     * @return sql语句和参数
     */
    SqlPrepared insert(List<T> beans);

    /**
     * 数据更新语句生成
     *
     * @param bean 更新对象
     * @return sql语句和参数
     */
    SqlPrepared update(T bean);

    /**
     * 数据更新语句生成
     *
     * @param bean       需要更新的属性
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared update(T bean, List<Condition> conditions);

    /**
     * 条件删除语句生成
     *
     * @param conditions 条件
     * @return sql语句和参数
     */
    SqlPrepared delete(List<Condition> conditions);

    /**
     * 建表语句生成
     *
     * @return sql语句和参数
     */
    List<SqlPrepared> createTableSql();

    /**
     * 删表语句生成
     *
     * @return sql语句和参数
     */
    SqlPrepared dropTableSql();

    /**
     * 查询空行
     *
     * @return SqlPrepared
     */
    SqlPrepared selectNull();

    /**
     * 添加列
     *
     * @param columnInfo 列信息
     * @return SqlPrepared
     */
    SqlPrepared addColumn(ColumnInfo columnInfo);

    /**
     * markdown格式表说明
     *
     * @return markdown格式表说明
     */
    String doc();

    /**
     * 数据库字段类型
     *
     * @param columnInfo 列信息
     * @return 数据库字段类型
     */
    String dataType(ColumnInfo columnInfo);
}
