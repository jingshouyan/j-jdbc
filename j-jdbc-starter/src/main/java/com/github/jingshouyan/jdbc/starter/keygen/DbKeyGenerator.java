package com.github.jingshouyan.jdbc.starter.keygen;

import com.github.jingshouyan.jdbc.core.keygen.KeyGenerator;
import com.github.jingshouyan.jdbc.starter.bean.IdBean;
import com.github.jingshouyan.jdbc.starter.dao.IdDao;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * @author jingshouyan
 * 11/29/18 4:36 PM
 */
@Component
public class DbKeyGenerator implements KeyGenerator {

    private static final long STEP = 1000L;

    private static final long INIT_ID = 10000L;

    private static final Map<String,Long> MAP = Maps.newConcurrentMap();

    @Autowired
    private IdDao idDao;

    @Override
    public long generateKey(String type) {
        synchronized (type.intern()){
            Long id = MAP.get(type);
            if(null == id){
                Optional<IdBean> idBeanOptional = idDao.find(type);
                if(idBeanOptional.isPresent()){
                    IdBean idBean = idBeanOptional.get();
                    id = idBean.getSeed();
                    idBean.setSeed(id + STEP*2);
                    idDao.update(idBean);
                }else{
                    id = INIT_ID;
                    IdBean idBean = new IdBean();
                    idBean.setIdType(type);
                    idBean.setSeed(id + STEP*2);
                    idDao.insert(idBean);
                }
            }
            id++;
            MAP.put(type,id);
            if(id % STEP == 0){
                IdBean idBean = new IdBean();
                idBean.setIdType(type);
                idBean.setSeed(id + STEP*2);
                idDao.update(idBean);
            }
            return id;
        }
    }
}
