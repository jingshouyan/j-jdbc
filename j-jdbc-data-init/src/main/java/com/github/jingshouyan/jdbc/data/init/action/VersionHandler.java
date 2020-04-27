package com.github.jingshouyan.jdbc.data.init.action;

import com.github.jingshouyan.jdbc.comm.util.VersionUtil;

/**
 * @author jingshouyan
 * #date 2020/4/14 18:56
 */
public interface VersionHandler extends Comparable<VersionHandler> {

    /**
     * 对应的版本
     *
     * @return 版本
     */
    String version();

    /**
     * 执行该版本的
     */
    void action();

    /**
     * 比较大小
     *
     * @param o other
     * @return 大小
     */
    @Override
    default int compareTo(VersionHandler o) {
        return VersionUtil.compareVersion(this.version(), o.version());
    }


}
