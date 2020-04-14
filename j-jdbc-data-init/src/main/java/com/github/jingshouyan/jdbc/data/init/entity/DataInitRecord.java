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
@Table("DATA_INIT_RECORD")
public class DataInitRecord extends BaseDO {
    @Key
    private Long id;
    @Column(length = 50,comment = "版本")
    private String version;
    @Column(length = 100,comment = "数据对象类型")
    private String entity;

    private Boolean success;
    @Column(json = true,dataType = DataType.MEDIUMTEXT,comment = "数据")
    private List<Record> data;

}
