package com.github.jingshouyan.jdbc.core.keygen;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author jingshouyan
 * 11/29/18 10:49 AM
 */
@Slf4j
public class DefaultKeyGenerator implements KeyGenerator {
    public static final long EPOCH;

    private static final long SEQUENCE_BITS = 12L;

    private static final long WORKER_ID_BITS = 10L;

    private static final long SEQUENCE_MASK = (1 << SEQUENCE_BITS) - 1;

    private static final long WORKER_ID_LEFT_SHIFT_BITS = SEQUENCE_BITS;

    private static final long TIMESTAMP_LEFT_SHIFT_BITS = WORKER_ID_LEFT_SHIFT_BITS + WORKER_ID_BITS;

    private static final long WORKER_ID_MAX_VALUE = 1L << WORKER_ID_BITS;

    private static final Random RANDOM = new Random();

    private static final int MAX_INIT_SEQ = 10;

    private static final DefaultKeyGenerator INSTANCE = new DefaultKeyGenerator();

    public static DefaultKeyGenerator getInstance() {
        return INSTANCE;
    }

    private static long workerId;

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        EPOCH = calendar.getTimeInMillis();
    }


    private long sequence;


    private long lastTime;

    /**
     * 设置工作进程Id.
     *
     * @param workerId 工作进程Id
     */
    public static void setWorkerId(final long workerId) {
        Preconditions.checkArgument(workerId >= 0L && workerId < WORKER_ID_MAX_VALUE);
        DefaultKeyGenerator.workerId = workerId;
    }

    /**
     * 生成Id.
     *
     * @param type key type
     * @return 返回@{@link Long}类型的Id
     */
    @Override
    public long generateKey(String type) {
        synchronized (type.intern()) {
            long currentMillis = currentTimeMillis();
            Preconditions.checkState(lastTime <= currentMillis, "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds", lastTime, currentMillis);
            //sequence 保持 连续 避免 一毫秒内第一个总是取到 0
            if (lastTime == currentMillis) {
                if (++sequence > SEQUENCE_MASK) {
                    currentMillis = waitUntilNextTime(currentMillis);
                    sequence = RANDOM.nextInt(MAX_INIT_SEQ);
                }
            } else {
                sequence = RANDOM.nextInt(MAX_INIT_SEQ);
            }
            lastTime = currentMillis;
            long key = ((currentMillis - EPOCH) << TIMESTAMP_LEFT_SHIFT_BITS) | (workerId << WORKER_ID_LEFT_SHIFT_BITS) | sequence;
            if (log.isDebugEnabled()) {
                log.debug("{};{}-{}-{}", key, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(lastTime)), workerId, sequence);
            }
            return key;
        }

    }

    private long waitUntilNextTime(final long lastTime) {
        long time = currentTimeMillis();
        while (time <= lastTime) {
            time = currentTimeMillis();
        }
        return time;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
