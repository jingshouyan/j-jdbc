package com.jingshouyan.init;

import com.github.jingshouyan.jdbc.data.init.action.VersionHandler;
import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;
import org.springframework.stereotype.Component;

/**
 * @author jingshouyan
 * #date 2020/4/15 9:37
 */
@Component
public class InitDataImpl implements VersionHandler {

    @Override
    public String version() {
        return "1.2";
    }

    @Override
    public void action() {
        throw new RuntimeException("111");
    }
}
