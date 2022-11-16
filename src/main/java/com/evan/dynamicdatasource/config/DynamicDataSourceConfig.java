package com.evan.dynamicdatasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.StringUtils;
import com.evan.dynamicdatasource.aop.DynamicDataSourceAspect;
import com.evan.dynamicdatasource.config.druid.DruidDynamicDataSourceConfig;
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
import java.util.*;

/**
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/14 13:42
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 11)
@SuppressWarnings(value = {"rawtypes", "unchecked"})
public  class  DynamicDataSourceConfig{
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceConfig.class);

    private final static Map<String, Map<String, String>> PROPERTIES_MAP = new HashMap<>();

    /**
     * 默认的分组key 如果此值不配置则取当前分组第一个数据源
     */
    @Value("${evan.dynamic.dataSource.defaultDataSourceKey:}")
    private String defaultDataSourceKey;

    /**
     * 配置文件地址，默认为当前项目 相对路径下的 datasource.properties 文件
     */
    @Value("${evan.dynamic.dataSource.propertiesFilePath: classpath:datasource.properties}")
    private String propertiesFilePath;

    /**
     * 初始化记载数据源配置数据于 #PROPERTIES_MAP缓存中
     */
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
                Map<String, String> propertiesMap = DynamicDataSourceConfig.PROPERTIES_MAP.get(groupKey);
                if (propertiesMap != null) {
                    propertiesMap.put(valueKey, value);
                }else{
                    HashMap<String, String> grouperMap = new HashMap<>();
                    grouperMap.put(valueKey, value);
                    DynamicDataSourceConfig.PROPERTIES_MAP.put(groupKey, grouperMap);
                }
            }
            // 如果没有配置则默认取第一个
            if (StringUtils.isEmpty(defaultDataSourceKey)){
                defaultDataSourceKey = PROPERTIES_MAP.keySet().stream().findFirst().get();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource(DataSourceConfig druidDataSourceConfig){
        Map<Object, Object> targetDataSource = new HashMap<>();
        for (String groupKey : PROPERTIES_MAP.keySet()) {
            targetDataSource.put(groupKey, createDataSource(PROPERTIES_MAP.get(groupKey), groupKey, druidDataSourceConfig));
        }
        DynamicDataSourceRouting dataSource = new DynamicDataSourceRouting();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(targetDataSource.get(defaultDataSourceKey));
        return dataSource;
    }

    /**
     * 初始化当前的切面入Spring容器中
     * @return 返回当前@TargetDataSource注解切面类
     */
    @Bean
    public DynamicDataSourceAspect dynamicDataSourceAspect(){
        return new DynamicDataSourceAspect();
    }

    @Bean
    public DataSourceConfig<DruidDataSource> druidDataSourceConfig(){
        return new DruidDynamicDataSourceConfig();
    }

    /**
     * 创建数据源对象
     *
     * @param propertiesMap         数据源配置属性
     * @param currentGroupKey       当前数据源的分组Key
     * @param config 配置对象
     * @return 返回创建好的了数据源对象
     */
    private  DataSource createDataSource(Map<String, String> propertiesMap, String currentGroupKey, DataSourceConfig config){
        DataSource dataSource = config.createDataSource(propertiesMap, currentGroupKey);
        config.setDefaultConfig(dataSource, propertiesMap, currentGroupKey);
        config.setOtherConfig(dataSource, propertiesMap, currentGroupKey);
        return dataSource;
    }
}
