package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.jingshouyan.bean.UserBean;
import com.jingshouyan.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author jingshouyan
 * 11/29/18 5:38 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class DbTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void insert(){
        UserBean userBean = new UserBean();
        userBean.setName("张三");
        userBean.setAge(30);
        userBean.setTags(Lists.newArrayList("a","b"));
        userDao.insert(userBean);
        log.info(userBean.toString());
        UserBean userBean1 = userDao.find(userBean.getId()).get();
        log.info(userBean1.toString());
    }

    @Test
    public void query(){
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("age").gt(20).conditions();
        List<UserBean> userBeans = userDao.query(conditions);
        userBeans.forEach(System.out::println);

    }

}