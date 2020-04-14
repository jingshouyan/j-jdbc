package com.github.jingshouyan.jdbc.data.init.action;

import com.github.jingshouyan.jdbc.data.init.entity.DataInitVersion;

/**
 * @author jingshouyan
 * #date 2020/4/14 18:56
 */
public interface InitData extends Comparable<InitData> {

    /**
     * 执行该版本的时间
     * @param version 版本信息
     */
    void action(DataInitVersion version);

    /**
     * 排序
     * @return 排序
     */
    default int order() {
        return 0;
    }

    /**
     * 比较
     * @param o 另一个
     * @return 大小
     */
    @Override
    default int compareTo(InitData o) {
        return Integer.compare(this.order(),o.order());
    }
}
