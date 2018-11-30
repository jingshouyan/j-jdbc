package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.jingshouyan.bean.UserBean;
import com.jingshouyan.dao.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Test
    public void rowSet() throws Exception{
        SqlRowSet rowSet = userDao.test();
        SqlRowSetMetaData md = rowSet.getMetaData();
        int count  = md.getColumnCount();
        Set<String> cn = Sets.newHashSet();
        for(String columnName : md.getColumnNames()){
            System.out.println(columnName);
            cn.add(columnName.toLowerCase());
        }
        Map<String,ColumnInfo> map = TableUtil.tableInfo(UserBean.class)
                .getLowerCaseColumnMap();
        List<ColumnInfo> columnInfos = Lists.newArrayList();
        for (String key : map.keySet()){
            if(!cn.contains(key)){
                columnInfos.add(map.get(key));
            }
        }
        rowSet.getMetaData().getColumnCount();
        System.out.println(rowSet);
    }

    @Test
    public void exist(){
        boolean b = userDao.existTable();
        System.out.println(b);
    }

}
