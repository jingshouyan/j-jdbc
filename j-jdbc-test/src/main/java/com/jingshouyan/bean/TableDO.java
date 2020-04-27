package com.jingshouyan.bean;

import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.entity.Record;
import lombok.Data;

/**
 * @author jingshouyan
 * #date 2020/4/27 18:47
 */
@Data
@Table(value = "TABLE_INFO",comment = "表信息")
public class TableDO implements Record {

    @Key
    private Long id;
    @Column(value = "TABLE_SCHEMA",comment = "库名")
    private String tableSchema;
    @Column(value = "TABLE_NAME",comment = "表名")
    private String tableName;
    @Column(value = "TABLE_COMMENT",comment = "表名")
    private String tableComment;


}
