package com.jingshouyan.dao.impl;

import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.google.common.collect.Maps;
import com.jingshouyan.bean.ColumnDO;
import com.jingshouyan.dao.ColumnDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * 11/29/18 5:27 PM
 */
@Repository
public class ColumnDaoImpl extends BaseDaoImpl<ColumnDO> implements ColumnDao {


    @Override
    public List<ColumnDO> listBySchema(String schema) {
        String sql = "SELECT\n" +
                "\ttable_schema,\n" +
                "\tTABLE_NAME,\n" +
                "\tCOLUMN_NAME,\n" +
                "\tCOLUMN_TYPE,\n" +
                "\tCOLUMN_COMMENT \n" +
                "FROM\n" +
                "\tINFORMATION_SCHEMA.COLUMNS \n" +
                "WHERE\n" +
                "\ttable_schema = :schema \n" +
                "\tAND ( TABLE_NAME LIKE '%\\_0' OR TABLE_NAME NOT REGEXP '[0-9]$' ) ";
        Map<String, Object> params = Maps.newHashMap();
        params.put("schema", schema);
        return template.query(sql, params, rowMapper);
    }
}
