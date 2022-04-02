package com.github.jingshouyan.jdbc.core.util.table;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.EncryptType;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.core.encryption.EncryptionProvider;
import com.github.jingshouyan.jdbc.core.keygen.KeyGeneratorProvider;
import com.github.jingshouyan.jdbc.core.util.json.JsonUtil;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author jingshouyan
 * 11/27/18 3:42 PM
 */
public class TableUtil {
    private static final Map<Class<?>, TableInfo> TABLE_MAP = new ConcurrentHashMap<>();


    public static TableInfo tableInfo(Class<?> clazz) {
        TableInfo tableInfo = TABLE_MAP.get(clazz);
        if(tableInfo != null) {
            return tableInfo;
        }
        return TABLE_MAP.computeIfAbsent(clazz, TableInfo::new);
    }

    public static String keyFieldName(Class<?> clazz) {
        ColumnInfo key = tableInfo(clazz).getKey();
        Preconditions.checkNotNull(key, String.format("class[%s] is not set key", clazz.toString()));
        return key.getFieldName();
    }


    public static String columnName(Class<?> clazz, String fieldName) {
        ColumnInfo columnInfo = tableInfo(clazz).getFieldNameMap().get(fieldName);
        Preconditions.checkNotNull(columnInfo,
                String.format("[%s] dos not contains field [%s]", clazz.toString(), fieldName)
        );
        return columnInfo.getColumnName();
    }

    @SneakyThrows
    public static Map<String, Object> valueMap(@NonNull Object bean) {
        TableInfo beanTable = tableInfo(bean.getClass());
        int columnSize = beanTable.getColumns().size();
        int capacity = capacity(columnSize);
        Map<String, Object> map = new LinkedHashMap<>(capacity);
        beanTable.getColumns().stream()
                .filter(c -> !c.isForeign())
                .forEach(c -> {
                    String fieldName = c.getFieldName();
                    Object value = fieldValue(bean, c);
                    if (null != value) {
                        map.put(fieldName, value);
                    }
                });
        return map;
    }


    @SneakyThrows
    public static Object fieldValue(Object bean, ColumnInfo columnInfo) {
        Field field = columnInfo.getField();
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        Object value = field.get(bean);
        if (value == null) {
            return null;
        }
        if (columnInfo.isJson()) {
            value = JsonUtil.toJsonString(value);
        }
        if (columnInfo.isEncrypt()) {
            String password = null;
            if (columnInfo.getEncryptType() == EncryptType.FIXED) {
                password = columnInfo.getEncryptKey();
            } else if (columnInfo.getEncryptType() == EncryptType.FLIED) {
                TableInfo beanTable = tableInfo(bean.getClass());
                ColumnInfo encryt = beanTable.getFieldNameMap().get(columnInfo.getEncryptKey());
                Object v = fieldValue(bean, encryt);
                if (v != null) {
                    password = String.valueOf(v);
                }
            }
            Preconditions.checkNotNull(password, "encryptType/decrypt password is null");
            value = EncryptionProvider.encrypt(String.valueOf(value), password);
        }
        return value;
    }

    @SneakyThrows
    public static void genKey(Object bean) {
        ColumnInfo columnInfo = tableInfo(bean.getClass()).getKey();
        if (null == columnInfo || !columnInfo.isAutoGen()) {
            return;
        }
        Object value = fieldValue(bean, columnInfo);
        if (isEmpty(value)) {
            Class<?> c = columnInfo.getField().getType();
            long l = KeyGeneratorProvider
                    .getKeyGenerator()
                    .generateKey(bean.getClass().getSimpleName());

            if (c == Long.class || c == long.class) {
                columnInfo.getField().set(bean, l);
            } else if (c == String.class) {
                String v = String.valueOf(l);
                if (bean instanceof BaseDO) {
                    v = ((BaseDO) bean).idPrefix() + v + ((BaseDO) bean).idSuffix();
                }
                columnInfo.getField().set(bean, v);
            }
        }
    }

    public static boolean isEmpty(Object obj) {
        return null == obj;
    }


    private static final int MIN_EXPECTED_SIZE = 3;
    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private static int capacity(int expectedSize) {
        if (expectedSize < MIN_EXPECTED_SIZE) {
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE;
    }
}
