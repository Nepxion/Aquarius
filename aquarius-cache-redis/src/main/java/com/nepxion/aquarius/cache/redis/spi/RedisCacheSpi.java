package com.nepxion.aquarius.cache.redis.spi;

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
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.cache.spi.CacheSpi;

public class RedisCacheSpi implements CacheSpi {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheSpi.class);

    private RedissonClient redisson;

    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Object invokeCacheable(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        System.out.println("1");
        return null;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String value, String key, boolean allEntries, boolean beforeInvocation) throws Throwable {
        System.out.println("2");
        return null;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        System.out.println("3");
        return null;
    }
}