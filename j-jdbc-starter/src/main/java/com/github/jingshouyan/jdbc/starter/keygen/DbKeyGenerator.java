package com.github.jingshouyan.jdbc.starter.keygen;

import com.github.jingshouyan.jdbc.core.keygen.KeyGenerator;
import com.github.jingshouyan.jdbc.starter.help.IdHelper;
import com.github.jingshouyan.jdbc.starter.help.JdbcExecHelper;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author jingshouyan
 * 11/29/18 4:36 PM
 */
@Component
public class DbKeyGenerator implements KeyGenerator {

    private static final Map<String, AtomicLong> MAP = Maps.newConcurrentMap();

    @Autowired
    private IdHelper idHelper;
    @Autowired
    private JdbcExecHelper execHelper;

    @Override
    public long generateKey(String type) {
        AtomicLong longAdder = MAP.get(type);
        if (longAdder == null) {
            longAdder = MAP.computeIfAbsent(type, idType ->
                    new AtomicLong(idHelper.get(idType))
            );
        }
        long result = longAdder.incrementAndGet();
        if (result % IdHelper.STEP == 0) {
            execHelper.exec(() -> idHelper.update(type, result));
        }
        return result;
    }

}
