package com.github.jingshouyan.jdbc.sharding.algorithm;

import com.github.jingshouyan.jdbc.sharding.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

/**
 * @author jingshouyan
 * #date 2020/02/08 09:45
 */
@Slf4j
public class ModPreciseShardingAlgorithm<T extends Comparable<?>> implements PreciseShardingAlgorithm<T> {
    public static final long MIN_SHARD = 100L;

    private boolean endWith(String str, T value, int sharding) {
        long va;
        if(value instanceof Number){
            va = ((Number) value).longValue();
        }else{
            String str2 = String.valueOf(value);
            va = StringUtil.getNumber(str2);
            if(va < MIN_SHARD) {
                va = str2.hashCode();
            }
        }
        va = Math.abs(va);
        String v = StringUtil.getShardingSuffix(va % sharding);
        return str.endsWith(v);
    }

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<T> shardingValue){
        for (String each : availableTargetNames) {
            if (endWith(each, shardingValue.getValue(), availableTargetNames.size())) {
                return each;
            }
        }
        log.error("shard failed! names:{},value:{}", availableTargetNames, shardingValue);
        throw new IllegalArgumentException();
    }
}
