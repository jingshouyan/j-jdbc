package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.google.common.collect.Lists;
import com.jingshouyan.bean.ForeignDO;
import com.jingshouyan.bean.UserDO;
import com.jingshouyan.dao.ForeignDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/3/7 14:53
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class ForeignTest {

    public static void main(String[] args) {
        TableInfo info = TableUtil.tableInfo(ForeignDO.class);
        System.out.println(info);
    }

    @Autowired
    private ForeignDao foreignDao;

    @Test
    public void insert() {
        ForeignDO foreignDO = new ForeignDO();
//        foreignDO.setId("123");
        foreignDO.setUserId("1111");
        foreignDO.setUsers(Lists.newArrayList());
        foreignDO.setU2(new ArrayList<UserDO>());
        foreignDO.setU(new UserDO());
        foreignDao.insert(foreignDO);
    }

    @Test
    public void batchInsert() {
        List<ForeignDO> foreignDOS = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            ForeignDO foreignDO = new ForeignDO();
            foreignDO.setUserId("1111");
            foreignDO.setUsers(Lists.newArrayList());
            foreignDO.setU2(new ArrayList<UserDO>());
            foreignDO.setU(new UserDO());
            foreignDOS.add(foreignDO);
        }
        foreignDao.batchInsert(foreignDOS);
    }

    @Test
    public void update() {
        ForeignDO foreignDO = new ForeignDO();
        foreignDO.setId("123");
        foreignDO.setUserId("122");
        foreignDO.setUsers(Lists.newArrayList());
        foreignDO.setU2(new ArrayList<UserDO>());
        foreignDO.setU(new UserDO());
        foreignDao.update(foreignDO);
    }

    @Test
    public void query() {
        ForeignDO foreignDO = foreignDao.find("123").get();
        System.out.println(foreignDO);
    }

}
