package com.github.jingshouyan.jdbc.data.init;

import com.github.jingshouyan.jdbc.comm.util.VersionUtil;
import com.github.jingshouyan.jdbc.data.init.dao.DataInitVersionDao;
import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author jingshouyan
 * #date 2020/4/14 11:05
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DataInitProperties.class)
@ComponentScan()
@Order(Ordered.LOWEST_PRECEDENCE)
public class DataInitAutoConfiguration implements ApplicationRunner {

    @Resource
    private DataInitProperties properties;

    @Autowired
    private DataInitVersionDao versionDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<DataInitVersion> optVersion = versionDao.latestVersion();
        List<String> versions = properties.getVersions();
        if (optVersion.isPresent()) {
            String version = optVersion.get().getVersion();
            versions.stream()
                    .filter(v -> isNew(v, version))
                    .forEach(v -> {
                        DataInitVersion dataInitVersion = new DataInitVersion();
                        dataInitVersion.setVersion(v);
                        versionDao.insert(dataInitVersion);


                    });
        }
    }

    private boolean isNew(String version, String target) {
        return VersionUtil.compareVersion(version, target) > 0;
    }

}
