package com.nepxion.aquarius.cache;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.List;

import org.aopalliance.intercept.MethodInvocation;

public interface CacheDelegate {
    // 新增缓存
    Object invokeCacheable(MethodInvocation invocation, List<String> keys, long expire) throws Throwable;

    // 更新缓存
    Object invokeCachePut(MethodInvocation invocation, List<String> keys, long expire) throws Throwable;

    // 清除缓存
    Object invokeCacheEvict(MethodInvocation invocation, List<String> keys, String name, boolean allEntries, boolean beforeInvocation) throws Throwable;
}