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
import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;

public class RedissonCacheDelegateImpl implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedissonCacheDelegateImpl.class);

    @Autowired
    private RedissonHandler redissonHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    @Value("${" + CacheConstant.CACHE_AOP_EXCEPTION_IGNORE + ":true}")
    private Boolean cacheAopExceptionIgnore;

    private RMapCache<String, Object> cache;

    @PostConstruct
    public void initialize() {
        try {
            redissonHandler.validateStartedStatus();

            RedissonClient redission = redissonHandler.getRedisson();
            cache = redission.getMapCache(prefix);
        } catch (Exception e) {
            LOG.error("Get MapCache in Redisson failed", e);
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
    public Object invokeCacheable(MethodInvocation invocation, List<String> keys, long expire) throws Throwable {
        // 空值不缓存
        Object object = null;
        try {
            redissonHandler.validateStartedStatus();

            object = cache.get(keys.get(0));

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
                redissonHandler.validateStartedStatus();

                for (String key : keys) {
                    if (expire <= 0) {
                        cache.fastPut(key, object);
                    } else {
                        cache.fastPut(key, object, expire, TimeUnit.MILLISECONDS);
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
                redissonHandler.validateStartedStatus();

                for (String key : keys) {
                    if (expire <= 0) {
                        cache.fastPut(key, object);
                    } else {
                        cache.fastPut(key, object, expire, TimeUnit.MILLISECONDS);
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
            String compositeWildcardKey = prefix + "_" + name;
            compositeWildcardKeys.add(compositeWildcardKey);
        } else {
            // 精准匹配当前Key, 例如：aquarius-cache-abc
            compositeWildcardKeys = keys;
        }

        if (beforeInvocation) {
            try {
                redissonHandler.validateStartedStatus();

                clear(compositeWildcardKeys, allEntries);

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
                redissonHandler.validateStartedStatus();

                clear(compositeWildcardKeys, allEntries);

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

    private void clear(List<String> compositeWildcardKeys, boolean allEntries) {
        Set<String> keys = cache.keySet();
        for (String k : keys) {
            if (allEntries) {
                for (String compositeWildcardKey : compositeWildcardKeys) {
                    if (k.startsWith(compositeWildcardKey)) {
                        cache.remove(k);
                    }
                }
            } else {
                for (String compositeWildcardKey : compositeWildcardKeys) {
                    if (StringUtils.equals(k, compositeWildcardKey)) {
                        cache.remove(k);
                    }
                }
            }
        }
    }
}