package com.evan.dynamicdatasource.config.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.evan.dynamicdatasource.config.DataSourceConfig;

import java.util.Map;
import java.util.Properties;

/**
 *默认的多数据源的实现，使用Alibab Druid数据源对象
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/16 16:31
 */
public class DruidDynamicDataSourceConfig implements DataSourceConfig<DruidDataSource> {

    /**
     * 默认
     * @param propertiesMap 配置属性
     * @param currentGroupKey 当前分组Key
     * @return DruidS数据源对象
     */
    @Override
    public DruidDataSource createDataSource(Map<String, String> propertiesMap, String currentGroupKey) {
        return new DruidDataSource();
    }

    /**
     * 设置Druid数据源默认的一些配置信息
     * @param dataSource 数据源对象
     * @param propertiesMap 属性配置
     * @param currentGroupKey 当前数据源对象的分组Key
     */
    @Override
    public void setDefaultConfig(DruidDataSource dataSource, Map<String, String> propertiesMap, String currentGroupKey) {
        dataSource.setUrl(propertiesMap.get("url"));
        dataSource.setUsername(propertiesMap.get("username"));
        dataSource.setPassword(propertiesMap.get("password"));
        //借用连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnBorrow(false);
        //归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(false);
        //如果检测失败，则连接将被从池中去除
        dataSource.setTestWhileIdle(true);
        dataSource.setTimeBetweenEvictionRunsMillis(propertiesMap.get("timeBetweenEvictionRunsMillis") != null ? Long.parseLong(propertiesMap.get("timeBetweenEvictionRunsMillis")) : 60000);//1分钟
        // 设置最小活跃时间 默认值为30分钟
        dataSource.setMinEvictableIdleTimeMillis(propertiesMap.get("minEvictableIdleTimeMillis") != null ? Long.parseLong(propertiesMap.get("minEvictableIdleTimeMillis")) : 300000);
        dataSource.setMaxActive(propertiesMap.get("maxActive") != null ?  Integer.parseInt(propertiesMap.get("maxActive")): 20);
        dataSource.setInitialSize(propertiesMap.get("initialSize") != null ?  Integer.parseInt(propertiesMap.get("initialSize")): 5);
        dataSource.setMaxWait(propertiesMap.get("maxWait") != null ? Long.parseLong(propertiesMap.get("maxWait")) : 60000);
        dataSource.setMinIdle(propertiesMap.get("minIdle") != null ?  Integer.parseInt(propertiesMap.get("minIdle")): 1);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setMaxOpenPreparedStatements(propertiesMap.get("maxOpenPreparedStatements") != null ?  Integer.parseInt(propertiesMap.get("maxOpenPreparedStatements")): 20);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(propertiesMap.get("maxPoolPreparedStatementPerConnectionSize") != null ?  Integer.parseInt(propertiesMap.get("maxPoolPreparedStatementPerConnectionSize")): 20);
        dataSource.setConnectProperties(getProperties(propertiesMap.get("connectProperties")));
        dataSource.setDriverClassName(propertiesMap.get("driver-class-name"));
    }

    /**
     * 获取解析ConnectProperties数据配置
     * @param connectProperties 配置连接时的一些配置数据
     * @return 解析后的一个配置信息数据
     */
    protected Properties getProperties(String connectProperties) {
        connectProperties = connectProperties == null ? "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000": connectProperties;
        Properties properties = new Properties();
        String[] strings = connectProperties.split(";");
        for (String item : strings) {
            String[] split = item.split("=");
            properties.setProperty(split[0], split[1]);
        }
        return properties;
    }
}
