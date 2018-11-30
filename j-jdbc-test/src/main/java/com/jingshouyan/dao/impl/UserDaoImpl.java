package com.jingshouyan.dao.impl;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.jingshouyan.bean.UserBean;
import com.jingshouyan.dao.UserDao;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * @author jingshouyan
 * 11/29/18 5:27 PM
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl<UserBean> implements UserDao {


    public SqlRowSet test(){
        SqlRowSet rowSet = template.queryForRowSet("select * from UserBean where 1=2",new HashMap<>());

        return rowSet;
    }
}
