package com.evan.dynamicdatasource.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
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

    @Override
    protected Object determineCurrentLookupKey() {
        return getDataSourceGroupKey();
    }

    public static void setDataSourceGroupKey(String dataSource) {
        CONTEXT_HOLDER.set(dataSource);
    }

    public static String getDataSourceGroupKey() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearDataSource() {
        CONTEXT_HOLDER.remove();
    }
}
