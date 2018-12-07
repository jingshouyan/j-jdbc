package com.github.jingshouyan.jdbc.core.dao.impl;

import com.github.jingshouyan.jdbc.comm.bean.BaseBean;
import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * 11/27/18 3:22 PM
 */
public abstract class BaseDaoImpl<T extends BaseBean> implements BaseDao<T> {
    private Class<T> clazz;
    private RowMapper<T> rowMapper;


    public BaseDaoImpl() {
        init();
    }

    private SqlGenerator<T> sqlGenerator(){
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
    public Class<T> getClazz(){
        return clazz;
    }

    @Override
    public Optional<T> find(Object id) {
        Preconditions.checkNotNull(id,"id is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).eq(id).conditions();
        List<T> ts = query(conditions);
        return ts.stream().findFirst();
    }


    @Override
    public List<T> findByIds(List<?> ids) {
        Preconditions.checkNotNull(ids, "ids is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).in(ids).conditions();
        return query(conditions);
    }


    @Override
    public Page<T> queryPage(List<Condition> conditions, Page<T> page) {
        int count = count(conditions);
        page.totalCount(count);
        List<T> ts = queryLimit(conditions,page);
        page.setList(ts);
        return page;
    }

    @Override
    public List<T> query(List<Condition> conditions) {
        SqlPrepared sqlPrepared = sqlGenerator().query(conditions);
        List<T> ts = template.query(sqlPrepared.getSql(), sqlPrepared.getParams(), rowMapper);
        return ts;
    }

    @Override
    public List<T> queryLimit(List<Condition> conditions,Page<T> page) {
        SqlPrepared sqlPrepared = sqlGenerator().query(conditions, page);
        List<T> ts = template.query(sqlPrepared.getSql(), sqlPrepared.getParams(), rowMapper);
        return ts;
    }


    @Override
    public int update(T t, List<Condition> conditions) {
        t.forUpdate();
        SqlPrepared sqlPrepared = sqlGenerator().update(t, conditions);
        int fetch =  template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
        return fetch;
    }


    @Override
    public int count(List<Condition> conditions) {
        SqlPrepared sqlPrepared = sqlGenerator().count(conditions);
        return template.queryForObject(sqlPrepared.getSql(), sqlPrepared.getParams(), Integer.class);
    }


    @Override
    public int insert(T t) {
        List<T> list = Lists.newArrayList(t);
        return insert(list);
    }

    private int insert(@NonNull List<T> list) {
        Preconditions.checkArgument(!list.isEmpty(), "list is empty!");
        for (T t : list) {
            t.forCreate();
            //如果不设置主键，则使用 keygen 生成主键
            genKey(t);
        }
        SqlPrepared sqlPrepared = sqlGenerator().insert(list);
        int fetch = template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
        for (T t : list) {
            //添加插入事件
            DmlEventBus.onCreate(t);
        }
        return fetch;
    }

    @Override
    public int batchInsert(@NonNull List<T> list) {
        Preconditions.checkArgument(!list.isEmpty(), "list is empty!");
        SqlPrepared sqlPrepared = null;
        List<Map<String, Object>> values = Lists.newArrayList();
        Map[] v = new Map[list.size()];
        for (T t : list) {
            t.forCreate();
            //如果不设置主键，则使用 keygen 生成主键
            genKey(t);
            List<T> ts = Lists.newArrayList(t);
            sqlPrepared = sqlGenerator().insert(ts);
            values.add(sqlPrepared.getParams());
        }
        v = values.toArray(v);
        int[] fetches = template.batchUpdate(sqlPrepared.getSql(), v);
        int fetch = IntStream.of(fetches).sum();
        for (T t : list) {
            //添加插入事件
            DmlEventBus.onCreate(t);
        }
        return fetch;
    }

    @Override
    public int update(T t) {
        String key = key();
        Object value = fieldValue(t, key);
        Preconditions.checkNotNull(value, "The @Key field must not null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key).eq(value).conditions();
        int fetch = update(t, conditions);
        //添加更新事件
        DmlEventBus.onUpdate(t);
        return fetch;
    }

    @Override
    public int batchUpdate(List<T> list){
        Preconditions.checkArgument(!list.isEmpty(), "list is empty!");
        SqlPrepared sqlPrepared = null;
        List<Map<String, Object>> values = Lists.newArrayList();
        Map[] v = new Map[list.size()];
        for (T t : list) {
            String key = key();
            Object value = fieldValue(t,key);
            Preconditions.checkNotNull(value, "The @Key field must not null");
            List<Condition> conditions = ConditionUtil.newInstance().field(key).eq(value).conditions();
            sqlPrepared = sqlGenerator().update(t,conditions);
            values.add(sqlPrepared.getParams());
        }
        v = values.toArray(v);
        int[] fetches = template.batchUpdate(sqlPrepared.getSql(), v);
        int fetch = IntStream.of(fetches).sum();
        for (T t : list) {
            //添加更新事件
            DmlEventBus.onUpdate(t);
        }
        return fetch;
    }

    @Override
    public int delete4List(List<?> ids) {
        Preconditions.checkNotNull(ids, "ids is null");
        List<Condition> conditions = ConditionUtil.newInstance().field(key()).in(ids).conditions();
        List<T> list = Lists.newArrayList();
        if(DmlEventBus.isDeleteOn()){
            list = findByIds(ids);
        }
        int fetch = delete4Batch(conditions);
        //添加删除事件
        if(DmlEventBus.isDeleteOn()){
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
        SqlPrepared sqlPrepared = sqlGenerator().createTableSql();
        return template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
    }

    @Override
    public int dropTable() {
        SqlPrepared sqlPrepared = sqlGenerator().dropTableSql();
        return template.update(sqlPrepared.getSql(), sqlPrepared.getParams());
    }

    @Override
    public boolean existTable(){
        try {
            SqlPrepared sqlPrepared = sqlGenerator().selectNull();
            template.queryForRowSet(sqlPrepared.getSql(),sqlPrepared.getParams());
            return true;
        }catch (BadSqlGrammarException e){
        }
        return false;
    }

    @Override
    public int updateTable(){
        SqlPrepared sqlPrepared = sqlGenerator().selectNull();
        SqlRowSet rowSet = template.queryForRowSet(sqlPrepared.getSql(),sqlPrepared.getParams());
        SqlRowSetMetaData md = rowSet.getMetaData();
        Set<String> cn = Sets.newHashSet();
        for(String columnName : md.getColumnNames()){
            System.out.println(columnName);
            cn.add(columnName.toLowerCase());
        }
        Map<String,ColumnInfo> map = TableUtil.tableInfo(clazz)
                .getLowerCaseColumnMap();
        List<ColumnInfo> columnInfos = Lists.newArrayList();
        for (String key : map.keySet()){
            if(!cn.contains(key)){
                columnInfos.add(map.get(key));
            }
        }

        if(columnInfos.isEmpty()) {
            return 0;
        }

        for (ColumnInfo columnInfo : columnInfos) {
            SqlPrepared sqlPrepared1 = sqlGenerator().addColumn(columnInfo);
            return template.update(sqlPrepared1.getSql(), sqlPrepared1.getParams());
        }

        return columnInfos.size();
    }

    private Object fieldValue(T t,String fieldName){
        return TableUtil.fieldValue(t,fieldName);
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
