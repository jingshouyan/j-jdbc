package com.github.jingshouyan.jdbc.starter.aop;


import com.github.jingshouyan.jdbc.starter.JdbcProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.tree.RowMapper;
import java.util.Collection;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 */
@Component
@Aspect
@Slf4j(topic = "J-JDBC")
public class Log4Sql {

    @Resource
    private JdbcProperties jdbcProperties;

    @Pointcut("bean(*JdbcTemplate)")
    public void aspect() {
    }

    @Around("aspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if(!jdbcProperties.isShowSql()){
            return joinPoint.proceed();
        }
        long start = System.currentTimeMillis();
        try {
            Object[] args = joinPoint.getArgs();
            log.debug("sql execution starting");
            for (int i = 0; i < args.length; i++) {
                if(args[i] instanceof RowMapper){
                    continue;
                }
                log.debug("arg.{}===>{}", i, args[i]);
            }
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            int fetch = 0;
            if (result instanceof Collection) {
                fetch = ((Collection) result).size();
            } else if(result instanceof Number) {
                fetch = ((Number) result).intValue();
            } else if(result instanceof int[]) {
                fetch = IntStream.of((int[])result).sum();
            }
            log.debug("sql execution end. use time : {}ms, fetch : {}, result: {}", (end - start), fetch, result);
            return result;
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            if (log.isErrorEnabled()) {
                log.error("call {} error. use time : {}ms ,message: {}", joinPoint.toShortString(), (end - start), e.getMessage());
            }
            throw e;
        }

    }
}
