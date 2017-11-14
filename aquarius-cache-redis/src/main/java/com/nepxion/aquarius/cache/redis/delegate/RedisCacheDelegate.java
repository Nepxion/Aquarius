package com.nepxion.aquarius.cache.redis.delegate;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.delegate.CacheDelegate;
import com.nepxion.aquarius.cache.redis.entity.RedisCacheEntity;

@Component("RedisCacheDelegate")
public class RedisCacheDelegate implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegate.class);

    @Autowired
    @Qualifier("aquariusRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisCacheEntity redisCacheEntity;

    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String getPrefix() {
        return redisCacheEntity.getPrefix();
    }

    @Override
    public Object invokeCacheable(MethodInvocation invocation, String key, long expire) throws Throwable {
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 空值不缓存
        Object object = null;
        try {
            object = valueOperations.get(key);
        } catch (Exception e) {
            LOG.warn("Redis exception occurs while getting data", e);
        }

        LOG.info("Before invocation, key={}, cache={} in Redis", key, object);

        if (object != null) {
            return object;
        }

        object = invocation.proceed();

        if (object != null) {
            try {
                if (expire == -1) {
                    valueOperations.set(key, object);
                } else {
                    valueOperations.set(key, object, expire, TimeUnit.MILLISECONDS);
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while setting data", e);
            }

            LOG.info("After invocation, key={}, cache={} in Redis", key, object);
        }

        return object;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String key, boolean allEntries, boolean beforeInvocation) throws Throwable {
        System.out.println("2");
        return null;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String key, long expire) throws Throwable {
        System.out.println("3");
        return null;
    }
}