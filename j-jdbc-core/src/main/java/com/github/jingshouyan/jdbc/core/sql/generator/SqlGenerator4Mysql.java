package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.bean.*;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mysql
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
public class SqlGenerator4Mysql<T> extends AbstractSqlGenerator<T> implements SqlGenerator<T> {


    public SqlGenerator4Mysql(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected String q() {
        return "`";
    }

    @Override
    public SqlPrepared queryLimit(List<Condition> conditions, Page<T> page, Collection<String> fields) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT " + columns(fields) + " FROM " + tableName();
        SqlPrepared whereSql = where(conditions);
        sql += whereSql.getSql();
        sql += orderBy(page.getOrderBies());
        long offset = (page.getPage() - 1) * page.getPageSize();
        sql += " LIMIT " + offset + "," + page.getPageSize();
        sqlPrepared.setSql(sql);
        sqlPrepared.setParams(whereSql.getParams());
        return sqlPrepared;
    }

    @Override
    public List<SqlPrepared> createTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName());
        sql.append(" (");
        for (ColumnInfo column : tableInfo.getColumns()) {
            if (column.isForeign()) {
                continue;
            }
            sql.append(columnString(column));
            sql.append(" ,");
        }
        sql.deleteCharAt(sql.length() - 1);
        ColumnInfo key = tableInfo.getKey();
        if (null != key) {
            sql.append(", PRIMARY KEY (");
            sql.append(columnName(key));
            sql.append(")");
        }
        for (IndexInfo indexInfo : tableInfo.getIndices()) {
            if (indexInfo.isUnique()) {
                sql.append(", UNIQUE (");
            } else {
                sql.append(", KEY (");
            }
            String index = indexInfo.getColumnInfos().stream()
                    .map(this::columnName)
                    .collect(Collectors.joining(","));
            sql.append(index);
            sql.append(")");
        }
        sql.append(")  COMMENT='").append(tableComment()).append("'");
        sqlPrepared.setSql(sql.toString());
        return Lists.newArrayList(sqlPrepared);
    }

    @Override
    public SqlPrepared dropTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "DROP TABLE IF EXISTS " + tableName();
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }

    @Override
    public SqlPrepared addColumn(ColumnInfo columnInfo) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "ALTER TABLE " + tableName() + " ADD " + columnString(columnInfo);
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }

    protected String columnString(@NonNull ColumnInfo column) {
        String str;
        switch (column.getDataType()) {
            case TINYINT:
                str = "TINYINT";
                break;
            case SMALLINT:
                str = "SMALLINT";
                break;
            case INT:
                str = "INT";
                break;
            case BIGINT:
                str = "BIGINT";
                break;
            case DOUBLE:
                str = "DOUBLE";
                break;
            case DECIMAL:
                str = "DECIMAL(" + column.getColumnLength() + "," + column.getScale() + ")";
                break;
            case TIME:
                str = "TIME";
                break;
            case DATETIME:
                str = "DATETIME";
                break;
            case JSON:
                str = "JSON";
                break;
            case TEXT:
                str = "TEXT";
                break;
            case MEDIUMTEXT:
                str = "MEDIUMTEXT";
                break;
            case LONGTEXT:
                str = "LONGTEXT";
                break;
            case VARCHAR:
            default:
                str = "VARCHAR(" + column.getColumnLength() + ")";
                break;
        }

        str = columnName(column) + " " + str;
        if (null != column.getDefaultData()) {
            str += " DEFAULT '" + column.getDefaultData() + "'";
        }
        if (null != column.getComment()) {
            str += " COMMENT '" + column.getComment() + "'";
        }
        return str;
    }


}
