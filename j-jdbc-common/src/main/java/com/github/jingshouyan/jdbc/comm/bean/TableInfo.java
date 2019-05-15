package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.annotaion.Ignore;
import com.github.jingshouyan.jdbc.comm.annotaion.Index;
import com.github.jingshouyan.jdbc.comm.annotaion.ListQueryFields;
import com.github.jingshouyan.jdbc.comm.annotaion.Table;
import com.github.jingshouyan.jdbc.comm.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jingshouyan
 * 11/22/18 5:27 PM
 */
@Getter@Setter@ToString
public class TableInfo {
    private Class<?> clazz;

    private String tableName;

    private String comment;

    private ColumnInfo key;

    private List<ColumnInfo> columns = new ArrayList<>();

    private Map<String, ColumnInfo> fieldNameMap = new HashMap<>();

    private Map<String, ColumnInfo> lowerCaseColumnMap = new HashMap<>();

    private List<IndexInfo> indices = new ArrayList<>();

    private List<String> listQueryFields;


    public TableInfo(Class<?> clazz){
        this.clazz = clazz;
        tableName = clazz.getSimpleName();
        comment = "";
        Table table = clazz.getAnnotation(Table.class);
        if(null != table){
            if(!StringUtil.isNullOrEmpty(table.value())){
                tableName = table.value();
            }
            comment = table.comment();
        }

        // 属性名 用于排除 重名的 父类中的属性
        Set<String> fieldNames = new HashSet<>();
        for (Class c = clazz; Object.class != c; c = c.getSuperclass()) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                // 静态属性
                if (Modifier.isStatic(mod)) {
                    continue;
                }
                // 排除添加 @Ignore 的属性
                if (field.isAnnotationPresent(Ignore.class)) {
                    continue;
                }
                if (!fieldNames.contains(field.getName())) {
                    fieldNames.add(field.getName());
                    ColumnInfo beanColumn = new ColumnInfo(field);
                    addColumnInfo(beanColumn);
                    // field 中添加的 @Index 处理
                    Index index = field.getAnnotation(Index.class);
                    if(index != null) {
                        IndexInfo indexInfo = new IndexInfo();
                        indexInfo.getColumnInfos().add(beanColumn);
                        indexInfo.setUnique(index.unique());
                        indices.add(indexInfo);
                    }
                }

            }
        }
        getColumns().sort(Comparator.comparing(ColumnInfo::getOrder));

        // class 上添加的 @Index 处理
        List<IndexInfo> clazzIndexInfo = Arrays.stream(clazz.getAnnotationsByType(Index.class))
                .map(
                        index -> {
                            IndexInfo indexInfo = new IndexInfo();
                            List<ColumnInfo> columnInfos = Arrays.stream(index.value())
                                    .map(fieldName -> {
                                        ColumnInfo columnInfo = fieldNameMap.get(fieldName);
                                        assert columnInfo != null;
                                        return columnInfo;
                                    })
                                    .collect(Collectors.toList());
                            indexInfo.setColumnInfos(columnInfos);
                            indexInfo.setUnique(index.unique());
                            return indexInfo;
                        }
                ).collect(Collectors.toList());
        indices.addAll(clazzIndexInfo);

        ListQueryFields lqf = clazz.getAnnotation(ListQueryFields.class);
        if(null != lqf) {
            listQueryFields = Stream.of(lqf.value()).collect(Collectors.toList());
        }
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
