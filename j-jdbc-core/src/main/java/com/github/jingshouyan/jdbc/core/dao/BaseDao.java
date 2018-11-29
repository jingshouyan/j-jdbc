package com.github.jingshouyan.jdbc.core.dao;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;

import java.util.List;
import java.util.Optional;

/**
 * @author jingshouyan
 * 11/27/18 3:21 PM
 */
public interface BaseDao<T> {
    /**
     * 获取 T 的类型
     * @return
     */
    Class<T> getClazz();
    /**
     * 根据主键查询数据
     *
     * @param id 主键
     * @return null或单条数据
     */
    Optional<T> find(Object id);

    /**
     * 根据主键列表查询数据
     *
     * @param ids 主键列表
     * @return 数据列表
     */
    List<T> findByIds(List<?> ids);

    /**
     * 条件查询
     *
     * @param conditions 条件
     * @return 结果集
     */
    List<T> query(List<Condition> conditions);

    /**
     * 条件查询（取一页数据）
     *
     * @param conditions 条件
     * @param page     页
     * @return 结果集
     */
    List<T> queryLimit(List<Condition> conditions, Page<T> page);

    /**
     * 条件分页查询
     *
     * @param conditions 条件
     * @param page     页
     * @return 页信息及数据
     */
    Page<T> queryPage(List<Condition> conditions, Page<T> page);

    /**
     * 条件计数
     *
     * @param conditions 条件
     * @return 数量
     */
    int count(List<Condition> conditions);

    /**
     * 数据插入
     *
     * @param t 数据对象
     * @return 影响行数
     */
    int insert(T t);

//    int insert(List<T> list);

    /**
     * 数据批量插入
     * 注：数据字段是否设值需要保持一致
     *
     * @param list 数据集合
     * @return 影响行数
     */
    int batchInsert(List<T> list);

    /**
     * 基于主键的数据更新
     *
     * @param t 数据对象
     * @return 影响行数
     */
    int update(T t);

    /**
     * 基于主键的数据批量更新
     * 注：数据字段是否设值需要保持一致
     *
     * @param list 数据集合
     * @return 影响行数
     */
    int batchUpdate(List<T> list);

    /**
     * 根据条件更新数据
     *
     * @param t        数据值存放位置（忽略主键）
     * @param conditions 条件
     * @return 影响行数
     */
    int update(T t, List<Condition> conditions);

    /**
     * 根据主键列表删除数据
     *
     * @param ids 主键列表
     * @return 影响行数
     */
    int delete4List(List<?> ids);

    /**
     * 根据主键列表删除数据
     *
     * @param id 主键列表
     * @return 影响行数
     */
    int delete(Object id);

    /**
     * 条件删除数据
     *
     * @param conditions 条件
     * @return 影响行数
     */
    int delete4Batch(List<Condition> conditions);

    /**
     * 创建表
     *
     * @return 影响行数
     */
    int createTable();

    /**
     * 删除表
     *
     * @return 影响行数
     */
    int dropTable();
}
