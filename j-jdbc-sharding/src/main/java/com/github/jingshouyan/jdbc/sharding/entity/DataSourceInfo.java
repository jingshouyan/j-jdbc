package com.github.jingshouyan.jdbc.sharding.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

import static com.github.jingshouyan.jdbc.sharding.Constant.DATA_SOURCE_TYPE_NORMAL;

/**
 * @author jingshouyan
 * #date 2020/2/9 14:59
 */
@Data
public class DataSourceInfo {

    private boolean showLog = false;
    private int type = DATA_SOURCE_TYPE_NORMAL;
    private Map<String, String> routeMap;
    private int tableShard = 5;
    private List<DatabaseLinkInfo> linkInfos;

}
