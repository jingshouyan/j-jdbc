package com.github.jingshouyan.jdbc.data.init.entity;

import com.github.jingshouyan.jdbc.comm.annotation.Column;
import com.github.jingshouyan.jdbc.comm.annotation.Key;
import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.entity.BaseDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Table("DATA_INIT_VERSION")
public class DataInitVersion extends BaseDO {
    @Key
    @Column(length = 50)
    private String version;

    private Integer successCount;
}
