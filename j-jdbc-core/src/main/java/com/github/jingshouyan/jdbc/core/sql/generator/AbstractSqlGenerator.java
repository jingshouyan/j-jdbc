package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.*;
import com.github.jingshouyan.jdbc.comm.entity.Record;
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
public abstract class AbstractSqlGenerator<T extends Record> implements SqlGenerator<T> {

    protected Class<T> clazz;
    protected TableInfo tableInfo;


    /**
     * 更新时设置为null的占位符
     */
    private static final Object FLAG_NULL = new Object();


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
        Record bean = beans.get(0);
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
        prepareUpdateNull(bean, beanMap);
        return update(beanMap, conditions);
    }

    @Override
    public SqlPrepared update(T bean, List<Condition> conditions) {
        Map<String, Object> beanMap = valueMap(bean);
        prepareUpdateNull(bean, beanMap);
        return update(beanMap, conditions);
    }

    /**
     * 准备更新为 null 的字段
     *
     * @param bean    bean
     * @param beanMap beamMap
     */
    private void prepareUpdateNull(T bean, Map<String, Object> beanMap) {
        List<String> nullFields = bean.updateNullFields();
        if (nullFields != null && !nullFields.isEmpty()) {
            for (String field : nullFields) {
                beanMap.put(field, FLAG_NULL);
            }
        }
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

            if (value == FLAG_NULL) {
                // 更新为null
                sql.append(String.format(" %s = null ,", column));
            } else {
                // 正常更新值
                sql.append(String.format(" %s = :%s__set ,", column, key));
                param.put(key + "__set", value);
            }
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
                    if (columnInfo.isFixEncrypted()) {
                        eq = EncryptionProvider.encrypt(eq.toString(), columnInfo.getEncryptKey());
                    }
                    sql.append(String.format(" AND %s = :%s__eq ", column, key));
                    params.put(key + "__eq", eq);
                }
                if (null != compare.getLike()) {
                    Object like = compare.getLike();
                    if (columnInfo.isFixEncrypted()) {
                        like = EncryptionProvider.encrypt(like.toString(), columnInfo.getEncryptKey());
                    }
                    sql.append(String.format(" AND %s LIKE :%s__like ", column, key));
                    params.put(key + "__like", like);
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
                    if (columnInfo.isFixEncrypted()) {
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
                    if (columnInfo.isFixEncrypted()) {
                        in = in.stream()
                                .map(i -> EncryptionProvider.encrypt(String.valueOf(i), columnInfo.getEncryptKey()))
                                .collect(Collectors.toList());
                    }
                    sql.append(String.format(" AND %s IN (:%s__in) ", column, key));
                    params.put(key + "__in", in);
                }
                if (null != compare.getNotIn()) {
                    Collection<?> notIn = compare.getNotIn();
                    if (columnInfo.isFixEncrypted()) {
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
        ColumnInfo columnInfo = columnInfo(fieldName);
        Preconditions.checkNotNull(columnInfo,
                String.format("[%s] dos not contains field [%s]", clazz, fieldName));
        return columnName(columnInfo);
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

    public static final String LINK_BREAK = "\r\n";

    @Override
    public String doc() {
        StringBuilder sb = new StringBuilder();
        sb.append(LINK_BREAK);
        sb.append("## ").append(tableInfo.getTableName()).append("  ").append(tableInfo.getComment()).append(LINK_BREAK);
        // 数据结构信息
        sb.append("|#|列名|数据类型|备注|").append(LINK_BREAK);
        sb.append("|-|----|-------|----|").append(LINK_BREAK);
        for (int i = 0; i < tableInfo.getColumns().size(); i++) {
            ColumnInfo columnInfo = tableInfo.getColumns().get(i);
            String columnName = columnInfo.getColumnName();
            String dataType = dataType(columnInfo);
            String comment = columnInfo.getComment();
            sb.append("|").append(i + 1)
                    .append("|").append(columnName)
                    .append("|").append(dataType)
                    .append("|").append(comment)
                    .append("|").append(LINK_BREAK);
        }
        return sb.toString();
    }
}
