package com.nepxion.aquarius.limit.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.annotation.Limit;
import com.nepxion.matrix.aop.AbstractInterceptor;

@Component("limitInterceptor")
public class LimitInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LimitInterceptor.class);

    @Autowired
    private LimitDelegate limitDelegate;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Limit limitAnnotation = getLimitAnnotation(invocation);
        if (limitAnnotation != null) {
            String name = limitAnnotation.name();
            String key = limitAnnotation.key();
            int limitPeriod = limitAnnotation.limitPeriod();
            int limitCount = limitAnnotation.limitCount();

            return invoke(invocation, limitAnnotation, name, key, limitPeriod, limitCount);
        }

        return invocation.proceed();
    }

    private Limit getLimitAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(Limit.class)) {
            return method.getAnnotation(Limit.class);
        }

        return null;
    }

    private Object invoke(MethodInvocation invocation, Annotation annotation, String name, String key, int limitPeriod, int limitCount) throws Throwable {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [Limit]'s name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [Limit]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, key);
        String compositeKey = KeyUtil.getCompositeKey(prefix, name, spelKey);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        if (frequentLogPrint) {
            LOG.info("Intercepted for annotation - Limit [key={}, limitPeriod={}, limitCount={}, proxyType={}, proxiedClass={}, method={}]", compositeKey, limitPeriod, limitCount, proxyType, proxiedClassName, methodName);
        }

        return limitDelegate.invoke(invocation, compositeKey, limitPeriod, limitCount);
    }
}