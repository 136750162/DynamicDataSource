package com.evan.dynamicdatasource.config;

import javax.sql.DataSource;
import java.util.Map;

/**
 *
 * 数据库配置的顶级配置类，目前默认实现druid数据源配置可根据自己使用动态调整所选数据源配置
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/16 16:29
 */
public interface DataSourceConfig<T extends DataSource> {

    T createDataSource(Map<String, String> propertiesMap, String currentGroupKey);
    void setDefaultConfig(T dataSource, Map<String, String> propertiesMap, String currentGroupKey);

    default void setOtherConfig(T dataSource, Map<String, String> propertiesMap, String currentGroupKey){

    }
}
