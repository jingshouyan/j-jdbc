package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.annotaion.Ignore;
import com.github.jingshouyan.jdbc.comm.annotaion.Table;
import com.github.jingshouyan.jdbc.comm.util.StringUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author jingshouyan
 * 11/22/18 5:27 PM
 */
@Getter@Setter@ToString
public class TableInfo {
    private Class<?> clazz;

    private String tableName;

    private String comment = "";

    private ColumnInfo key;

    private List<ColumnInfo> columns = new ArrayList<>();

    private Map<String, ColumnInfo> fieldNameMap = new HashMap<>();

    private Map<String, ColumnInfo> lowerCaseColumnMap = new HashMap<>();


    public TableInfo(Class<?> clazz){
        this.clazz = clazz;
        tableName = clazz.getSimpleName();
        comment = "";
        Table table = clazz.getAnnotation(Table.class);
        if(null != table){
            if(StringUtil.isNullOrEmpty(table.value())){
                tableName = table.value();
            }
            comment = table.comment();
        }
        //属性名 用于排除 重名的 父类中的属性
        Set<String> fieldNames = new HashSet<>();
        for (Class c = clazz; Object.class != c; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                //静态属性
                if (Modifier.isStatic(mod)) {
                    continue;
                }
                //排除添加 @Ignore 的属性
                if (field.isAnnotationPresent(Ignore.class)) {
                    continue;
                }
                if (!fieldNames.contains(field.getName())) {
                    fieldNames.add(field.getName());
                    ColumnInfo beanColumn = new ColumnInfo(field);
                    addColumnInfo(beanColumn);
                }
            }
        }
        getColumns().sort(Comparator.comparing(ColumnInfo::getOrder));
    }

    /**
     * 添加列信息
     *
     * @param column 列信息
     */
    private void addColumnInfo(ColumnInfo column) {
        columns.add(column);
        fieldNameMap.put(column.getFieldName(), column);
        lowerCaseColumnMap.put(column.getColumnName().toLowerCase(), column);
        if (column.isKey() && key == null) {
            //取第一个为 key
            //因为是先取 类 中的属性，然后再取 父类 中的属性
            key = column;
        }
    }
}
