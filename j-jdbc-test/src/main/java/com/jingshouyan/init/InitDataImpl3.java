package com.jingshouyan.init;

import com.github.jingshouyan.jdbc.data.init.action.VersionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2020/4/15 9:37
 */
@Component
@Slf4j
public class InitDataImpl3 implements VersionHandler {

    @Override
    public String version() {
        return "1.3";
    }

    @Override
    public void action() {
        log.info(version());
    }
}
