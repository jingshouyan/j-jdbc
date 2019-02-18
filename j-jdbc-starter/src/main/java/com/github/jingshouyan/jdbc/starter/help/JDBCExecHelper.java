package com.github.jingshouyan.jdbc.starter.help;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author jingshouyan
 * #date 2019/1/31 12:21
 */
@Component
public class JDBCExecHelper {
    private final int cpuNum = Runtime.getRuntime().availableProcessors();
    private final ExecutorService EXEC = new ThreadPoolExecutor(cpuNum * 4,
            cpuNum * 8, 30L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1024),
            new ThreadFactoryBuilder().setNameFormat("exec-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );



    public void exec(Runnable runnable) {
        EXEC.execute(runnable);
    }
}
