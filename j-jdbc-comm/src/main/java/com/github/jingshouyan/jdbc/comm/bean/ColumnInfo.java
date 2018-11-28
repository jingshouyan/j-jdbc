package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Field;

/**
 * @author jingshouyan
 * 11/22/18 5:11 PM
 */
@Getter@Setter@ToString
public class ColumnInfo {
    private Field field;
    private String fieldName;
    private String columnName;
    private int columnLength;
    private boolean json;
    private boolean encrypt;
    private EncryptType encryptType;
    private String encryptKey;
    private boolean key;
    private boolean autoGen;
    private boolean index;
    private String comment;
    private int order;


    public ColumnInfo(Field field){
        // 默认值
        this.field = field;
        fieldName = field.getName();
        columnName = field.getName();
        columnLength = Constant.COLUMN_LENGTH_DEFAULT;
        json = false;
        encrypt = false;
        encryptType = EncryptType.NONE;
        encryptKey = Constant.COLUMN_ENCRYPT_KEY_DEFAULT;
        key = false;
        autoGen = false;
        index = false;
        comment = "";
        order = Constant.COLUMN_ORDER_DEFAULT;
        Column column = field.getAnnotation(Column.class);
        if (null != column) {
            if(!StringUtil.isNullOrEmpty(column.value())){
                columnName = column.value();
            }
            if(column.encryptType()!=EncryptType.NONE){
                encrypt = true;
                encryptType = column.encryptType();
            }
            columnLength = column.length();
            json = column.json();
            encryptType = column.encryptType();
            encryptKey = column.encryptKey();
            index = column.index();
            comment = column.comment();
            order = column.order();
        }
        //是否添加了 @Key 注解
        Key k = field.getAnnotation(Key.class);
        if (null != k) {
            key = true;
            autoGen = k.generatorIfNotSet();
        }
    }
}
