package com.jingshouyan.dao;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.jingshouyan.bean.UserDO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author jingshouyan
 * 11/29/18 5:26 PM
 */
public interface UserDao extends BaseDao<UserDO> {
    SqlRowSet test();
}
