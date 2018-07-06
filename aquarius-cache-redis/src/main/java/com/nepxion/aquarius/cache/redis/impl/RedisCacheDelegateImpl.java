package com.nepxion.aquarius.cache.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
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

            if (frequentLogPrint) {
                LOG.info("Before invocation, Cacheable key={}, cache={} in Redis", key, object);
            }
        } catch (Exception e) {
            LOG.warn("Redis exception occurs while Cacheable", e);
        }

        if (object != null) {
            return object;
        }

        object = invocation.proceed();

        if (object != null) {
            try {
                if (expire <= 0) {
                    valueOperations.set(key, object);
                } else {
                    valueOperations.set(key, object, expire, TimeUnit.MILLISECONDS);
                }

                if (frequentLogPrint) {
                    LOG.info("After invocation, Cacheable key={}, cache={} in Redis", key, object);
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while Cacheable", e);
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
                if (expire <= 0) {
                    valueOperations.set(key, object);
                } else {
                    valueOperations.set(key, object, expire, TimeUnit.MILLISECONDS);
                }

                if (frequentLogPrint) {
                    LOG.info("After invocation, CachePut key={}, cache={} in Redis", key, object);
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while CachePut", e);
            }
        }

        return object;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String key, String name, boolean allEntries, boolean beforeInvocation) throws Throwable {
        String compositeWildcardKey = null;
        if (allEntries) {
            // 通配全局Key, 例如：aquarius-cache
            compositeWildcardKey = KeyUtil.getCompositeWildcardKey(prefix, name);
        } else {
            // 精准匹配当前Key, 例如：aquarius-cache-abc
            compositeWildcardKey = KeyUtil.getCompositeWildcardKey(key);
        }

        if (beforeInvocation) {
            try {
                clear(compositeWildcardKey);

                if (frequentLogPrint) {
                    if (allEntries) {
                        LOG.info("Before invocation, CacheEvict clear all keys with prefix={} in Redis", compositeWildcardKey);
                    } else {
                        LOG.info("Before invocation, CacheEvict clear key={} in Redis", compositeWildcardKey);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while CacheEvict", e);
            }
        }

        Object object = invocation.proceed();

        if (!beforeInvocation) {
            try {
                clear(compositeWildcardKey);

                if (frequentLogPrint) {
                    if (allEntries) {
                        LOG.info("After invocation, CacheEvict clear all keys with prefix={} in Redis", compositeWildcardKey);
                    } else {
                        LOG.info("After invocation, CacheEvict clear key={} in Redis", compositeWildcardKey);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while CacheEvict", e);
            }
        }

        return object;
    }

    private void clear(String compositeWildcardKey) {
        RedisTemplate<String, Object> redisTemplate = redisHandler.getRedisTemplate();
        Set<String> keys = redisTemplate.keys(compositeWildcardKey);
        for (String k : keys) {
            redisTemplate.delete(k);
        }
    }
}