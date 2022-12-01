package com.evan.dynamicdatasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 *
 * 动态数据源的一个路由选择类，基于Spring 的动态数据源路由去实现
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/14 13:50
 */
public class DynamicDataSourceRouting extends AbstractRoutingDataSource {

    /**
     * ThreadLocal 用于提供线程局部变量，在多线程环境可以保证各个线程里的变量独立于其它线程里的变量。
     * 也就是说 ThreadLocal 可以为每个线程创建一个【单独的变量副本】，相当于线程的 private static 类型变量。
     */
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<>();

    /**
     * 获取数据源对象
     * @return 返回指定的数据源对象
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceGroupKey();
    }

    /**
     * 设置当前线程所持有的数据源分组Key
     * @param dataSourceGroupKey 数据源分组key
     */
    public static void setDataSourceGroupKey(String dataSourceGroupKey) {
        CONTEXT_HOLDER.set(dataSourceGroupKey);
    }

    /**
     * 获取当前现成持有的一个数据源分组Key
     * @return 获取当前现成持有的一个数据源分组Key
     */
    public static String getDataSourceGroupKey() {
        return CONTEXT_HOLDER.get();
    }

    /**
     * 清除当前线程所持有的数据源分组Key
     */
    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
