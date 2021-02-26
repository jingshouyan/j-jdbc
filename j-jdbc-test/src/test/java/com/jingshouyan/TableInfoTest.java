package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.annotation.Table;
import com.github.jingshouyan.jdbc.comm.bean.TableInfo;
import com.github.jingshouyan.jdbc.comm.exception.IllegalTypeException;
import com.github.jingshouyan.jdbc.core.sql.SqlPrepared;
import com.github.jingshouyan.jdbc.core.sql.generator.SqlGenerator4Mysql;
import com.github.jingshouyan.jdbc.core.util.table.TableUtil;
import com.jingshouyan.bean.UserDO;
import org.junit.Test;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author jingshouyan
 * #date 2019/1/30 11:53
 */


public class TableInfoTest {
    public static void main(String[] args) {
        TableInfo tableInfo = TableUtil.tableInfo(UserDO.class);
        System.out.println(tableInfo);
        SqlGenerator4Mysql<UserDO> gen = new SqlGenerator4Mysql<>(UserDO.class);
        List<SqlPrepared> sql = gen.createTableSql();
        sql.forEach(System.out::println);
    }

    @Test
    public void illegalKey() {
        boolean t = false;
        try{
            TableInfo tableInfo = TableUtil.tableInfo(IllegalKey.class);
        }catch (IllegalTypeException e){
            e.printStackTrace();
            t  = true;
        }

        Assert.isTrue(t,"catch exception");
    }
}
