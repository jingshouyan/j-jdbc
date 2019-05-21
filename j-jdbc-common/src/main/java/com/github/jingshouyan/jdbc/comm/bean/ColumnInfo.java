package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.Constant;
import com.github.jingshouyan.jdbc.comm.annotaion.Column;
import com.github.jingshouyan.jdbc.comm.annotaion.Foreign;
import com.github.jingshouyan.jdbc.comm.annotaion.Key;
import com.github.jingshouyan.jdbc.comm.util.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jingshouyan
 * 11/22/18 5:11 PM
 */
@Data@EqualsAndHashCode
public class ColumnInfo {
    private Field field;
    private String fieldName;
    private String columnName;
    private boolean immutable;
    private boolean router;
    private int columnLength;
    private boolean json;
    private boolean encrypt;
    private EncryptType encryptType;
    private String encryptKey;
    private boolean key;
    private boolean autoGen;
    private String defaultData;
    private String comment;
    private int order;

    private boolean foreign;
    private ForeignInfo foreignInfo;


    public ColumnInfo(Field field){
        // 默认值
        this.field = field;
        fieldName = field.getName();
        columnName = field.getName();
        Foreign f = field.getAnnotation(Foreign.class);
        if(f!=null){
            foreign = true;
            ForeignInfo info = new ForeignInfo();
            Class<?> type = field.getType();
            if(Collection.class.isAssignableFrom(type)){
                info.setCollection(true);
                if(Set.class.isAssignableFrom(type) || List.class.isAssignableFrom(type)){
                    info.setCollectionType(type);
                } else {
                    String message = "field: [" + field.getName() + "] must be set|list";
                    throw new IllegalArgumentException(message);
                }
                ParameterizedType type1 = (ParameterizedType)field.getGenericType();
                info.setForeignType((Class<?>) type1.getActualTypeArguments()[0]);
            } else {
                info.setCollection(false);
                info.setForeignType(type);
            }
            info.setThisKey(f.thisKey());
            info.setThatKey(f.thatKey());
            List<String> list = Stream.of(f.value()).collect(Collectors.toList());
            info.setFields(list);
            foreignInfo = info;
        } else {
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
            Column column = field.getAnnotation(Column.class);
            if (null != column) {
                immutable = column.immutable();
                router = column.router();
                if(router) {
                    // 路由列也不可变
                    immutable = true;
                }
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
                if(!"".equals(column.defaultData())){
                    defaultData = column.defaultData();
                }
                comment = column.comment();
                order = column.order();
            }
            //是否添加了 @Key 注解
            Key k = field.getAnnotation(Key.class);
            if (null != k) {
                key = true;
                //主键 一定是不可变字段
                immutable = true;
                autoGen = k.generatorIfNotSet();
            }
        }

    }
}
