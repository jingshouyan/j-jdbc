package com.github.jingshouyan.jdbc.starter.help;

import com.github.jingshouyan.jdbc.starter.dao.IdDao;
import com.github.jingshouyan.jdbc.starter.entity.IdDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author jingshouyan
 * #date 2019/1/11 19:22
 */
@Component
public class IdHelper {

    public static final long STEP = 1 << 10;

    private static final long INIT_ID = STEP << 4;

    private static final long DB_STEP = STEP << 2;

    @Autowired
    private IdDao idDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public long get(String idType) {
        long id = INIT_ID;
        Optional<IdDO> idBeanOptional = idDao.find(idType);
        if (idBeanOptional.isPresent()) {
            IdDO idBean = idBeanOptional.get();
            id = idBean.getSeed();
            idBean.setSeed(id + DB_STEP);
            idDao.update(idBean);
        } else {
            IdDO idBean = new IdDO();
            idBean.setIdType(idType);
            idBean.setSeed(INIT_ID + DB_STEP);
            idDao.insert(idBean);
        }
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Throwable.class)
    public void update(String idType, long old) {
        IdDO idBean = new IdDO();
        idBean.setIdType(idType);
        idBean.setSeed(old + DB_STEP);
        idDao.update(idBean);
    }

}
