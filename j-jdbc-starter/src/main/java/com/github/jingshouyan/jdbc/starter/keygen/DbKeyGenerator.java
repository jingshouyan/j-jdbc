package com.github.jingshouyan.jdbc.starter.keygen;

import com.github.jingshouyan.jdbc.core.keygen.KeyGenerator;
import com.github.jingshouyan.jdbc.starter.entity.IdDO;
import com.github.jingshouyan.jdbc.starter.dao.IdDao;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author jingshouyan
 * 11/29/18 4:36 PM
 */
@Component
public class DbKeyGenerator implements KeyGenerator {

    private static final long STEP = 1000L;

    private static final long INIT_ID = 10000L;

    private static final Map<String, LongAdder> MAP = Maps.newConcurrentMap();

    @Autowired
    private IdDao idDao;

    @Override
    public long generateKey(String type) {
        LongAdder longAdder = MAP.computeIfAbsent(type, idType -> {
            LongAdder ladd = new LongAdder();
            Optional<IdDO> idBeanOptional = idDao.find(idType);
            if (idBeanOptional.isPresent()) {
                IdDO idBean = idBeanOptional.get();
                ladd.add(idBean.getSeed());
                idBean.setSeed(newSeed(ladd));
                idDao.update(idBean);
            } else {
                ladd.add(INIT_ID);
                IdDO idBean = new IdDO();
                idBean.setIdType(idType);
                idBean.setSeed(newSeed(ladd));
                idDao.insert(idBean);
            }
            return ladd;
        });

        longAdder.increment();

        if(longAdder.longValue() % STEP == 0){
            IdDO idBean = new IdDO();
            idBean.setIdType(type);
            idBean.setSeed(newSeed(longAdder));
            idDao.update(idBean);
        }
        return longAdder.longValue();
    }

    private long newSeed(LongAdder longAdder){
        return longAdder.longValue() + STEP * 2;
    }
}
