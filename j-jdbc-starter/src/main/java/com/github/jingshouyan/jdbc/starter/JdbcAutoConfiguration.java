package com.github.jingshouyan.jdbc.starter;

import com.github.jingshouyan.jdbc.core.dao.BaseDao;
import com.github.jingshouyan.jdbc.core.keygen.KeyGeneratorProvider;
import com.github.jingshouyan.jdbc.starter.keygen.DbKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author jingshouyan
 * 11/29/18 4:15 PM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(JdbcProperties.class)
@ComponentScan()
@Order(10)
public class JdbcAutoConfiguration implements ApplicationRunner {

    public static final int CREATE_TABLE = 1;

    public static final int DROP_CREATE_TABLE = 2;

    public static final int UPDATE_TABLE = 3;

    @Resource
    private JdbcProperties properties;

    @Autowired
    private DbKeyGenerator dbKeyGenerator;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public void run(ApplicationArguments args) {
        Map<String,BaseDao> map = ctx.getBeansOfType(BaseDao.class);
        for (BaseDao dao: map.values()) {
            try{
                switch (properties.getTableInit()){
                    case CREATE_TABLE:
                        createTable(dao);
                        break;
                    case DROP_CREATE_TABLE:
                        dropCreateTable(dao);
                        break;
                    case UPDATE_TABLE:
                        updateTable(dao);
                        break;
                    default:
                }

            }catch (Exception e){
                log.error("init table error",e);
            }
        }


        KeyGeneratorProvider.setKeyGenerator(dbKeyGenerator);
    }

    private void createTable(BaseDao dao){
        if(!dao.existTable()) {
            dao.createTable();
        }
    }

    private void dropCreateTable(BaseDao dao){
        if(dao.existTable()){
            dao.dropTable();
        }
        dao.createTable();
    }

    private void updateTable(BaseDao dao){
        if(dao.existTable()){
            dao.updateTable();
        }else {
            dao.createTable();
        }

    }
}
