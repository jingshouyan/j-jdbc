package com.github.jingshouyan.jdbc.sharding.util;

import com.github.jingshouyan.jdbc.sharding.algorithm.ModPreciseShardingAlgorithm;
import com.github.jingshouyan.jdbc.sharding.algorithm.ModRangeShardingAlgorithm;
import com.github.jingshouyan.jdbc.sharding.entity.DataSourceInfo;
import com.github.jingshouyan.jdbc.sharding.entity.DatabaseLinkInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.apache.shardingsphere.api.config.masterslave.MasterSlaveRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.jingshouyan.jdbc.sharding.Constant.*;

/**
 * @author jingshouyan
 * #date 2020/2/9 16:19
 */
public class DataSourceUtil {

    private static final String DS = "ds";
    private static final String MASTER = "master";
    private static final String SLAVE = "slave";
    private static final String UL = "_";
    private static final String DOT = ".";

    public static DataSource createDataSource(DataSourceInfo info) {
        switch (info.getType()) {
            case DATA_SOURCE_TYPE_NORMAL:
                return normalDataSource(info);
            case DATA_SOURCE_TYPE_SHARDING:
                return shardingDataSource(info);
            case DATA_SOURCE_TYPE_MASTER_SLAVE:
                return masterSlaveDataSource(info);
            case DATA_SOURCE_TYPE_SHARDING_MASTER_SLAVE:
                return shardingMasterSlaveDataSource(info);
            default:
                throw new IllegalArgumentException("Unsupported dataSource type");
        }
    }

    @SneakyThrows
    private static DataSource shardingMasterSlaveDataSource(DataSourceInfo info) {
        ShardingRuleConfiguration shardingRuleConfiguration = prepareShardingRuleConfiguration(info);
        int dsShard = info.getLinkInfos().size();
        List<MasterSlaveRuleConfiguration> masterSlaveConfigs = Lists.newArrayList();
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        for (int i = 0; i < dsShard; i++) {
            DatabaseLinkInfo masterInfo = info.getLinkInfos().get(i);
            // 数据源逻辑名
            String dsLogic = StringUtil.getActualName(DS, i);
            // master
            String dsMaster = dsLogic + UL + MASTER;
            DataSource master = datasource(masterInfo);
            // 加入 map
            dataSourceMap.put(dsMaster, master);

            List<String> dsSlaves = Lists.newArrayList();
            List<DatabaseLinkInfo> slaveInfos = masterInfo.getSlaves();
            int slaveSize = slaveInfos.size();
            for (int j = 0; j < slaveSize; j++) {
                DatabaseLinkInfo slaveInfo = slaveInfos.get(j);
                // slave
                String dsSlave = StringUtil.getActualName(dsLogic + UL + SLAVE, j);
                dsSlaves.add(dsSlave);
                DataSource slave = datasource(slaveInfo);
                dataSourceMap.put(dsSlave, slave);
            }
            MasterSlaveRuleConfiguration config = new MasterSlaveRuleConfiguration(dsLogic, dsMaster, dsSlaves);
            masterSlaveConfigs.add(config);
        }
        shardingRuleConfiguration.setMasterSlaveRuleConfigs(masterSlaveConfigs);
        Properties props = properties(info);
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, props);
    }

    @SneakyThrows
    private static DataSource shardingDataSource(DataSourceInfo info) {
        ShardingRuleConfiguration shardingRuleConfiguration = prepareShardingRuleConfiguration(info);
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        int dsShard = info.getLinkInfos().size();
        for (int i = 0; i < dsShard; i++) {
            DatabaseLinkInfo linkInfo = info.getLinkInfos().get(i);
            String dsName = StringUtil.getActualName(DS, i);
            DataSource ds = datasource(linkInfo);
            dataSourceMap.put(dsName, ds);
        }
        Properties props = properties(info);
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, props);
    }

    /**
     * 准备 分片规则
     *
     * @param info 连接信息
     * @return 分片基本配置
     */
    private static ShardingRuleConfiguration prepareShardingRuleConfiguration(DataSourceInfo info) {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        int dsShard = info.getLinkInfos().size();
        int tableShard = info.getTableShard();
        ModPreciseShardingAlgorithm preciseShardingAlgorithm = new ModPreciseShardingAlgorithm();
        ModRangeShardingAlgorithm rangeShardingAlgorithm = new ModRangeShardingAlgorithm();
        for (Map.Entry<String, String> entry : info.getRouteMap().entrySet()) {
            String logicTable = entry.getKey();
            String shardingColumn = entry.getValue();
            String actualNodes = actualNodes(logicTable, dsShard, tableShard);
            TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(logicTable, actualNodes);
            ShardingStrategyConfiguration shardingStrategyConfiguration = new StandardShardingStrategyConfiguration(
                    shardingColumn, preciseShardingAlgorithm, rangeShardingAlgorithm);
            tableRuleConfiguration.setTableShardingStrategyConfig(shardingStrategyConfiguration);
            if (dsShard > 1) {
                tableRuleConfiguration.setDatabaseShardingStrategyConfig(shardingStrategyConfiguration);
            }
            shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
        }
        String defaultDataSourceName = StringUtil.getActualName(DS, 0);
        shardingRuleConfiguration.setDefaultDataSourceName(defaultDataSourceName);
        return shardingRuleConfiguration;
    }

    private static String actualNodes(String logicTable, int dsShard, int tableShard) {
        return IntStream.range(0, dsShard)
                .mapToObj(i -> StringUtil.getActualName(DS, i))
                .flatMap(ds -> IntStream.range(0, tableShard)
                        .mapToObj(i -> ds + DOT + StringUtil.getActualName(logicTable, i))
                )
                .collect(Collectors.joining(","));
    }

    @SneakyThrows
    private static DataSource masterSlaveDataSource(DataSourceInfo info) {
        DatabaseLinkInfo linkInfo = info.getLinkInfos().get(0);
        Map<String, DataSource> dataSourceMap = Maps.newHashMap();
        String master = "ds_master";
        List<String> slaves = Lists.newArrayList();
        DataSource dsMaster = datasource(linkInfo);
        dataSourceMap.put(master, dsMaster);
        List<DatabaseLinkInfo> slaveInfos = linkInfo.getSlaves();
        for (int i = 0; i < slaveInfos.size(); i++) {
            DatabaseLinkInfo slaveInfo = slaveInfos.get(i);
            String slave = StringUtil.getActualName("ds_slave", i);
            slaves.add(slave);
            DataSource dsSlave = datasource(slaveInfo);
            dataSourceMap.put(slave, dsSlave);
        }
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration("ds_master_slave", master, slaves);
        Properties properties = properties(info);
        return MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig, properties);
    }

    private static Properties properties(DataSourceInfo info) {
        Properties properties = new Properties();
        properties.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(), String.valueOf(info.isShowLog()));
        return properties;
    }

    private static DataSource normalDataSource(DataSourceInfo info) {
        return datasource(info.getLinkInfos().get(0));
    }

    private static DataSource datasource(DatabaseLinkInfo info) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(info.getUrl());
        config.setDriverClassName(info.getDriver());
        config.setUsername(info.getUsername());
        config.setPassword(info.getPassword());
        config.setMaximumPoolSize(info.getMaxPoolSize());
        config.setMinimumIdle(info.getMinIdle());
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }


}
