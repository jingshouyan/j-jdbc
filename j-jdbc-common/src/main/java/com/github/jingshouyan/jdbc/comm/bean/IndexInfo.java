package com.github.jingshouyan.jdbc.comm.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/5/15 15:34
 */

@Data
public class IndexInfo {
    List<ColumnInfo> columnInfos = new ArrayList<>();
    boolean unique = false;
}
