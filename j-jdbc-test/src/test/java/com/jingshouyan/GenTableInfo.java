package com.jingshouyan;

import com.github.jingshouyan.jdbc.comm.bean.ColumnInfo;
import com.jingshouyan.bean.ColumnDO;
import com.jingshouyan.bean.TableDO;
import com.jingshouyan.dao.ColumnDao;
import com.jingshouyan.dao.TableDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author jingshouyan
 * #date 2020/4/27 19:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class GenTableInfo {

    @Autowired
    private TableDao tableDao;
    @Autowired
    private ColumnDao columnDao;

    @Test
    public void show(){
        List<TableDO> tables = tableDao.listBySchema("IM_PLATFORM");
        List<ColumnDO> columns = columnDao.listBySchema("IM_PLATFORM");
        tables.forEach(t -> t.setTableComment(t.getTableComment().trim()));
        columns.forEach(c -> c.setColumnComment(c.getColumnComment().trim()));
        Map<String,List<ColumnDO>> map = columns.stream()
                .collect(Collectors.groupingBy(ColumnDO::getTableName));
//        System.out.println(map);
        for (TableDO table : tables) {
            List<ColumnDO> columns2 = map.get(table.getTableName());
            String doc = doc(table,columns2);
            System.out.println(doc);
        }
    }
    public static final String LINK_BREAK = "\r\n";
    private String doc(TableDO table,List<ColumnDO> columns) {
        StringBuilder sb = new StringBuilder();
        String tableName = table.getTableName().replaceAll("_0","");
        sb.append("## ").append(tableName).append("  ").append(table.getTableComment()).append(LINK_BREAK);
        // 数据结构信息
        sb.append("|#|列名|数据类型|备注|").append(LINK_BREAK);
        sb.append("|-|----|-------|----|").append(LINK_BREAK);
        for (int i = 0; i < columns.size(); i++) {
            ColumnDO columnInfo = columns.get(i);
            String columnName = columnInfo.getColumnName();
            String dataType = columnInfo.getColumnType();
            String comment = columnInfo.getColumnComment();
            sb.append("|").append(i + 1)
                    .append("|").append(columnName)
                    .append("|").append(dataType)
                    .append("|").append(comment)
                    .append("|").append(LINK_BREAK);
        }
        return sb.toString();
    }

}
