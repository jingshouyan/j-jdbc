package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import lombok.NonNull;

import java.util.List;

/**
 * @author jingshouyan
 * @date 2018/5/9 18:56
 */
public class SqlGenerator4Gbase<T> extends AbstractSqlGenerator<T> {

    public SqlGenerator4Gbase(Class<T> clazz){
        super(clazz);
    }

    @Override
    protected char valueSeparate() {
        return ' ';
    }


    @Override
    public SqlPrepared query(List<Condition> conditions, Page<T> page) {
        int offset = (page.getPage() - 1) * page.getPageSize();
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT skip "+offset+" first "+page.getPageSize()+" * FROM "+tableName();
        SqlPrepared whereSql = where(conditions);
        sql += whereSql.getSql();
        sql += orderBy(page.getOrderBies());
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
        TableInfo beanTable = TableUtil.tableInfo(clazz);
        for (ColumnInfo column : beanTable.getColumns()) {
            sql.append(columnString(column));
            sql.append(" ,");
        }
        sql.deleteCharAt(sql.length() - 1);
        ColumnInfo key = beanTable.getKey();
        if (null != key) {
            sql.append(", PRIMARY KEY (");
            sql.append(key.getColumnName());
            sql.append(")");
        }
        sql.append(")");
        sqlPrepared.setSql(sql.toString());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared dropTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "DROP TABLE IF EXIST" + tableName() ;
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }


    protected String columnString(@NonNull ColumnInfo column) {
        String str;
        Class clazz = column.getField().getType();
        switch (clazz.getSimpleName().toLowerCase()) {
            case "byte":
                str = "NUMERIC(4,0)";
                break;
            case "short":
                str = "NUMERIC(6,0)";
                break;
            case "int":
            case "integer":
                str = "NUMERIC(11,0)";
                break;
            case "long":
                str = "NUMERIC(24,0)";
                break;
            case "boolean":
                str = "NUMERIC(4,0)";
                break;
            case "float":
            case "double":
                str = "NUMERIC(20,11)";
                break;
            default:
                if (column.getColumnLength() < Constant.VARCHAR_MAX_LENGTH) {
                    str = "LVARCHAR(" + column.getColumnLength() + ")";
                } else {
                    str = "TEXT";
                }
                break;
        }
        str = "" + column.getColumnName() + " " + str +" ";
        return str;
    }
}
