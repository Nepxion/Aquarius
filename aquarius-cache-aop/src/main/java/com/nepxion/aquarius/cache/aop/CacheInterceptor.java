package com.nepxion.aquarius.cache.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.lang.reflect.Method;

import javax.annotation.PostConstruct;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;
import com.nepxion.aquarius.cache.delegate.CacheDelegate;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.property.AquariusProperties;
import com.nepxion.matrix.aop.AbstractInterceptor;

@Component("cacheInterceptor")
public class CacheInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CacheInterceptor.class);

    @Autowired
    private CacheDelegate cacheDelegate;

    @Autowired
    private AquariusProperties properties;

    private String prefix;

    @PostConstruct
    public void initialize() {
        LOG.info("Cache delegate instance is {}...", cacheDelegate.getClass());

        prefix = properties.getString(AquariusConstant.NAMESPACE);
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Cacheable cacheableAnnotation = getCacheableAnnotation(invocation);
        if (cacheableAnnotation != null) {
            String name = cacheableAnnotation.name();
            String key = cacheableAnnotation.key();
            long expire = cacheableAnnotation.expire();

            return invokeCacheable(invocation, name, key, expire);
        }

        CachePut cachePutAnnotation = getCachePutAnnotation(invocation);
        if (cachePutAnnotation != null) {
            String name = cachePutAnnotation.name();
            String key = cachePutAnnotation.key();
            long expire = cachePutAnnotation.expire();

            return invokeCachePut(invocation, name, key, expire);
        }

        CacheEvict cacheEvictAnnotation = getCacheEvictAnnotation(invocation);
        if (cacheEvictAnnotation != null) {
            String name = cacheEvictAnnotation.name();
            String key = cacheEvictAnnotation.key();
            boolean allEntries = cacheEvictAnnotation.allEntries();
            boolean beforeInvocation = cacheEvictAnnotation.beforeInvocation();

            return invokeCacheEvict(invocation, name, key, allEntries, beforeInvocation);
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

    private Object invokeCacheable(MethodInvocation invocation, String name, String key, long expire) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [Cacheable]'s name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [Cacheable]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, prefix, name, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - Cacheable [name={}, key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", name, spelKey, expire, proxyType, proxiedClassName, methodName);

        return cacheDelegate.invokeCacheable(invocation, spelKey, expire);
    }

    private Object invokeCachePut(MethodInvocation invocation, String name, String key, long expire) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [CachePut]'s name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [CachePut]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, prefix, name, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - CachePut [name={}, key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", name, spelKey, expire, proxyType, proxiedClassName, methodName);

        return cacheDelegate.invokeCachePut(invocation, spelKey, expire);
    }

    private Object invokeCacheEvict(MethodInvocation invocation, String name, String key, boolean allEntries, boolean beforeInvocation) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [CacheEvict]'s name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [CacheEvict]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, prefix, name, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - CacheEvict [name={}, key={}, allEntries={}, beforeInvocation={}, proxyType={}, proxiedClass={}, method={}]", name, spelKey, allEntries, beforeInvocation, proxyType, proxiedClassName, methodName);

        return cacheDelegate.invokeCacheEvict(invocation, spelKey, name, allEntries, beforeInvocation);
    }
}