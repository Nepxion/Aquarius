package com.nepxion.aquarius.lock.annotation;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
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
public @interface WriteLock {
    /**
     * 锁的名字
     * @return String
     */
    String name() default "";

    /**
     * 锁的key
     * @return String
     */
    String key() default "";

    /** 
     * 持锁时间，持锁超过此时间则自动丢弃锁
     * 单位毫秒，默认5秒
     * @return long
     */
    long leaseTime() default 5000L;

    /**
     * 没有获取到锁时，等待时间
     * 单位毫秒，默认60秒
     * @return long
     */
    long waitTime() default 60000L;

    /**
     * 是否采用锁的异步执行方式(异步拿锁，同步阻塞)
     * @return boolean
     */
    boolean async() default false;

    /**
     * 是否采用公平锁
     * @return boolean
     */
    boolean fair() default false;
}