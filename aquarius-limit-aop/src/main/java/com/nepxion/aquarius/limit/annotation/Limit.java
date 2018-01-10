package com.nepxion.aquarius.limit.annotation;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limit {
    /**
     * 资源的名字
     * @return String
     */
    String name() default "";

    /**
     * 资源的key
     * @return String
     */
    String key() default "";

    /** 
     * 给定的时间段
     * 单位秒
     * @return int
     */
    int limitPeriod();

    /**
     * 最多的访问限制次数
     * @return int
     */
    int limitCount();
}