package com.evan.dynamicdatasource.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 设置数据源的分组名
 *
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/14 15:55
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {

    /**
     * 需要指定的数据源分组Key
     * @return
     */
    String value();
}
