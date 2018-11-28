package com.github.jingshouyan.jdbc.core.util.table;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.core.util.aes.AesUtil;
import com.github.jingshouyan.jdbc.core.util.json.JsonUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jingshouyan
 * 11/27/18 3:42 PM
 */
public class TableUtil {
    private static final Map<Class<?>, TableInfo> TABLE_MAP = new ConcurrentHashMap<>();


    public static TableInfo tableInfo(Class<?> clazz){
        return TABLE_MAP.computeIfAbsent(clazz,TableInfo::new);
    }

    public static String keyFieldName(Class<?> clazz) {
        ColumnInfo key = tableInfo(clazz).getKey();
        Preconditions.checkNotNull(key,String.format("class[%s] is not set key",clazz.toString()));
        return key.getFieldName();
    }


    public static String columnName(Class<?> clazz, String fieldName){
        ColumnInfo columnInfo = tableInfo(clazz).getFieldNameMap().get(fieldName);
        Preconditions.checkNotNull(columnInfo,
                String.format("[%s] dos not contains field [%s]", clazz.toString(), fieldName)
        );
        return columnInfo.getColumnName();
    }

    @SneakyThrows
    public static Map<String,Object> valueMap(@NonNull Object bean) {
        Map<String, Object> map = Maps.newHashMap();
        TableInfo beanTable = tableInfo(bean.getClass());
        for (String fieldName : beanTable.getFieldNameMap().keySet()) {
            Object value = fieldValue(bean,fieldName);
            if(null == value){
                continue;
            }
            map.put(fieldName, value);
        }
        return map;
    }



    @SneakyThrows
    public static Object fieldValue(Object bean,String fieldName){
        TableInfo beanTable = tableInfo(bean.getClass());
        ColumnInfo columnInfo = beanTable.getFieldNameMap().get(fieldName);
        Preconditions.checkNotNull(columnInfo,
                        String.format("[%s] dos not contains field [%s]", bean.getClass().toString(), fieldName)
        );
        Field field = columnInfo.getField();
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object value = field.get(bean);
        if (value == null){
            return null;
        }
        if(columnInfo.isJson()){
            value = JsonUtil.toJsonString(value);
        }
        if(columnInfo.isEncrypt()) {
            String password = null;
            if(columnInfo.getEncryptType()==EncryptType.FIXED){
                password = columnInfo.getEncryptKey();
            } else if(columnInfo.getEncryptType()==EncryptType.FLIED){
                Object v = fieldValue(bean,columnInfo.getEncryptKey());
                if(v != null){
                    password = String.valueOf(v);
                }
            }
            Preconditions.checkNotNull(password,"encryptType/decrypt password is null");
            value = AesUtil.encrypt(String.valueOf(value),password);
        }
        return value;
    }


}
