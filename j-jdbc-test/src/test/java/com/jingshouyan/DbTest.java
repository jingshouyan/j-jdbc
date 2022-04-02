package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.google.common.collect.Lists;
import com.jingshouyan.bean.UserDO;
import com.jingshouyan.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * 11/29/18 5:38 PM
 */
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
    public void doc() {
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
        Page<UserDO> page = userDao.queryPage(conditions, new Page<>());
        System.out.println(page);
    }

    @Test
    public void queryEncryptLike() {
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("encryptTest").like("士大夫3%")
                .conditions();
        List<UserDO> users = userDao.query(conditions);
        users.forEach(System.out::println);
    }

    @Test
    public void sqlInject() {
        List<Condition> conditions = ConditionUtil.newInstance()
                .field("age").eq("1' or 1=1 ")
                .conditions();
        List<UserDO> users = userDao.query(conditions);
        users.forEach(System.out::println);
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
            userBean.setText("test");
            userBean.setNew1("new1 dfd");
            userBean.setNew2("new2 dfd");
            userBean.setNew4("new3 dfd");
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
            user.setAge(user.getAge() + 3);
            user.setNickname(UUID.randomUUID().toString());
            return user;
        }).collect(Collectors.toList());
        userDao.batchUpdate(users);
        users.forEach(System.out::println);
    }

    @Test
    public void updateAll() {
        UserDO userDO = new UserDO();
        userDO.setAge(22);
        userDO.setKey("key-test ");
        userDao.update(userDO, null);
    }

    @Test
    public void update() {
        Page<UserDO> page = new Page<>();
        page.setPageSize(1);
        List<UserDO> users = userDao.queryLimit(null, page);
        users.stream().findFirst().ifPresent(user -> {
            user.setAcc1(BigDecimal.valueOf(4564.786555));
            userDao.update(user);
            System.out.println(user);
        });
    }

    @Test
    public void exist() {
        boolean b = userDao.existTable();
        System.out.println(b);
    }

    @Test
    public void nullTest() {
        UserDO user = new UserDO();
        user.setName("掌声");
        user.setAge(33998);
        user.setTags(Lists.newArrayList("哈哈"));
        user.setNickname("大嘴巴子");
        user.setEncryptTest("发生温热");
        user.setKey("13123");
        user.setAcc1(new BigDecimal("1.1"));
        user.setAcc2(new BigDecimal("2.2"));
        user.setAcc3(new BigDecimal("3.3"));
        user.setAcc4(new BigDecimal("4.4"));
        user.setAcc5(new BigDecimal("5.5"));
        user.setAcc6(new BigDecimal("6.6"));
        user.setAcc7(new BigDecimal("7.7"));
        user.setAcc8(new BigDecimal("8.8"));
        user.setLocalDate(LocalDate.now());
        user.setLocalDateTime(LocalDateTime.now());
        user.setLocalTime(LocalTime.now());
        user.setText("test");
        user.setNew1("new1 dfd");
        user.setNew2("new2 dfd");
        user.setNew4("new3 dfd");
        userDao.insert(user);
        UserDO u2 = userDao.find(user.getId()).get();

        u2.setNew1("");
        userDao.update(u2);
        userDao.find(user.getId());

    }

    @Test
    public void nullTest2() {

        List<Condition> conditions = ConditionUtil.newInstance()
                .field("age").gt(302).lt(310)
                .conditions();
        UserDO userDO = new UserDO();
        userDO.setText("new xxx is null");
        userDO.setNew1("");
        userDO.setNew2("");
        userDO.setNew4("");
        userDao.update(userDO, conditions);
    }

}
