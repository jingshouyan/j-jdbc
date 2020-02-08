package com.github.jingshouyan.jdbc.sharding.algorithm;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Collection;

/**
 * @author jingshouyan
 * #date 2020/2/8 10:41
 */
public class ModRangeShardingAlgorithm<T extends Comparable<?>> implements RangeShardingAlgorithm<T> {
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<T> rangeShardingValue) {
        Range<T> range = rangeShardingValue.getValueRange();
        Object lowerEndpoint = range.lowerEndpoint();
        if (lowerEndpoint instanceof Number) {
            long lower = ((Number) lowerEndpoint).longValue();
            Object upperEndpoint = range.upperEndpoint();
            long upper = ((Number) upperEndpoint).longValue();
            if (!range.hasLowerBound()) {
                lower = lower + 1;
            }
            if (!range.hasUpperBound()) {
                upper = upper - 1;
            }
            if (upper - lower + 1 >= collection.size()) {
                return collection;
            }
            //TODO: 计算分片
        }
        return collection;
    }
}
