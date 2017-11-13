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

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.cache.annotation.CacheEvict;
import com.nepxion.aquarius.cache.annotation.CachePut;
import com.nepxion.aquarius.cache.annotation.Cacheable;
import com.nepxion.aquarius.cache.spi.CacheSpiLoader;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.matrix.aop.AbstractInterceptor;

@Component("cacheInterceptor")
public class CacheInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(CacheInterceptor.class);

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Cacheable cacheableAnnotation = getCacheableAnnotation(invocation);
        if (cacheableAnnotation != null) {
            String value = cacheableAnnotation.value();
            String key = cacheableAnnotation.key();
            long expire = cacheableAnnotation.expire();

            return invokeCacheable(invocation, value, key, expire);
        }

        CacheEvict cacheEvictAnnotation = getCacheEvictAnnotation(invocation);
        if (cacheEvictAnnotation != null) {
            String value = cacheEvictAnnotation.value();
            String key = cacheEvictAnnotation.key();
            boolean allEntries = cacheEvictAnnotation.allEntries();
            boolean beforeInvocation = cacheEvictAnnotation.beforeInvocation();

            return invokeCacheEvict(invocation, value, key, allEntries, beforeInvocation);
        }

        CachePut cachePutAnnotation = getCachePutAnnotation(invocation);
        if (cachePutAnnotation != null) {
            String value = cachePutAnnotation.value();
            String key = cachePutAnnotation.key();
            long expire = cachePutAnnotation.expire();

            return invokeCachePut(invocation, value, key, expire);
        }

        return invocation.proceed();
    }

    private Object invokeCacheable(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [Cacheable]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, value, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - Cacheable [value={}, key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", value, spelKey, expire, proxyType, proxiedClassName, methodName);

        return CacheSpiLoader.load().invokeCacheable(invocation, value, key, expire);
    }

    private Object invokeCacheEvict(MethodInvocation invocation, String value, String key, boolean allEntries, boolean beforeInvocation) throws Throwable {
        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [CacheEvict]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, value, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - CacheEvict [value={}, key={}, allEntries={}, beforeInvocation={}, proxyType={}, proxiedClass={}, method={}]", value, spelKey, allEntries, beforeInvocation, proxyType, proxiedClassName, methodName);

        return CacheSpiLoader.load().invokeCacheEvict(invocation, value, key, allEntries, beforeInvocation);
    }

    private Object invokeCachePut(MethodInvocation invocation, String value, String key, long expire) throws Throwable {
        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [CachePut]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, value, key);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        LOG.info("Intercepted for annotation - CachePut [value={}, key={}, expire={}, proxyType={}, proxiedClass={}, method={}]", value, spelKey, expire, proxyType, proxiedClassName, methodName);

        return CacheSpiLoader.load().invokeCachePut(invocation, value, key, expire);
    }

    public String getSpelKey(MethodInvocation invocation, String value, String key) {
        String[] parameterNames = getParameterNames(invocation);
        Object[] arguments = getArguments(invocation);

        // 使用SPEL进行Key的解析
        ExpressionParser parser = new SpelExpressionParser();

        // SPEL上下文
        EvaluationContext context = new StandardEvaluationContext();

        // 把方法参数放入SPEL上下文中
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], arguments[i]);
        }

        return CacheSpiLoader.load().getPrefix() + "_" + value + "_" + parser.parseExpression(key).getValue(context, String.class);
    }

    private Cacheable getCacheableAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(Cacheable.class)) {
            return method.getAnnotation(Cacheable.class);
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

    private CachePut getCachePutAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(CachePut.class)) {
            return method.getAnnotation(CachePut.class);
        }

        return null;
    }
}