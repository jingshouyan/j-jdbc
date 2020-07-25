package com.github.jingshouyan.jdbc.core.dao.impl;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.entity.Record;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jdbc.core.event.DmlEventBus;
import com.github.jingshouyan.jdbc.core.mapper.BeanRowMapper;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator;
import com.github.jingshouyan.jdbc.core.sql.generator.factory.SqlGeneratorFactoryUtil;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * 11/27/18 3:22 PM
 */
@Slf4j
public abstract class BaseDaoImpl<T extends Record> implements BaseDao<T> {
    protected Class<T> clazz;
    protected RowMapper<T> rowMapper;

    public BaseDaoImpl() {
        init();
    }

    private SqlGenerator<T> sqlGenerator() {
        return SqlGeneratorFactoryUtil.getSqlGeneratorFactory().sqlGenerator(clazz);
    }

    @SuppressWarnings("unchecked")
    private void init() {
        Type t = getClass().getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            clazz = (Class<T>) p[0];
            rowMapper = new BeanRowMapper<>(clazz);
        }
    }

    @Autowired
    protected NamedParameterJdbcTemplate template;

    @Override
    public Class<T> getClazz() {
        return clazz;
    }

    @Override
    public Optional<T> find(Object id) {
        Preconditions.checkNotNull(id, "id is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).eq(id).conditions();
        List<T> ts = queryField(conditions, null);
        return ts.stream().findFirst();
    }

    @Override
    public Optional<T> findField(Object id, Collection<String> fields) {
        Preconditions.checkNotNull(id, "id is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).eq(id).conditions();
        List<T> ts = queryField(conditions, fields);
        return ts.stream().findFirst();
    }

    @Override
    public List<T> findByIds(Collection<?> ids) {
        return findByIdsField(ids, fields());
    }

    @Override
    public List<T> findByIdsField(Collection<?> ids, Collection<String> fields) {
        Preconditions.checkNotNull(ids, "ids is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).in(ids).conditions();
        return queryField(conditions, fields);
    }


    @Override
    public Page<T> queryPage(List<Condition> conditions, Page<T> page) {
        return queryFieldPage(conditions, page, fields());
    }

    @Override
    public Page<T> queryFieldPage(List<Condition> conditions, Page<T> page, Collection<String> fields) {
        int count = count(conditions);
        page.totalCount(count);
        List<T> ts = queryFieldLimit(conditions, page, fields);
        page.setList(ts);
        return page;
    }

    @Override
    public List<T> query(List<Condition> conditions) {
        return queryField(conditions, fields());
    }

    @Override
    public List<T> queryField(List<Condition> conditions, Collection<String> fields) {
        SqlPrepared sqlPrepared = sqlGenerator().query(conditions, fields, false);
        return template.query(sqlPrepared.getSql(), sqlPrepared.getParams(), rowMapper);
    }

    @Override
    public List<T> queryDistinct(List<Condition> conditions, Collection<String> fields) {
        SqlPrepared sqlPrepared = sqlGenerator().query(conditions, fields, true);
        return template.query(sqlPrepared.getSql(), sqlPrepared.getParams(), rowMapper);
    }

    @Override
    public List<T> queryLimit(List<Condition> conditions, Page<T> page) {
        return queryFieldLimit(conditions, page, fields());
    }

    @Override
    public List<T> queryFieldLimit(List<Condition> conditions, Page<T> page, Collection<String> fields) {
        SqlPrepared sqlPrepared = sqlGenerator().queryLimit(conditions, page, fields);
        return template.query(sqlPrepared.getSql(), sqlPrepared.getParams(), rowMapper);
    }





    @Override
    public int count(List<Condition> conditions) {
        SqlPrepared sqlPrepared = sqlGenerator().count(conditions);
        Integer count = template.queryForObject(sqlPrepared.getSql(), sqlPrepared.getParams(), Integer.class);
        return Objects.nonNull(count) ? count : 0;
    }


    @Override
    public int insert(T t) {
        @SuppressWarnings("unchecked")
        List<T> list = Lists.newArrayList(t);
        return batchInsert(list);
    }

    private int insertRawBatch(List<T> list) {

        SqlPrepared sqlPrepared = sqlGenerator().insert(list);

        return template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
    }

    private int insertJdbcBatch(List<T> list) {
        String sql = null;
        @SuppressWarnings("unchecked")
        Map<String, Object>[] v = new Map[list.size()];
        for (int i = 0; i < list.size(); i++) {
            @SuppressWarnings("unchecked")
            List<T> ts = Lists.newArrayList(list.get(i));
            SqlPrepared sqlPrepared = sqlGenerator().insert(ts);
            sql = sqlPrepared.getSql();
            v[i] = sqlPrepared.getParams();
        }
        assert sql != null;
        int[] fetches = template.batchUpdate(sql, v);
        return IntStream.of(fetches).sum();
    }

    @Override
    public int batchInsert(@NonNull List<T> list) {
        Preconditions.checkArgument(!list.isEmpty(), "list is empty!");
        for (T t : list) {
            t.forCreate();
            //如果不设置主键，则使用 keygen 生成主键
            genKey(t);
        }
        int fetch = insertRawBatch(list);
        for (T t : list) {
            //添加插入事件
            DmlEventBus.onCreate(t);
        }
        return fetch;
    }


    @Override
    public int update(T t) {
        t.forUpdate();
        SqlPrepared sqlPrepared = sqlGenerator().update(t);
        int fetch = template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
        //添加更新事件
        DmlEventBus.onUpdate(t);
        return fetch;
    }

    @Override
    public int update(T t, List<Condition> conditions) {
        t.forUpdate();
        SqlPrepared sqlPrepared = sqlGenerator().update(t, conditions);
        int fetch = template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
        return fetch;
    }

    @Override
    public int batchUpdate(List<T> list) {
        Preconditions.checkArgument(!list.isEmpty(), "list is empty!");
        String sql = null;
        @SuppressWarnings("unchecked")
        Map<String, Object>[] v = new Map[list.size()];
        for (int i = 0; i < list.size(); i++) {
            T t = list.get(i);
            t.forUpdate();
            SqlPrepared sqlPrepared = sqlGenerator().update(t);
            sql = sqlPrepared.getSql();
            v[i] = sqlPrepared.getParams();
        }
        int[] fetches = template.batchUpdate(sql, v);
        int fetch = IntStream.of(fetches).sum();
        for (T t : list) {
            //添加更新事件
            DmlEventBus.onUpdate(t);
        }
        return fetch;
    }

    @Override
    public int delete4List(Collection<?> ids) {
        Preconditions.checkNotNull(ids, "ids is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).in(ids).conditions();
        List<T> list = Lists.newArrayList();
        if (DmlEventBus.isDeleteOn()) {
            list = findByIds(ids);
        }
        int fetch = delete4Batch(conditions);
        //添加删除事件
        if (DmlEventBus.isDeleteOn()) {
            list.forEach(DmlEventBus::onDelete);
        }
        return fetch;
    }

    @Override
    public int delete(Object id) {
        List<Object> l = Lists.newArrayList(id);
        return delete4List(l);
    }

    @Override
    public int delete4Batch(List<Condition> compares) {
        SqlPrepared sqlPrepared = sqlGenerator().delete(compares);
        return template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
    }


    @Override
    public int createTable() {
        List<SqlPrepared> sqlPrepareds = sqlGenerator().createTableSql();
        for (SqlPrepared sqlPrepared : sqlPrepareds) {
            template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
        }
        return 1;
    }

    @Override
    public int dropTable() {
        SqlPrepared sqlPrepared = sqlGenerator().dropTableSql();
        return template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
    }

    @Override
    public boolean existTable() {
        try {
            SqlPrepared sqlPrepared = sqlGenerator().selectNull();
            template.queryForRowSet(sqlPrepared.getSql(), sqlPrepared.getParams());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public int updateTable() {
        SqlPrepared sqlPrepared = sqlGenerator().selectNull();
        SqlRowSet rowSet = template.queryForRowSet(sqlPrepared.getSql(), sqlPrepared.getParams());
        SqlRowSetMetaData md = rowSet.getMetaData();
        Set<String> cn = Sets.newHashSet();
        for (String columnName : md.getColumnNames()) {
            cn.add(columnName.toLowerCase());
        }

        List<ColumnInfo> columnInfos = TableUtil.tableInfo(clazz).getColumns().stream()
                .filter(c -> !c.isForeign())
                .filter(c -> !cn.contains(c.getColumnName().toLowerCase()))
                .collect(Collectors.toList());

        if (columnInfos.isEmpty()) {
            return 0;
        }

        for (ColumnInfo columnInfo : columnInfos) {
            try {
                SqlPrepared sqlPrepared1 = sqlGenerator().addColumn(columnInfo);
                template.update(sqlPrepared1.getSql(), sqlPrepared1.getParams());
            } catch (Exception e) {
                log.warn("update table error", e);
            }

        }

        return columnInfos.size();
    }

    @Override
    public String doc() {
        return sqlGenerator().doc();
    }

    /**
     * 查询时默认指定的列
     *
     * @return 查询时默认指定的列
     */
    private List<String> fields() {
        return TableUtil.tableInfo(clazz).getListQueryFields();
    }


    private String key() {
        return TableUtil.keyFieldName(clazz);
    }

    /**
     * 生成主键
     *
     * @param t 对象
     */
    private void genKey(T t) {
        TableUtil.genKey(t);
    }

}
