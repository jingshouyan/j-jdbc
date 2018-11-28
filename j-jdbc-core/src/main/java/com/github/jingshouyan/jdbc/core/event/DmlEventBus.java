package com.github.jingshouyan.jdbc.core.event;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 数据 DML 事件总线
 * @author jingshouyan
 * @date 2018/4/14 17:25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DmlEventBus {
    private static final Map<DmlType,EventBus> BUS_MAP = Maps.newConcurrentMap();

    public static EventBus getEventBus(DmlType dmlType) {
        return BUS_MAP.computeIfAbsent(dmlType,(key)->new EventBus());
    }
    public static void onCreate(Object t){
        if(isCreateOn()){
            getEventBus(DmlType.CREATE).post(t);
        }
    }

    public static void onUpdate(Object t){
        if(isUpdateOn()){
            getEventBus(DmlType.UPDATE).post(t);
        }
    }

    public static void onDelete(Object t){
        if(isDeleteOn()){
            getEventBus(DmlType.DELETE).post(t);
        }
    }
    public static boolean isCreateOn() {
        return BUS_MAP.containsKey(DmlType.CREATE);
    }
    public static boolean isUpdateOn() {
        return BUS_MAP.containsKey(DmlType.UPDATE);
    }
    public static boolean isDeleteOn() {
        return BUS_MAP.containsKey(DmlType.DELETE);
    }




}
