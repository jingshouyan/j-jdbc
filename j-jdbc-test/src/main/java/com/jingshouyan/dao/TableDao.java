package com.jingshouyan.dao;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.jingshouyan.bean.TableDO;

import java.util.List;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface TableDao extends BaseDao<TableDO> {
    /**
     * 查询表信息
     *
     * @param schema 库
     * @return 列信息
     */
    List<TableDO> listBySchema(String schema);

}
