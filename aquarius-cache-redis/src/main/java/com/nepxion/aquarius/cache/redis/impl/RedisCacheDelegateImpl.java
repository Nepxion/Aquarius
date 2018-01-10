package com.nepxion.aquarius.cache.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.util.KeyUtil;

public class RedisCacheDelegateImpl implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegateImpl.class);

    @Autowired
    private RedisHandler redisHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public Object invokeCacheable(MethodInvocation invocation, String key, long expire) throws Throwable {
        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 空值不缓存
        Object object = null;
        try {
            object = valueOperations.get(key);
        } catch (Exception e) {
            LOG.warn("Redis exception occurs while getting data", e);
        }

        if (frequentLogPrint) {
            LOG.info("Before invocation, Cacheable key={}, cache={} in Redis", key, object);
        }

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

            if (frequentLogPrint) {
                LOG.info("After invocation, Cacheable key={}, cache={} in Redis", key, object);
            }
        }

        return object;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String key, long expire) throws Throwable {
        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        // 空值不缓存
        Object object = invocation.proceed();
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

            if (frequentLogPrint) {
                LOG.info("After invocation, CachePut key={}, cache={} in Redis", key, object);
            }
        }

        return object;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String key, String name, boolean allEntries, boolean beforeInvocation) throws Throwable {
        if (beforeInvocation) {
            if (frequentLogPrint) {
                LOG.info("Before invocation, CacheEvict clear key={} in Redis", key);
            }

            try {
                clear(key, name, allEntries);
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while setting data", e);
            }
        }

        Object object = invocation.proceed();

        if (!beforeInvocation) {
            if (frequentLogPrint) {
                LOG.info("After invocation, CacheEvict clear key={} in Redis", key);
            }

            try {
                clear(key, name, allEntries);
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while setting data", e);
            }
        }

        return object;
    }

    private void clear(String key, String name, boolean allEntries) {
        String compositeWildcardKey = null;
        if (allEntries) {
            compositeWildcardKey = KeyUtil.getCompositeWildcardKey(prefix, name);
        } else {
            compositeWildcardKey = KeyUtil.getCompositeWildcardKey(key);
        }

        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        Set<String> keys = redisTemplate.keys(compositeWildcardKey);
        for (String k : keys) {
            redisTemplate.delete(k);
        }
    }
}