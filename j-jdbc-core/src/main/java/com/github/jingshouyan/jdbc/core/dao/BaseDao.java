package com.github.jingshouyan.jdbc.core.dao;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author jingshouyan
 * 11/27/18 3:21 PM
 */
public interface BaseDao<T extends BaseDO> {
    /**
     * 获取 T 的类型
     * @return
     */
    Class<T> getClazz();
    /**
     * 根据主键查询数据
     *
     * @param id 主键
     * @return optional 记录
     */
    Optional<T> find(Object id);
    /**
     * 根据主键查询数据
     *
     * @param id 主键
     * @param fields 查询的属性
     * @return optional 记录
     */
    Optional<T> findField(Object id, Collection<String> fields);

    /**
     * 根据主键列表查询数据
     *
     * @param ids 主键列表
     * @return 数据列表
     */
    List<T> findByIds(Collection<?> ids);
    /**
     * 根据主键列表查询数据
     *
     * @param ids 主键列表
     * @param fields 查询的属性
     * @return 数据列表
     */
    List<T> findByIdsField(Collection<?> ids, Collection<String> fields);

    /**
     * 条件查询
     *
     * @param conditions 条件
     * @return 结果集
     */
    List<T> query(List<Condition> conditions);
    /**
     * 条件查询
     *
     * @param conditions 条件
     * @param fields 查询的属性
     * @return 结果集
     */
    List<T> queryField(List<Condition> conditions, Collection<String> fields);

    /**
     * 条件查询（取一页数据）
     *
     * @param conditions 条件
     * @param page     页
     * @return 结果集
     */
    List<T> queryLimit(List<Condition> conditions, Page<T> page);
    /**
     * 条件查询（取一页数据）
     *
     * @param conditions 条件
     * @param page     页
     * @param fields 查询的属性
     * @return 结果集
     */
    List<T> queryFieldLimit(List<Condition> conditions, Page<T> page, Collection<String> fields);



    /**
     * 条件分页查询
     *
     * @param conditions 条件
     * @param page     页
     * @return 页信息及数据
     */
    Page<T> queryPage(List<Condition> conditions, Page<T> page);
    /**
     * 条件分页查询
     *
     * @param conditions 条件
     * @param page     页信息
     * @param fields 查询的属性
     * @return 页信息及数据
     */
    Page<T> queryFieldPage(List<Condition> conditions, Page<T> page, Collection<String> fields);

    /**
     * 条件计数
     *
     * @param conditions 条件
     * @return 数量
     */
    int count(List<Condition> conditions);

    /**
     * 数据插入,值为 null 的不插入
     *
     * @param t 数据对象
     * @return 影响行数
     */
    int insert(T t);

//    int insert(List<T> list);

    /**
     * 数据批量插入
     * 注：数据字段需要对齐,
     * 即 list中第一个对象的 a 属性不为null,其他对象的 a 属性也不能为 null
     *
     * @param list 数据集合
     * @return 影响行数
     */
    int batchInsert(Collection<T> list);

    /**
     * 基于主键的数据更新,null字段不更新,必须有主键且不能为空
     *
     * @param t 数据对象
     * @return 影响行数
     */
    int update(T t);

    /**
     * 基于主键的数据批量更新,null字段不更新,必须有主键且不能为空
     * 注：数据字段需要对齐,
     * 即 list中第一个对象的 a 属性不为null,其他对象的 a 属性也不能为 null
     *
     * @param list 数据集合
     * @return 影响行数
     */
    int batchUpdate(Collection<T> list);



    /**
     * 根据条件更新数据,主键不会被更新
     *
     * @param t        数据值存放位置（忽略主键）
     * @param conditions 条件
     * @return 影响行数
     */
    int update(T t, List<Condition> conditions);

    /**
     * 根据主键列表删除数据,必须有主键
     *
     * @param ids 主键列表
     * @return 影响行数
     */
    int delete4List(Collection<?> ids);

    /**
     * 根据主键列表删除数据,必须有主键
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

    /**
     * 表是否存在
     *
     * @return 表是否存在
     */
    boolean existTable();

    /**
     *  更新表
     * @return 添加的行数
     */
    int updateTable();

    /**
     * 列表查询默认查询的字段列表
     * @return 列表查询默认查询的字段列表,返回 null 则查询 *(全部字段)
     */
    List<String> fields();
}
