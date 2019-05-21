package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jdbc.core.util.json.JsonUtil;
import com.google.common.collect.Lists;
import com.jingshouyan.bean.UserDO;
import com.jingshouyan.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    public void insert() {
        UserDO userBean = new UserDO();
        userBean.setName("张三");
        userBean.setAge(30);
        userBean.setTags(Lists.newArrayList("a", "b"));
        userBean.setNickname("alkaksdjflk");
        userBean.setEncryptTest("士大夫");
        userBean.setAcc1(new BigDecimal("12.5744334"));
        userBean.setAcc2(new BigDecimal("1112.578867"));
        userDao.insert(userBean);

        log.debug(userBean.toString());
        UserDO userBean1 = userDao.find(userBean.getId()).get();
        log.debug(userBean1.toString());
    }

    @Test
    public void query() {
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("age").gt(20).lte(89)
//                .field("nickname").eq("1' or '2'='2 ")
                .field("encryptTest").notIn(Lists.newArrayList("士大夫1", "士大夫2"))

                .conditions();
        List<UserDO> userBeans = userDao.query(conditions);
        userBeans.forEach(System.out::println);
        System.out.println(JsonUtil.toJsonString(userBeans));
    }

    @Test
    public void batchInsert() {
        List<UserDO> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserDO userBean = new UserDO();
            userBean.setName("张三");
            userBean.setAge(30 + i);
            userBean.setTags(Lists.newArrayList("a", "b"));
            userBean.setNickname("alkaksdjflk");
            userBean.setEncryptTest("士大夫" + i);
            users.add(userBean);
        }
        userDao.batchInsert(users);
    }

    @Test
    public void batchUpdate() {
        List<UserDO> users = userDao.query(null);
        userDao.batchUpdate(users);
    }

    @Test
    public void exist() {
        boolean b = userDao.existTable();
        System.out.println(b);
    }

}
