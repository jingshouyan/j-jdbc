package com.jingshouyan.dao.impl;

import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.google.common.collect.Maps;
import com.jingshouyan.bean.TableDO;
import com.jingshouyan.dao.TableDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * 11/29/18 5:27 PM
 */
@Repository
public class TableDaoImpl extends BaseDaoImpl<TableDO> implements TableDao {

    @Override
    public List<TableDO> listBySchema(String schema) {
        String sql = "SELECT\n" +
                "\tTABLE_SCHEMA,\n" +
                "\tTABLE_NAME,\n" +
                "\tTABLE_COMMENT \n" +
                "FROM\n" +
                "\tINFORMATION_SCHEMA.TABLES \n" +
                "WHERE\n" +
                "\ttable_schema = :schema \n" +
                "\tAND (\n" +
                "\tTABLE_NAME LIKE '%\\_0' \n" +
                "\tOR TABLE_NAME NOT REGEXP '[0-9]$')";
        Map<String, Object> params = Maps.newHashMap();
        params.put("schema", schema);
        return template.query(sql, params, rowMapper);
    }
}
