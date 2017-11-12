package com.nepxion.aquarius.lock.annotation;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadLock {
    /**
     * 锁的key
     */
    String key();

    /** 
     * 持锁时间，持锁超过此时间则自动丢弃锁
     * 单位毫秒，默认5秒
     */
    long leaseTime() default 5 * 1000;

    /**
     * 没有获取到锁时，等待时间（单位毫秒，默认5秒）
     * 单位毫秒，默认60秒
     */
    long waitTime() default 60 * 1000;

    /**
     * 是否采用锁的异步执行方式
     */
    boolean async() default false;

    /**
     * 是否采用公平锁
     */
    boolean fair() default false;
}