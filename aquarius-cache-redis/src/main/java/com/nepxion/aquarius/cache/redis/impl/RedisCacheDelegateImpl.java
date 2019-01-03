package com.nepxion.aquarius.cache.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.util.KeyUtil;

public class RedisCacheDelegateImpl implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegateImpl.class);

    @Autowired
    private RedisHandler redisHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    @Value("${" + CacheConstant.CACHE_AOP_EXCEPTION_IGNORE + ":true}")
    private Boolean cacheAopExceptionIgnore;

    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;

    @PostConstruct
    public void initialize() {
        try {
            redisTemplate = redisHandler.getRedisTemplate();
            valueOperations = redisTemplate.opsForValue();
        } catch (Exception e) {
            LOG.error("Get ValueOperations in Redis failed", e);
        }
    }

    @Override
    public Object invokeCacheable(MethodInvocation invocation, List<String> keys, long expire) throws Throwable {
        // 空值不缓存
        Object object = null;
        try {
            object = valueOperations.get(keys.get(0));

            if (frequentLogPrint) {
                LOG.info("Before invocation, Cacheable key={}, cache={} in Redis", keys, object);
            }
        } catch (Exception e) {
            if (cacheAopExceptionIgnore) {
                LOG.error("Redis exception occurs while Cacheable", e);
            } else {
                throw e;
            }
        }

        if (object != null) {
            return object;
        }

        object = invocation.proceed();

        if (object != null) {
            try {
                for (String key : keys) {
                    if (expire <= 0) {
                        valueOperations.set(key, object);
                    } else {
                        valueOperations.set(key, object, expire, TimeUnit.MILLISECONDS);
                    }
                }

                if (frequentLogPrint) {
                    LOG.info("After invocation, Cacheable key={}, cache={} in Redis", keys, object);
                }
            } catch (Exception e) {
                if (cacheAopExceptionIgnore) {
                    LOG.error("Redis exception occurs while Cacheable", e);
                } else {
                    throw e;
                }
            }
        }

        return object;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, List<String> keys, long expire) throws Throwable {
        // 空值不缓存
        Object object = invocation.proceed();
        if (object != null) {
            try {
                for (String key : keys) {
                    if (expire <= 0) {
                        valueOperations.set(key, object);
                    } else {
                        valueOperations.set(key, object, expire, TimeUnit.MILLISECONDS);
                    }
                }

                if (frequentLogPrint) {
                    LOG.info("After invocation, CachePut key={}, cache={} in Redis", keys, object);
                }
            } catch (Exception e) {
                if (cacheAopExceptionIgnore) {
                    LOG.error("Redis exception occurs while CachePut", e);
                } else {
                    throw e;
                }
            }
        }

        return object;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, List<String> keys, String name, boolean allEntries, boolean beforeInvocation) throws Throwable {
        List<String> compositeWildcardKeys = null;
        if (allEntries) {
            compositeWildcardKeys = new ArrayList<String>(1);
            // 通配全局Key, 例如：aquarius-cache
            String compositeWildcardKey = KeyUtil.getCompositeWildcardKey(prefix, name);
            compositeWildcardKeys.add(compositeWildcardKey);
        } else {
            compositeWildcardKeys = new ArrayList<String>(keys.size());
            for (String key : keys) {
                // 精准匹配当前Key, 例如：aquarius-cache-abc
                String compositeWildcardKey = KeyUtil.getCompositeWildcardKey(key);
                compositeWildcardKeys.add(compositeWildcardKey);
            }
        }

        if (beforeInvocation) {
            try {
                clear(compositeWildcardKeys);

                if (frequentLogPrint) {
                    if (allEntries) {
                        LOG.info("Before invocation, CacheEvict clear all keys with prefix={} in Redis", compositeWildcardKeys);
                    } else {
                        LOG.info("Before invocation, CacheEvict clear key={} in Redis", compositeWildcardKeys);
                    }
                }
            } catch (Exception e) {
                if (cacheAopExceptionIgnore) {
                    LOG.error("Redis exception occurs while CacheEvict", e);
                } else {
                    throw e;
                }
            }
        }

        Object object = invocation.proceed();

        if (!beforeInvocation) {
            try {
                clear(compositeWildcardKeys);

                if (frequentLogPrint) {
                    if (allEntries) {
                        LOG.info("After invocation, CacheEvict clear all keys with prefix={} in Redis", compositeWildcardKeys);
                    } else {
                        LOG.info("After invocation, CacheEvict clear key={} in Redis", compositeWildcardKeys);
                    }
                }
            } catch (Exception e) {
                if (cacheAopExceptionIgnore) {
                    LOG.error("Redis exception occurs while CacheEvict", e);
                } else {
                    throw e;
                }
            }
        }

        return object;
    }

    private void clear(List<String> compositeWildcardKeys) {
        for (String compositeWildcardKey : compositeWildcardKeys) {
            Set<String> keys = redisTemplate.keys(compositeWildcardKey);
            for (String k : keys) {
                redisTemplate.delete(k);
            }
        }
    }
}