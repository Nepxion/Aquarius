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

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.nepxion.aquarius.cache.delegate.CacheDelegate;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.util.KeyUtil;

public class RedisCacheDelegate implements CacheDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisCacheDelegate.class);

    @Autowired
    @Qualifier("aquariusRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

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

        LOG.info("Before invocation, Cacheable key={}, cache={} in Redis", key, object);

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

            LOG.info("After invocation, Cacheable key={}, cache={} in Redis", key, object);
        }

        return object;
    }

    @Override
    public Object invokeCachePut(MethodInvocation invocation, String key, long expire) throws Throwable {
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

            LOG.info("After invocation, CachePut key={}, cache={} in Redis", key, object);
        }

        return object;
    }

    @Override
    public Object invokeCacheEvict(MethodInvocation invocation, String key, String name, boolean allEntries, boolean beforeInvocation) throws Throwable {
        if (beforeInvocation) {
            LOG.info("Before invocation, CacheEvict clear key={} in Redis", key);
            try {
                clear(key, name, allEntries);
            } catch (Exception e) {
                LOG.warn("Redis exception occurs while setting data", e);
            }
        }

        Object object = invocation.proceed();

        if (!beforeInvocation) {
            LOG.info("After invocation, CacheEvict clear key={} in Redis", key);
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

        Set<String> keys = redisTemplate.keys(compositeWildcardKey);
        for (String k : keys) {
            redisTemplate.delete(k);
        }
    }
}