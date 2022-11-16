package com.evan.dynamicdatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.StringUtils;
import com.evan.dynamicdatasource.aop.DynamicDataSourceAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/14 13:42
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)
//@EnableConfigurationProperties(DynamicDataSourceConfig.class)
public  class  DynamicDataSourceConfig{
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    private final static Map<String, Map<String, String>> PROPERTIES_MAP = new HashMap<>();

    @Value("${spring.dataSource.defaultDataSourceKe:}")
    private String defaultDataSourceKey;

    @Value("${spring.dataSource.propertiesFilePath: classpath:datasource.properties}")
    private String propertiesFilePath;

    @PostConstruct
    public void init(){
        DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
        Properties properties = new Properties();
        try {
            log.info("多数据源开始读取配置文件，当前读取的配置文件位置为：{}", propertiesFilePath);
            Resource resource = defaultResourceLoader.getResource(propertiesFilePath);
            properties.load(resource.getInputStream());
            Set<Map.Entry<Object, Object>> entries = properties.entrySet();
            for (Map.Entry<Object, Object> next : entries) {
                String key = (String) next.getKey();
                String value = (String) next.getValue();
                // 取第一位作为分组Key
                String[] str = key.split("\\.");
                String groupKey = str[0];
                String valueKey = str[str.length - 1];
                // 如果没有配置则默认取第一个
                if (StringUtils.isEmpty(defaultDataSourceKey)){
                    defaultDataSourceKey = groupKey;
                }
                Map<String, String> propertiesMap = DynamicDataSourceConfig.PROPERTIES_MAP.get(groupKey);
                if (propertiesMap != null) {
                    propertiesMap.put(valueKey, value);
                }else{
                    HashMap<String, String> grouperMap = new HashMap<>();
                    grouperMap.put(valueKey, value);
                    DynamicDataSourceConfig.PROPERTIES_MAP.put(groupKey, grouperMap);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource(){
        Map<Object, Object> targetDataSource = new HashMap<>();
        for (String groupKey : PROPERTIES_MAP.keySet()) {
            targetDataSource.put(groupKey, getDataSource(PROPERTIES_MAP.get(groupKey)));
        }
        DynamicDataSourceRouting dataSource = new DynamicDataSourceRouting();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(targetDataSource.get(defaultDataSourceKey));
        return dataSource;
    }

    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect(){
        return new DynamicDataSourceAspect();
    }

    private  DataSource getDataSource(Map<String, String> propertiesMap){
        DruidDataSource dataSource = new DruidDataSource();
        setDefaultProperties(propertiesMap, dataSource);
        setOtherDataSourceProperties(dataSource, propertiesMap);
        return dataSource;
    }

    private void setDefaultProperties(Map<String, String> propertiesMap, DruidDataSource dataSource) {
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
    private Properties getProperties(String connectProperties) {
        connectProperties = connectProperties == null ? "druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000": connectProperties;
        Properties properties = new Properties();
        String[] strings = connectProperties.split(";");
        for (String item : strings) {
            String[] split = item.split("=");
            properties.setProperty(split[0], split[1]);
        }
        return properties;
    }

    protected  void setOtherDataSourceProperties(DataSource dataSource, Map<String, String> propertiesMap){

    };
}
