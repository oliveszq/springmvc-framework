package com.example.framework.annotation;

import java.lang.annotation.*;

//该注解是使用在方法上
@Target({ElementType.METHOD})
//指定该注解在运行时仍然可用
@Retention(RetentionPolicy.RUNTIME)
//指定该注解会包含在 Java 文档中
@Documented
/**
 * 该注解类用来限制时间段和访问次数
 */
public @interface AccessLimit {
    int seconds();

    int maxCount();
}
