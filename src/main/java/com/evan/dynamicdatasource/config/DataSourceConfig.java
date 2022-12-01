package com.evan.dynamicdatasource.config;
import com.evan.dynamicdatasource.config.druid.DruidDynamicDataSourceConfig;
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

    /**
     * 创建数据源对象 默认使用 @see DruidDynamicDataSourceConfig
     * @see DruidDynamicDataSourceConfig
     * @param propertiesMap 配置属性
     * @param currentGroupKey 当前分组Key
     * @return 返回创建好的数据源对象
     */
    T createDataSource(Map<String, String> propertiesMap, String currentGroupKey);

    /**
     * 设置默认的一些属性
     * @param dataSource 数据源对象
     * @param propertiesMap 属性配置
     * @param currentGroupKey 当前数据源对象的分组Key
     */
    void setDefaultConfig(T dataSource, Map<String, String> propertiesMap, String currentGroupKey);

    /**
     * 设置数据源其他的一些属性配置
     * @param dataSource 数据源对象
     * @param propertiesMap 属性配置
     * @param currentGroupKey 当前数据源对象的分组Key
     */
    default void setOtherConfig(T dataSource, Map<String, String> propertiesMap, String currentGroupKey){

    }
}
