package com.github.jingshouyan.jdbc.comm.bean;

import com.github.jingshouyan.jdbc.comm.Constant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Page
 *
 * @author jingshouyan
 * #date 2018/4/14 17:25
 */
@Data
public class Page<T> {
    private int page = Constant.PAGE_DEFAULT;
    private int pageSize = Constant.PAGE_SIZE_DEFAULT;
    private int totalPage;
    private int totalCount;
    private List<T> list;

    private List<OrderBy> orderBies = new ArrayList<>();


    public void totalCount(int totalCount) {
        int tp = totalCount / pageSize;
        int y = totalCount % pageSize;
        if (0 != y) {
            tp++;
        }
        setTotalPage(tp);
        this.totalCount = totalCount;
    }

    public void addOrderBy(OrderBy orderBy) {
        orderBies.add(orderBy);
    }

    public void addOrderBy(String key) {
        addOrderBy(key, true);
    }

    public void addOrderBy(String key, boolean asc) {
        addOrderBy(new OrderBy(key, asc));
    }

}
