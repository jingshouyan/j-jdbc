package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.OrderBy;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * sql 生成器抽象类
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
public abstract class AbstractSqlGenerator<T> implements SqlGenerator<T> {

    protected Class<T> clazz;


    public AbstractSqlGenerator(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 列名引号
     * @return
     */
    protected String q(){
        return "";
    }

    /**
     * 批量插入时value的分隔符
     * @return 分隔符
     */
    protected char valueSeparate() {
        return ',';
    }

    @Override
    public SqlPrepared query(List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT * FROM " + tableName();
        SqlPrepared whereSql = where(compares);
        sqlPrepared.setSql(sql + whereSql.getSql());
        sqlPrepared.setParams(whereSql.getParams());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared count(List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT COUNT(*) C FROM " + tableName();
        SqlPrepared whereSql = where(compares);
        sqlPrepared.setSql(sql + whereSql.getSql());
        sqlPrepared.setParams(whereSql.getParams());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared insert(List<T> beans) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        Object bean = beans.get(0);
        Map<String, Object> valueMap = valueMap(bean);
        StringBuilder sb = new StringBuilder();
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        values.append(" VALUES ");
        int i = 0;
        Map<Integer, String> keyMap = Maps.newHashMap();
        Map<String, Object> param = Maps.newHashMap();
        for (String key : valueMap.keySet()) {
            // 空值不插入
            if (isEmtry(valueMap.get(key))) {
                continue;
            }
            //拼装keys字符串
            keys.append(columnName(key));
            keys.append(',');
            //将所有的key标上序号放在map中，待用
            keyMap.put(i, key);
            i++;
        }
        keys.deleteCharAt(keys.length() - 1);
        int listSize = beans.size();
        for (int j = 0; j < listSize; j++) {
            bean = beans.get(j);
            valueMap = valueMap(bean);
            StringBuilder oneValue = new StringBuilder();
            for (int k = 0; k < i; k++) {
                String key = keyMap.get(k) + "_" + j + "_" + k;
                oneValue.append(':');
                oneValue.append(key);
                oneValue.append(',');
                param.put(key, valueMap.get(keyMap.get(k)));
            }
            oneValue.deleteCharAt(oneValue.length() - 1);
            values.append("(");
            values.append(oneValue);
            values.append(")");
            values.append(valueSeparate());
        }
        values.deleteCharAt(values.length() - 1);
        sb.append("INSERT INTO ");
        sb.append(tableName());
        sb.append(" (");
        sb.append(keys);
        sb.append(") ");
        sb.append(values);
        sqlPrepared.setSql(sb.toString());
        sqlPrepared.setParams(param);
        return sqlPrepared;
    }

    @Override
    public SqlPrepared update(T bean, List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        Map<String, Object> beanMap = valueMap(bean);
        Map<String, Object> param = Maps.newHashMap();
        sql.append("UPDATE ");
        sql.append(tableName());
        sql.append(" SET ");
        for (String key : beanMap.keySet()) {
            Object value = beanMap.get(key);
            // 空 不更新
            if (isEmtry(value)) {
                continue;
            }
            //主键不更新
            if (key.equals(key())) {
                continue;
            }
            String column = columnName(key);
            sql.append(String.format(" %s = :%s__set ,", column, key));
            param.put(key + "__set", value);
        }
        //只有当有字段需要更新
        Preconditions.checkArgument(!param.isEmpty(), "nothing to update");
        //移除set 最后一个逗号 ,
        sql.deleteCharAt(sql.length() - 1);
        SqlPrepared where = where(compares);
        sql.append(where.getSql());
        param.putAll(where.getParams());
        sqlPrepared.setSql(sql.toString());
        sqlPrepared.setParams(param);
        return sqlPrepared;
    }

    @Override
    public SqlPrepared delete(List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "DELETE FROM " + tableName();
        SqlPrepared where = where(compares);
        sql += where.getSql();
        sqlPrepared.setSql(sql);
        sqlPrepared.setParams(where.getParams());
        return sqlPrepared;
    }



    protected String orderBy(List<OrderBy> orderBies) {
        if (null == orderBies || orderBies.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" ORDER BY ");
        for (OrderBy order : orderBies) {
            sb.append(columnName(order.getKey()));
            sb.append(" ");
            sb.append(order.isAsc() ? "ASC" : "DESC");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    protected SqlPrepared where(List<Condition> compares){
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = Maps.newHashMap();
        sql.append(" WHERE 1=1 ");
        if (null != compares && !compares.isEmpty()) {

            for (Condition compare : compares) {
                //添加 条件值 校验
                String key = compare.getField();
                Preconditions.checkArgument(isField(key),
                        String.format("[%s] dos not contains field [%s]", clazz, key));
                String column = columnName(key);
                if(null != compare.getEq()) {
                    sql.append(String.format(" AND %s = :%s__eq ", column, key));
                    params.put(key + "__eq", compare.getEq());
                }
                if (null != compare.getLike()) {
                    sql.append(String.format(" AND %s LIKE :%s__like ", column, key));
                    params.put(key + "__like", compare.getLike());
                }
                if (null != compare.getGt()) {
                    sql.append(String.format(" AND %s > :%s__gt ", column, key));
                    params.put(key + "__gt", compare.getGt());
                }
                if (null != compare.getLt()) {
                    sql.append(String.format(" AND %s < :%s__lt ", column, key));
                    params.put(key + "__lt", compare.getLt());
                }
                if (null != compare.getGte()) {
                    sql.append(String.format(" AND %s >= :%s__gte ", column, key));
                    params.put(key + "__gte", compare.getGte());
                }
                if (null != compare.getLte()) {
                    sql.append(String.format(" AND %s <= :%s__lte ", column, key));
                    params.put(key + "__lte", compare.getLte());
                }
                if (null != compare.getNe()) {
                    sql.append(String.format(" AND %s <> :%s__ne ", column, key));
                    params.put(key + "__ne", compare.getNe());
                }
                if (null != compare.getEmpty()) {
                    if (compare.getEmpty()) {
                        sql.append(String.format(" AND %s IS NULL  ", column));
                    } else {
                        sql.append(String.format(" AND %s IS NOT NULL  ", column));
                    }
                }
                if (null != compare.getIn()) {
                    sql.append(String.format(" AND %s IN (:%s__in) ", column, key));
                    params.put(key + "__in", compare.getIn());
                }
                if (null != compare.getNotIn()) {
                    sql.append(String.format(" AND %s NOT IN (:%s__notIn) ", column, key));
                    params.put(key + "__notIn", compare.getNotIn());
                }
            }
        }
        sqlPrepared.setParams(params);
        sqlPrepared.setSql(sql.toString());
        return sqlPrepared;
    }

    protected String key() {
        return TableUtil.keyFieldName(clazz);
    }

    protected String tableName() {
        return TableUtil.tableInfo(clazz).getTableName();
    }

    protected String tableComment(){
        return TableUtil.tableInfo(clazz).getComment();
    }

    protected String columnName(String fieldName) {
        return q()+TableUtil.columnName(clazz, fieldName)+q();
    }

    protected Map<String, Object> valueMap(Object bean) {
        return TableUtil.valueMap(bean);
    }

    private boolean isField(String fieldName) {
        return TableUtil.tableInfo(clazz).getFieldNameMap().containsKey(fieldName);
    }

    protected boolean isEmtry(Object obj) {
        return obj == null;
    }

}
