package com.nepxion.aquarius.cache.spi;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;

public interface CacheSpi {
    // 初始锁上下文
    void initialize();

    // 销毁锁上下文
    void destroy();

    // 方法调用Cacheable
    Object invokeCacheable(MethodInvocation invocation, String value, String key, long expire) throws Throwable;

    // 方法调用CacheEvict
    Object invokeCacheEvict(MethodInvocation invocation, String value, String key, boolean allEntries, boolean beforeInvocation) throws Throwable;

    // 方法调用CachePut
    Object invokeCachePut(MethodInvocation invocation, String value, String key, long expire) throws Throwable;
}