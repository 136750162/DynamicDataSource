package com.evan.dynamicdatasource.aop;

import com.evan.dynamicdatasource.config.DynamicDataSourceRouting;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * &#064;Description
 * &#064;Author Evan
 * &#064;Date 2022/11/14 15:58
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

    @Around("@annotation(com.evan.dynamicdatasource.aop.TargetDataSource)")
    public Object setDataSource(ProceedingJoinPoint point){
        String targetDataSourceGroupKey = null;
        try {
            MethodSignature signature = (MethodSignature)point.getSignature();
            Method method = signature.getMethod();
            TargetDataSource targetDataSource = method.getAnnotation(TargetDataSource.class);
            if (targetDataSource != null){
                targetDataSourceGroupKey = targetDataSource.value();
                log.info("当前现成为：{}， 请求方法为：{}， 当前的数据源分组Key为：{}", Thread.currentThread().getName(), String.join("-->", signature.getClass().getName(), signature.getName()), targetDataSourceGroupKey);
                DynamicDataSourceRouting.setDataSourceGroupKey(targetDataSourceGroupKey);
            }
            return point.proceed();
        } catch (Throwable e) {
            log.error("切点切入方法异常，错误的信息为：{}",  e.toString());
            throw new RuntimeException(e);
        }finally {
            DynamicDataSourceRouting.clearDataSource();
            log.info("当前线程为：{}， 开始清理数据源分组Key-->{}", Thread.currentThread().getName(), targetDataSourceGroupKey);
        }
    }


}
