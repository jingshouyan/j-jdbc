package com.jingshouyan.dao.impl;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.jingshouyan.bean.UserBean;
import com.jingshouyan.dao.UserDao;
import org.springframework.stereotype.Repository;

/**
 * @author jingshouyan
 * 11/29/18 5:27 PM
 */
@Repository
public class UserDaoImpl extends BaseDaoImpl<UserBean> implements UserDao {

}
