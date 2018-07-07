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

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;

public class RedissonCacheDelegateImpl implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegateImpl.class);

    @Autowired
    private RedissonHandler redissonHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    private RMapCache<String, Object> cache;

    @PostConstruct
    public void initialize() {
        try {
            RedissonClient redission = redissonHandler.getRedisson();
            cache = redission.getMapCache(prefix);
        } catch (Exception e) {
            LOG.warn("Get MapCache in Redisson failed, maybe it has't been intialized");
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            redissonHandler.close();
        } catch (Exception e) {
            throw new AquariusException("Close Redisson failed", e);
        }
    }

    @Override
    public Object invokeCacheable(MethodInvocation invocation, String key, long expire) throws Throwable {
        // 空值不缓存
        Object object = null;
        try {
            if (cache != null) {
                object = cache.get(key);

                if (frequentLogPrint) {
                    LOG.info("Before invocation, Cacheable key={}, cache={} in Redis", key, object);
                }
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
                if (cache != null) {
                    if (expire <= 0) {
                        cache.fastPut(key, object);
                    } else {
                        cache.fastPut(key, object, expire, TimeUnit.MILLISECONDS);
                    }

                    if (frequentLogPrint) {
                        LOG.info("After invocation, Cacheable key={}, cache={} in Redis", key, object);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while Cacheable", e);
            }
        }

        return object;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String key, long expire) throws Throwable {
        // 空值不缓存
        Object object = invocation.proceed();
        if (object != null) {
            try {
                if (cache != null) {
                    if (expire <= 0) {
                        cache.fastPut(key, object);
                    } else {
                        cache.fastPut(key, object, expire, TimeUnit.MILLISECONDS);
                    }

                    if (frequentLogPrint) {
                        LOG.info("After invocation, CachePut key={}, cache={} in Redis", key, object);
                    }
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
            compositeWildcardKey = prefix + "_" + name;
        } else {
            // 精准匹配当前Key, 例如：aquarius-cache-abc
            compositeWildcardKey = key;
        }

        if (beforeInvocation) {
            if (cache != null) {
                try {
                    clear(compositeWildcardKey, allEntries);

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
        }

        Object object = invocation.proceed();

        if (!beforeInvocation) {
            if (cache != null) {
                try {
                    clear(compositeWildcardKey, allEntries);

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
        }

        return object;
    }

    private void clear(String compositeWildcardKey, boolean allEntries) {
        Set<String> keys = cache.keySet();
        for (String k : keys) {
            if (allEntries) {
                if (k.startsWith(compositeWildcardKey)) {
                    cache.remove(k);
                }
            } else {
                if (StringUtils.equals(k, compositeWildcardKey)) {
                    cache.remove(k);
                }
            }
        }
    }
}