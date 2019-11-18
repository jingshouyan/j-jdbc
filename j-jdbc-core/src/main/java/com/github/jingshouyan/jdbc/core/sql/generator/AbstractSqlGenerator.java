package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.*;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jdbc.core.encryption.EncryptionProvider;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sql 生成器抽象类
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
public abstract class AbstractSqlGenerator<T> implements SqlGenerator<T> {

    protected Class<T> clazz;
    protected TableInfo tableInfo;


    public AbstractSqlGenerator(Class<T> clazz) {
        this.clazz = clazz;
        this.tableInfo = TableUtil.tableInfo(clazz);
    }

    /**
     * @return 表名 ,列名包裹字符串
     */
    protected String q() {
        return "";
    }

    /**
     * 批量插入时value的分隔符
     *
     * @return 分隔符
     */
    protected char valueSeparate() {
        return ',';
    }

    @Override
    public SqlPrepared query(List<Condition> compares, Collection<String> fields, boolean distinct) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        SqlPrepared whereSql = where(compares);
        sql.append("SELECT ");
        if (distinct) {
            sql.append("DISTINCT ");
        }
        sql.append(columns(fields))
                .append(" FROM ")
                .append(tableName())
                .append(whereSql.getSql());
        sqlPrepared.setSql(sql.toString());
        sqlPrepared.setParams(whereSql.getParams());
        return sqlPrepared;
    }


    @Override
    public SqlPrepared count(List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT COUNT(*) AS C FROM " + tableName();
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
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 空值不插入
            if (isEmpty(value)) {
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
    public SqlPrepared update(T bean) {
        Map<String, Object> beanMap = valueMap(bean);
        Object id = beanMap.get(key());
        Preconditions.checkNotNull(id, "The @Key field must not null");
        ConditionUtil conditionUtil = ConditionUtil.newInstance()
                .field(key()).eq(id);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            ColumnInfo columnInfo = columnInfo(key);
            if (columnInfo != null && columnInfo.isRouter()) {
                conditionUtil.field(key).eq(value);
            }
        }
        List<Condition> conditions = conditionUtil.conditions();
        return update(beanMap, conditions);
    }

    @Override
    public SqlPrepared update(T bean, List<Condition> conditions) {
        Map<String, Object> beanMap = valueMap(bean);
        return update(beanMap, conditions);
    }

    private SqlPrepared update(Map<String, Object> beanMap, List<Condition> conditions) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = Maps.newHashMap();
        sql.append("UPDATE ");
        sql.append(tableName());
        sql.append(" SET ");
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            // 空 不更新
            if (isEmpty(value)) {
                continue;
            }
            // 不可变字段 不更新
            ColumnInfo columnInfo = columnInfo(key);
            if (columnInfo.isImmutable()) {
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
        SqlPrepared where = where(conditions);
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

    @Override
    public SqlPrepared selectNull() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT * FROM " + tableName() + " WHERE 1 = 2";
        sqlPrepared.setSql(sql);
        sqlPrepared.setParams(Maps.newHashMap());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared addColumn(ColumnInfo columnInfo) {
        throw new UnsupportedOperationException("not support yet.");
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

    protected SqlPrepared where(List<Condition> compares) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        Map<String, Object> params = Maps.newHashMap();
        sql.append(" WHERE 1=1 ");
        if (null != compares && !compares.isEmpty()) {

            for (Condition compare : compares) {
                //添加 条件值 校验
                String key = compare.getField();
                ColumnInfo columnInfo = columnInfo(key);
                Preconditions.checkNotNull(columnInfo,
                        String.format("[%s] dos not contains field [%s]", clazz, key));
                String column = columnName(columnInfo);
                if (null != compare.getEq()) {
                    Object eq = compare.getEq();
                    if (columnInfo.isEncrypt() && columnInfo.getEncryptType() == EncryptType.FIXED) {
                        eq = EncryptionProvider.encrypt(eq.toString(), columnInfo.getEncryptKey());
                    }
                    sql.append(String.format(" AND %s = :%s__eq ", column, key));
                    params.put(key + "__eq", eq);
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
                    Object neq = compare.getNe();
                    if (columnInfo.isEncrypt() && columnInfo.getEncryptType() == EncryptType.FIXED) {
                        neq = EncryptionProvider.encrypt(neq.toString(), columnInfo.getEncryptKey());
                    }
                    sql.append(String.format(" AND %s <> :%s__ne ", column, key));
                    params.put(key + "__ne", neq);
                }
                if (null != compare.getEmpty()) {
                    if (compare.getEmpty()) {
                        sql.append(String.format(" AND %s IS NULL  ", column));
                    } else {
                        sql.append(String.format(" AND %s IS NOT NULL  ", column));
                    }
                }
                if (null != compare.getIn()) {
                    Collection<?> in = compare.getIn();
                    if (columnInfo.isEncrypt() && columnInfo.getEncryptType() == EncryptType.FIXED) {
                        in = in.stream()
                                .map(i -> EncryptionProvider.encrypt(String.valueOf(i), columnInfo.getEncryptKey()))
                                .collect(Collectors.toList());
                    }
                    sql.append(String.format(" AND %s IN (:%s__in) ", column, key));
                    params.put(key + "__in", in);
                }
                if (null != compare.getNotIn()) {
                    Collection<?> notIn = compare.getNotIn();
                    if (columnInfo.isEncrypt() && columnInfo.getEncryptType() == EncryptType.FIXED) {
                        notIn = notIn.stream()
                                .map(i -> EncryptionProvider.encrypt(String.valueOf(i), columnInfo.getEncryptKey()))
                                .collect(Collectors.toList());
                    }
                    sql.append(String.format(" AND %s NOT IN (:%s__notIn) ", column, key));
                    params.put(key + "__notIn", notIn);
                }
                if (null != compare.getBetween()) {
                    Between between = compare.getBetween();
                    sql.append(String.format(" AND %s BETWEEN :%s__between_start AND :%s__between_end ", column, key, key));
                    params.put(key + "__between_start", between.getStart());
                    params.put(key + "__between_end", between.getEnd());
                }
            }
        }
        sqlPrepared.setParams(params);
        sqlPrepared.setSql(sql.toString());
        return sqlPrepared;
    }

    protected String columns(Collection<String> fields) {
        if (fields == null || fields.isEmpty()) {
            return tableInfo.getColumns().stream()
                    .filter(c -> !c.isForeign())
                    .map(this::columnName)
                    .collect(Collectors.joining(","));
        }
        return fields.stream()
                .map(this::columnName)
                .collect(Collectors.joining(","));
    }


    protected String key() {
        return tableInfo.getKey().getFieldName();
    }

    protected String tableName() {
        return q() + tableInfo.getTableName() + q();
    }

    protected String tableComment() {
        return tableInfo.getComment();
    }

    protected String columnName(String fieldName) {
        return columnName(columnInfo(fieldName));
    }

    protected String columnName(ColumnInfo columnInfo) {
        return q() + columnInfo.getColumnName() + q();
    }

    protected Map<String, Object> valueMap(Object bean) {
        return TableUtil.valueMap(bean);
    }

    private ColumnInfo columnInfo(String fieldName) {
        return tableInfo.getFieldNameMap().get(fieldName);
    }


    protected boolean isEmpty(Object obj) {
        return TableUtil.isEmpty(obj);
    }

}
