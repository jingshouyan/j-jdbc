package com.jingshouyan.dao;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.jingshouyan.bean.ColumnDO;
import com.jingshouyan.bean.UserDO;

import java.util.List;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface ColumnDao extends BaseDao<ColumnDO> {
    /**
     * 查询列信息
     * @param schema 库
     * @return 列信息
     */
    List<ColumnDO> listBySchema(String schema);
}
