package com.github.jingshouyan.jdbc.data.init;

import com.github.jingshouyan.jdbc.comm.util.VersionUtil;
import com.github.jingshouyan.jdbc.data.init.action.VersionHandler;
import com.github.jingshouyan.jdbc.data.init.dao.DataInitVersionDao;
import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:05
 */
@Slf4j
@Configuration
@ComponentScan()
@Order(Ordered.LOWEST_PRECEDENCE)
public class DataInitAutoConfiguration implements ApplicationRunner {


    @Autowired
    private DataInitVersionDao versionDao;

    @Autowired
    private ApplicationContext ctx;

    @Override
    public void run(ApplicationArguments args) {

        Map<String, VersionHandler> map = ctx.getBeansOfType(VersionHandler.class);
        List<VersionHandler> handlers = Lists.newArrayList(map.values());
        Collections.sort(handlers);
        String latest = versionDao.latestVersion().map(DataInitVersion::getVersion).orElse("");
        handlers.stream().filter(h -> isNew(h.version(), latest))
                .forEach(h -> {
                    DataInitVersion version = new DataInitVersion();
                    version.setVersion(h.version());
                    version.setClazz(h.getClass().getName());
                    versionDao.insert(version);
                    try {
                        h.action();
                        version.setSuccess(true);
                        versionDao.update(version);
                    } catch (Throwable e) {
                        version.setSuccess(false);
                        version.setMessage(e.getMessage());
                        version.forDelete();
                        versionDao.update(version);
                        throw e;
                    }
                });
    }

    private boolean isNew(String version, String target) {
        return VersionUtil.compareVersion(version, target) > 0;
    }

}
