package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotaion.Decimal;
import com.github.jingshouyan.jdbc.comm.bean.*;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import lombok.NonNull;

import java.lang.reflect.Field;
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
    public SqlPrepared createTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(tableName());
        sql.append(" (");
        TableInfo tableInfo = TableUtil.tableInfo(clazz);
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
            sql.append(", PRIMARY KEY (`");
            sql.append(key.getColumnName());
            sql.append("`)");
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
        sql.append(")  COMMENT='" + tableComment() + "';");
        sqlPrepared.setSql(sql.toString());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared dropTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "DROP TABLE IF EXISTS " + tableName() + ";";
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }

    @Override
    public SqlPrepared addColumn(ColumnInfo columnInfo) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "ALTER TABLE " + tableName() + " ADD " + columnString(columnInfo) + ";";
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }

    protected String columnString(@NonNull ColumnInfo column) {
        String str;
        Class clazz = column.getField().getType();
        switch (clazz.getSimpleName().toLowerCase()) {
            case "byte":
                str = "TINYINT";
                break;
            case "short":
                str = "SMALLINT";
                break;
            case "int":
            case "integer":
                str = "INT";
                break;
            case "long":
                str = "BIGINT";
                break;
            case "boolean":
                str = "TINYINT";
                break;
            case "float":
            case "double":
                str = "DOUBLE";
                break;
            case "bigdecimal":
                str = decimalStr(column.getField());
                break;
            case "localTime":
            case "localtime":
                str = "TIME";
                break;
            case "date":
            case "timestamp":
            case "localdate":
            case "localdatetime":
                str = "DATETIME";
                break;
            default:
                if (column.isJson() && !column.isEncrypt()) {
                    str = "JSON";
                    break;
                }
                if (column.getColumnLength() < Constant.VARCHAR_MAX_LENGTH) {
                    str = "VARCHAR(" + column.getColumnLength() + ")";
                } else {
                    str = "TEXT";
                }
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

    private String decimalStr(Field field) {
        Decimal decimal = field.getAnnotation(Decimal.class);
        if (decimal != null) {
            return String.format("DECIMAL(%d,%d)", decimal.precision(), decimal.scale());
        }
        return "DECIMAL(20,4)";
    }

}
