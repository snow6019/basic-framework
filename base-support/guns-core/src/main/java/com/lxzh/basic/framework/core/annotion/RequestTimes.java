package com.lxzh.basic.framework.core.annotion;

import java.lang.annotation.*;

/**
 * 请求次数注解，用于单位时间内请求次数限制
 * 使用方式： 请求接口上加上注解 @RequestTimes(count = 3, time = 10000)，表示10s内该接口限制请求3次
 *
 * @author wr
 * @date 2022-08-10
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestTimes {

    /**
     * 单位时间允许访问次数 - - -默认值是2
     */
    int count() default 2;

    /**
     * 设置单位时间为1分钟 - - - 默认值是1分钟
     */
    long time() default 60 * 1000;
}
