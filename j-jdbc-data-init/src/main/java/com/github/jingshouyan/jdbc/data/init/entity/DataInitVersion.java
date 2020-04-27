package com.github.jingshouyan.jdbc.data.init.entity;

import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.bean.DataType;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table(value = "DATA_INIT_VERSION", comment = "初始化数据版本")
public class DataInitVersion extends BaseDO {
    @Key
    private Long id;

    @Column(length = 50, comment = "版本")
    private String version;
    @Column(length = 500, comment = "执行的类")
    private String clazz;
    @Column(length = 50, comment = "执行结果")
    private Boolean success;
    @Column(dataType = DataType.TEXT, comment = "错误信息")
    private String message;

}
