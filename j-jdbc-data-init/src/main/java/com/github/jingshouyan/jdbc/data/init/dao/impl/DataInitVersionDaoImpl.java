package com.github.jingshouyan.jdbc.data.init.dao.impl;

import com.github.jingshouyan.jdbc.comm.bean.Condition;
import com.github.jingshouyan.jdbc.comm.bean.Page;
import com.github.jingshouyan.jdbc.comm.util.ConditionUtil;
import com.github.jingshouyan.jdbc.core.dao.impl.BaseDaoImpl;
import com.github.jingshouyan.jdbc.data.init.dao.DataInitVersionDao;
import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author jingshouyan
 * #date 2020/4/14 15:15
 */
@Repository
public class DataInitVersionDaoImpl extends BaseDaoImpl<DataInitVersion> implements DataInitVersionDao {

    @Override
    public Optional<DataInitVersion> latestVersion(){
        List<Condition> conditions = ConditionUtil.newInstance()
                .noDeleted()
                .conditions();
        Page<DataInitVersion> page = new Page<>();
        page.setPage(1);
        page.setPageSize(1);
        page.addOrderBy("createdAt",false);
        List<DataInitVersion> versions = this.queryLimit(conditions,page);
        return versions.stream().findFirst();
    }
}
