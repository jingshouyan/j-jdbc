package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Decimal;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;

/**
 * @author jingshouyan
 * 11/22/18 5:11 PM
 */
@Data
@EqualsAndHashCode
public class ColumnInfo {
    private Field field;
    private String fieldName;
    private String columnName;
    private boolean immutable;
    private boolean router;
    private int columnLength;
    /**
     * 小数位
     */
    private int scale;
    private boolean json;
    private boolean encrypt;
    private EncryptType encryptType;
    private String encryptKey;
    private boolean key;
    private boolean autoGen;
    private String defaultData;
    private String comment;
    private int order;
    private DataType dataType;

    private boolean foreign;
    private ForeignInfo foreignInfo;

    public boolean isFixEncrypted() {
        return encrypt && encryptType == EncryptType.FIXED;
    }


    public ColumnInfo(Field field) {
        // 默认值
        this.field = field;
        fieldName = field.getName();
        columnName = field.getName();

        immutable = false;
        router = false;
        columnLength = Constant.COLUMN_LENGTH_DEFAULT;
        json = false;
        encrypt = false;
        encryptType = EncryptType.NONE;
        encryptKey = "";
        key = false;
        autoGen = false;
        comment = "";
        order = Constant.COLUMN_ORDER_DEFAULT;
        dataType = DataType.TYPE_INFERENCE;
        Column column = field.getAnnotation(Column.class);
        if (null != column) {
            immutable = column.immutable();
            router = column.router();
            if (router) {
                // 路由列也不可变
                immutable = true;
            }
            if (!StringUtil.isNullOrEmpty(column.value())) {
                columnName = column.value();
            }
            if (column.encryptType() != EncryptType.NONE) {
                encrypt = true;
                encryptType = column.encryptType();
            }
            columnLength = column.length();
            json = column.json();
            encryptType = column.encryptType();
            encryptKey = column.encryptKey();
            if (!"".equals(column.defaultData())) {
                defaultData = column.defaultData();
            }
            comment = column.comment();
            order = column.order();
            dataType = column.dataType();
        }

        //是否添加了 @Key 注解
        Key k = field.getAnnotation(Key.class);
        if (null != k) {
            key = true;
            //主键 一定是不可变字段
            immutable = true;
            autoGen = k.generatorIfNotSet();
        }

        // 推断数据类型
        typeInference();


    }

    private void typeInference() {
        if (dataType == DataType.TYPE_INFERENCE) {
            Class clazz = this.getField().getType();
            switch (clazz.getSimpleName().toLowerCase()) {
                case "byte":
                case "boolean":
                    dataType = DataType.TINYINT;
                    break;
                case "short":
                    dataType = DataType.SMALLINT;
                    break;
                case "int":
                case "integer":
                    dataType = DataType.INT;
                    break;
                case "long":
                    dataType = DataType.BIGINT;
                    break;
                case "float":
                case "double":
                    dataType = DataType.DOUBLE;
                    break;
                case "bigdecimal":
                    dataType = DataType.DECIMAL;
                    break;
                case "localtime":
                    dataType = DataType.TIME;
                    break;
                case "date":
                case "timestamp":
                case "localdate":
                case "localdatetime":
                    dataType = DataType.DATETIME;
                    break;
                default:
                    if (this.isJson() && !this.isEncrypt()) {
                        dataType = DataType.JSON;
                        break;
                    }
                    if (this.getColumnLength() < Constant.VARCHAR_MAX_LENGTH) {
                        dataType = DataType.VARCHAR;
                    } else {
                        dataType = DataType.TEXT;
                    }
                    break;
            }

        }
        // 字段长度&小数位
        switch (dataType) {
            case VARCHAR:
                if (columnLength == Constant.COLUMN_LENGTH_DEFAULT) {
                    if (key) {
                        columnLength = Constant.VARCHAR_KEY_DEFAULT_LENGTH;
                    } else {
                        columnLength = Constant.VARCHAR_DEFAULT_LENGTH;
                    }
                }
                break;
            case DECIMAL:
                Decimal decimal = field.getAnnotation(Decimal.class);
                if (decimal == null) {
                    columnLength = Constant.DECIMAL_PRECISION_DEFAULT;
                    scale = Constant.DECIMAL_SCALE_DEFAULT;
                } else {
                    columnLength = decimal.precision();
                    scale = decimal.scale();
                }
                break;
            default:
        }
    }
}
