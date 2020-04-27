package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        userBean.setAge(2333);
        userBean.setKey("adf-key");
        userBean.setTags(Lists.newArrayList("a", "b"));
        userBean.setNickname("alkaksdjflk");
        userBean.setEncryptTest("士大夫");
        userBean.setAcc1(new BigDecimal("12.5744334"));
        userBean.setAcc2(new BigDecimal("1112.578867"));
        userBean.setLocalDate(LocalDate.now());
        userDao.insert(userBean);

        log.debug(userBean.toString());
        UserDO userBean1 = userDao.find(userBean.getId()).get();
        log.debug(userBean1.toString());
    }

    @Test
    public void doc(){
        String doc = userDao.doc();
        log.info(doc);
    }

    @Test
    public void queryRangeKey() {
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("id")
                .lt("0")
                .conditions();
        List<UserDO> userBeans = userDao.query(conditions);

    }

    @Test
    public void query() {
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("age")
//                .eq(25)
//                .between(22,25)
//                .gt(20).lte(22)
//                .field("nickname").eq("1' or '2'='2 ")
//                .field("encryptTest").notIn(Lists.newArrayList("士大夫1", "士大夫2"))

                .conditions();
        List<UserDO> userBeans = userDao.query(conditions);
        userBeans.forEach(System.out::println);
        String json = JsonUtil.toJsonString(userBeans);
        System.out.println(json);
        long count = userDao.count(conditions);
        System.out.println(count);
        userBeans = JsonUtil.toList(json, UserDO.class);
        System.out.println(userBeans);
        Page<UserDO> page = userDao.queryPage(conditions, new Page<>());
        System.out.println(page);
    }

    @Test
    public void queryDistinct() {
        List<UserDO> users = userDao.queryDistinct(null, Lists.newArrayList("tags"));
        System.out.println(users);
    }

    @Test
    public void batchInsert() {
        List<UserDO> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            UserDO userBean = new UserDO();
            userBean.setName("张三");
            userBean.setAge(302 + i);
            userBean.setTags(Lists.newArrayList("a", "b"));
            userBean.setNickname("alkaksdjflk");
            userBean.setEncryptTest("士大夫" + i);
            userBean.setLocalDate(LocalDate.now());
            userBean.setLocalDateTime(LocalDateTime.now());
            userBean.setLocalTime(LocalTime.now());
            users.add(userBean);
        }
        userDao.batchInsert(users);
    }

    @Test
    public void batchUpdate() {
        List<UserDO> users = userDao.query(null);
        users = users.stream().map(user -> {
            UserDO u = new UserDO();
            u.setId(user.getId());
            u.setName(user.getName() + "abc");
            u.setAge(333);
            return u;
        }).collect(Collectors.toList());
        userDao.batchUpdate(users);
    }

    @Test
    public void updateAll() {
        UserDO userDO = new UserDO();
        userDO.setAge(22);
        userDO.setKey("key-test ");
        userDao.update(userDO, null);
    }

    @Test
    public void exist() {
        boolean b = userDao.existTable();
        System.out.println(b);
    }

}
