package com.github.jingshouyan.jdbc.data.init.entity;

import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.bean.DataType;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import com.github.jingshouyan.jdbc.comm.entity.Record;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2020/4/14 13:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "DATA_INIT_RECORD",comment = "初始化数据记录")
public class DataInitRecord extends BaseDO {
    @Key
    private Long id;
    @Column(length = 50,comment = "版本id")
    private Long versionId;
    @Column(length = 50,comment = "版本")
    private String version;
    @Column(length = 100,comment = "数据对象类型")
    private String entity;
    @Column(json = true,dataType = DataType.MEDIUMTEXT,comment = "数据")
    private List<Record> data;
    @Column(comment = "导入结果")
    private Boolean success;
    @Column(comment = "错误信息")
    private String message;

}
