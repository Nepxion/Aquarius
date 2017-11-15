package com.nepxion.aquarius.cache.delegate;

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

public interface CacheDelegate {
    // 初始锁上下文
    void initialize();

    // 销毁锁上下文
    void destroy();

    // 新增缓存
    Object invokeCacheable(MethodInvocation invocation, String key, long expire) throws Throwable;

    // 更新缓存
    Object invokeCachePut(MethodInvocation invocation, String key, long expire) throws Throwable;

    // 清除缓存
    Object invokeCacheEvict(MethodInvocation invocation, String key, String name, boolean allEntries, boolean beforeInvocation) throws Throwable;
}