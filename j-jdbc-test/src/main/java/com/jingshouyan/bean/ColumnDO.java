package com.jingshouyan.bean;

import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.entity.Record;
import lombok.Data;

/**
 * @author jingshouyan
 * #date 2020/4/27 18:55
 */
@Data
@Table(value = "COLUMN_INFO", comment = "列信息")
public class ColumnDO implements Record {

    @Key
    private Long id;
    @Column(value = "TABLE_SCHEMA", comment = "库名")
    private String tableSchema;
    @Column(value = "TABLE_NAME", comment = "表名")
    private String tableName;
    @Column(value = "COLUMN_NAME", comment = "列名")
    private String columnName;
    @Column(value = "COLUMN_TYPE", comment = "列类型")
    private String columnType;
    @Column(value = "COLUMN_COMMENT", comment = "列备注")
    private String columnComment;
}
