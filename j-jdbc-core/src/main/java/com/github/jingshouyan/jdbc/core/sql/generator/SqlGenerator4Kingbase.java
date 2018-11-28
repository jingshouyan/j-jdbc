package com.github.jingshouyan.jdbc.core.sql.generator;


import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import lombok.NonNull;

/**
 * Kingbase
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
public class SqlGenerator4Kingbase<T> extends SqlGenerator4Oracle<T> {
    public SqlGenerator4Kingbase(Class<T> clazz) {
        super(clazz);
    }

    @Override
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
                    str = "VARCHAR(" + column.getColumnLength() + ")";
                } else {
                    str = "CLOB";
                }
                break;

        }
        str = "" + column.getColumnName() + " " + str +" ";
        return str;
    }
}
