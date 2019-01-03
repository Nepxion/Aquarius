package com.nepxion.aquarius.cache.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;
import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.matrix.proxy.aop.AbstractInterceptor;

public class CacheInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CacheInterceptor.class);

    @Autowired
    private CacheDelegate cacheDelegate;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    @Value("${" + CacheConstant.CACHE_EXPIRE + ":-1}")
    private long expiration;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Cacheable cacheableAnnotation = getCacheableAnnotation(invocation);
        if (cacheableAnnotation != null) {
            String name = cacheableAnnotation.name();
            String[] keys = cacheableAnnotation.key();
            long expire = cacheableAnnotation.expire();
            // 如果局部变量没配置，取全局变量值
            if (expire == -1234567890L) {
                expire = expiration;
            }

            return invokeCacheable(invocation, name, keys, expire);
        }

        CachePut cachePutAnnotation = getCachePutAnnotation(invocation);
        if (cachePutAnnotation != null) {
            String name = cachePutAnnotation.name();
            String[] keys = cachePutAnnotation.key();
            long expire = cachePutAnnotation.expire();
            // 如果局部变量没配置，取全局变量值
            if (expire == -1234567890L) {
                expire = expiration;
            }

            return invokeCachePut(invocation, name, keys, expire);
        }

        CacheEvict cacheEvictAnnotation = getCacheEvictAnnotation(invocation);
        if (cacheEvictAnnotation != null) {
            String name = cacheEvictAnnotation.name();
            String[] keys = cacheEvictAnnotation.key();
            boolean allEntries = cacheEvictAnnotation.allEntries();
            boolean beforeInvocation = cacheEvictAnnotation.beforeInvocation();

            return invokeCacheEvict(invocation, name, keys, allEntries, beforeInvocation);
        }

        return invocation.proceed();
    }

    private Cacheable getCacheableAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(Cacheable.class)) {
            return method.getAnnotation(Cacheable.class);
        }

        return null;
    }

    private CachePut getCachePutAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(CachePut.class)) {
            return method.getAnnotation(CachePut.class);
        }

        return null;
    }

    private CacheEvict getCacheEvictAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(CacheEvict.class)) {
            return method.getAnnotation(CacheEvict.class);
        }

        return null;
    }

    private Object invokeCacheable(MethodInvocation invocation, String name, String[] keys, long expire) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [Cacheable]'s name is null or empty");
        }

        if (ArrayUtils.isEmpty(keys)) {
            throw new AquariusException("Annotation [Cacheable]'s key is null or empty");
        }

        List<String> compositeKeys = new ArrayList<String>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (StringUtils.isEmpty(key)) {
                throw new AquariusException("Annotation [Cacheable]'s key is null or empty");
            }

            String spelKey = null;
            try {
                spelKey = getSpelKey(invocation, key);
            } catch (Exception e) {
                spelKey = key;
            }
            String compositeKey = KeyUtil.getCompositeKey(prefix, name, spelKey);
            compositeKeys.add(compositeKey);
        }
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        if (frequentLogPrint) {
            LOG.info("Intercepted for annotation - Cacheable [key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", compositeKeys, expire, proxyType, proxiedClassName, methodName);
        }

        return cacheDelegate.invokeCacheable(invocation, compositeKeys, expire);
    }

    private Object invokeCachePut(MethodInvocation invocation, String name, String[] keys, long expire) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [CachePut]'s name is null or empty");
        }

        if (ArrayUtils.isEmpty(keys)) {
            throw new AquariusException("Annotation [CachePut]'s key is null or empty");
        }

        List<String> compositeKeys = new ArrayList<String>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (StringUtils.isEmpty(key)) {
                throw new AquariusException("Annotation [CachePut]'s key is null or empty");
            }

            String spelKey = null;
            try {
                spelKey = getSpelKey(invocation, key);
            } catch (Exception e) {
                spelKey = key;
            }
            String compositeKey = KeyUtil.getCompositeKey(prefix, name, spelKey);
            compositeKeys.add(compositeKey);
        }
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        if (frequentLogPrint) {
            LOG.info("Intercepted for annotation - CachePut [key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", compositeKeys, expire, proxyType, proxiedClassName, methodName);
        }

        return cacheDelegate.invokeCachePut(invocation, compositeKeys, expire);
    }

    private Object invokeCacheEvict(MethodInvocation invocation, String name, String[] keys, boolean allEntries, boolean beforeInvocation) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [CacheEvict]'s name is null or empty");
        }

        if (ArrayUtils.isEmpty(keys)) {
            throw new AquariusException("Annotation [CacheEvict]'s key is null or empty");
        }

        List<String> compositeKeys = new ArrayList<String>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            if (StringUtils.isEmpty(key)) {
                throw new AquariusException("Annotation [CacheEvict]'s key is null or empty");
            }

            String spelKey = null;
            try {
                spelKey = getSpelKey(invocation, key);
            } catch (Exception e) {
                spelKey = key;
            }
            String compositeKey = KeyUtil.getCompositeKey(prefix, name, spelKey);
            compositeKeys.add(compositeKey);
        }
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        if (frequentLogPrint) {
            LOG.info("Intercepted for annotation - CacheEvict [key={}, allEntries={}, beforeInvocation={}, proxyType={}, proxiedClass={}, method={}]", compositeKeys, allEntries, beforeInvocation, proxyType, proxiedClassName, methodName);
        }

        return cacheDelegate.invokeCacheEvict(invocation, compositeKeys, name, allEntries, beforeInvocation);
    }
}