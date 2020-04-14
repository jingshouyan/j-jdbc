package com.github.jingshouyan.jdbc.data.init.dao;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;

import java.util.Optional;

/**
 * @author jingshouyan
 * #date 2020/4/14 15:14
 */
public interface DataInitVersionDao extends BaseDao<DataInitVersion> {

    /**
     * 获取最新的版本号
     *
     * @return 最新的版本号
     */
    Optional<DataInitVersion> latestVersion();
}
