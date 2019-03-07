package com.github.jingshouyan.jdbc.core.sql.generator;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * oracle
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
public class SqlGenerator4Oracle<T> extends AbstractSqlGenerator<T> implements SqlGenerator<T> {

    @Override
    protected char valueSeparate() {
        return ' ';
    }

    public SqlGenerator4Oracle(Class<T> clazz) {
       super(clazz);
    }

    @Override
    public SqlPrepared queryLimit(List<Condition> conditions, Page<T> page, Collection<String> fields) {
        SqlPrepared sqlPrepared = new SqlPrepared();
        String sql = "SELECT " + columns(fields) + " FROM " + tableName();
        SqlPrepared whereSql = where(conditions);
        sql += whereSql.getSql();
        sql += orderBy(page.getOrderBies());
        int offset = (page.getPage() - 1) * page.getPageSize();
        int end = offset + page.getPageSize();
        String sql2 = "";
        if(offset<=0){
            sql2 = " SELECT A.*, ROWNUM FROM ("+sql+") A WHERE ROWNUM <= "+end;
        }else{
            sql2 = "SELECT * FROM ( SELECT A.*, ROWNUM RN FROM ("+sql+") A WHERE ROWNUM <= "+end+" ) WHERE RN > "+offset;
        }
        sqlPrepared.setSql(sql2);
        sqlPrepared.setParams(whereSql.getParams());
        return sqlPrepared;
    }

    @Override
    public SqlPrepared createTableSql() {
        SqlPrepared sqlPrepared = new SqlPrepared();
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ");
        sql.append(tableName());
        sql.append(" (");
        TableInfo tableInfo = TableUtil.tableInfo(clazz);
        for (ColumnInfo column : tableInfo.getColumns()) {
            if(column.isForeign()){
                continue;
            }
            sql.append(columnString(column));
            sql.append(" ,");
        }
        sql.deleteCharAt(sql.length() - 1);
        ColumnInfo key = tableInfo.getKey();
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
        String sql = "DROP TABLE " + tableName() ;
        sqlPrepared.setSql(sql);
        return sqlPrepared;
    }

    protected String columnString(@NonNull ColumnInfo column) {
        String str;
        Class clazz = column.getField().getType();
        switch (clazz.getSimpleName().toLowerCase()) {
            case "byte":
                str = "NUMBER(4)";
                break;
            case "short":
                str = "NUMBER(6)";
                break;
            case "int":
            case "integer":
                str = "NUMBER(11)";
                break;
            case "long":
                str = "NUMBER(20)";
                break;
            case "boolean":
                str = "NUMBER(4)";
                break;
            case "float":
            case "double":
                str = "NUMBER(20,11)";
                break;
            default:
                if (column.getColumnLength() < Constant.VARCHAR_MAX_LENGTH) {
                    str = "VARCHAR2(" + column.getColumnLength() + ")";
                } else {
                    str = "CLOB";
                }
                break;

        }
        str = "" + column.getColumnName() + " " + str +" ";
        return str;
    }

}
