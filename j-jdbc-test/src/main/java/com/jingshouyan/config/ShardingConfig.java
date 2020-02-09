package com.jingshouyan.config;

import com.github.jingshouyan.jdbc.sharding.algorithm.ModPreciseShardingAlgorithm;
import com.github.jingshouyan.jdbc.sharding.util.StringUtil;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.core.constant.properties.ShardingPropertiesConstant;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author jingshouyan
 * #date 2020/2/8 12:13
 */
@Configuration
public class ShardingConfig {

    public static final String URL = "jdbc:mysql://127.0.0.1:3306/DB_TEST_%02d?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "abcd1234";
    public static final String DRIVER = "com.mysql.jdbc.Driver";
    public static final int DS_SHARD = 1;
    public static final int TABLE_SHARD = 10;
    public static final String DS_LOGIC_NAME = "ds";

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }


    @SneakyThrows
    @Bean
    public DataSource dataSource() {
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        Map<String, String> shardingConfig = shardingConfig();
        for (Map.Entry<String, String> entry : shardingConfig.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String actualNodes = actualDataNodes(key);
            TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration(key,actualNodes);
            ShardingStrategyConfiguration shardingStrategyConfiguration = new StandardShardingStrategyConfiguration(
                    value, new ModPreciseShardingAlgorithm()
            );
            tableRuleConfiguration.setTableShardingStrategyConfig(shardingStrategyConfiguration);
            if(DS_SHARD > 1){
                tableRuleConfiguration.setDatabaseShardingStrategyConfig(shardingStrategyConfiguration);
            }
            shardingRuleConfiguration.getTableRuleConfigs().add(tableRuleConfiguration);
        }
        Map<String, DataSource> dataSourceMap = dataSourceMap();
        String defaultDataSourceName = dataSourceMap.keySet().stream().findFirst().get();
        shardingRuleConfiguration.setDefaultDataSourceName(defaultDataSourceName);
        Properties props = new Properties();
        props.setProperty(ShardingPropertiesConstant.SQL_SHOW.getKey(),"true");
        return ShardingDataSourceFactory.createDataSource(dataSourceMap,shardingRuleConfiguration,props);
    }

    private Map<String, String> shardingConfig() {
        Map<String, String> map = Maps.newHashMap();

        map.put("DEMO_USER", "id");

        return map;
    }

    public String actualDataNodes(String logicTableName) {
        return IntStream.range(0, DS_SHARD)
                .mapToObj(i -> StringUtil.getActualName(DS_LOGIC_NAME,i))
                .flatMap(ds -> IntStream.range(0,TABLE_SHARD)
                        .mapToObj(i -> ds+"."+StringUtil.getActualName(logicTableName,i))
                )
                .collect(Collectors.joining(","));
    }

    public static void main(String[] args) {
        ShardingConfig shardingConfig = new ShardingConfig();
        String actualDataNodes = shardingConfig.actualDataNodes("USER_DO");
        System.out.println(actualDataNodes);
    }

    private Map<String, DataSource> dataSourceMap() {
        Map<String, DataSource> dataSourceMap = Maps.newLinkedHashMap();
        for (int i = 0; i < DS_SHARD; i++) {
            String dsName = StringUtil.getActualName(DS_LOGIC_NAME, i);
            DataSource ds = initDataSource(i);
            dataSourceMap.put(dsName, ds);
        }
        return dataSourceMap;
    }

    private DataSource initDataSource(int index) {
        HikariConfig config = new HikariConfig();
        String url = String.format(URL, index);
        config.setJdbcUrl(url);
        config.setDriverClassName(DRIVER);
        config.setUsername(USERNAME);
        config.setPassword(PASSWORD);
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        HikariDataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }


}
