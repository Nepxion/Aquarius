package com.nepxion.aquarius.lock.aop;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
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
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.annotation.Lock;
import com.nepxion.aquarius.lock.annotation.ReadLock;
import com.nepxion.aquarius.lock.annotation.WriteLock;
import com.nepxion.aquarius.lock.entity.LockType;
import com.nepxion.matrix.aop.AbstractInterceptor;

@Component("lockInterceptor")
public class LockInterceptor extends AbstractInterceptor {
    private static final Logger LOG = LoggerFactory.getLogger(LockInterceptor.class);

    @Autowired
    private LockDelegate lockDelegate;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + "}")
    private Boolean frequentLogPrint;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Lock lockAnnotation = getLockAnnotation(invocation);
        if (lockAnnotation != null) {
            String name = lockAnnotation.name();
            String key = lockAnnotation.key();
            long leaseTime = lockAnnotation.leaseTime();
            long waitTime = lockAnnotation.waitTime();
            boolean async = lockAnnotation.async();
            boolean fair = lockAnnotation.fair();

            return invoke(invocation, lockAnnotation, name, key, leaseTime, waitTime, async, fair);
        }

        ReadLock readLockAnnotation = getReadLockAnnotation(invocation);
        if (readLockAnnotation != null) {
            String name = readLockAnnotation.name();
            String key = readLockAnnotation.key();
            long leaseTime = readLockAnnotation.leaseTime();
            long waitTime = readLockAnnotation.waitTime();
            boolean async = readLockAnnotation.async();
            boolean fair = readLockAnnotation.fair();

            return invoke(invocation, readLockAnnotation, name, key, leaseTime, waitTime, async, fair);
        }

        WriteLock writeLockAnnotation = getWriteLockAnnotation(invocation);
        if (writeLockAnnotation != null) {
            String name = writeLockAnnotation.name();
            String key = writeLockAnnotation.key();
            long leaseTime = writeLockAnnotation.leaseTime();
            long waitTime = writeLockAnnotation.waitTime();
            boolean async = writeLockAnnotation.async();
            boolean fair = writeLockAnnotation.fair();

            return invoke(invocation, writeLockAnnotation, name, key, leaseTime, waitTime, async, fair);
        }

        return invocation.proceed();
    }

    private Lock getLockAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(Lock.class)) {
            return method.getAnnotation(Lock.class);
        }

        return null;
    }

    private ReadLock getReadLockAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(ReadLock.class)) {
            return method.getAnnotation(ReadLock.class);
        }

        return null;
    }

    private WriteLock getWriteLockAnnotation(MethodInvocation invocation) {
        Method method = invocation.getMethod();
        if (method.isAnnotationPresent(WriteLock.class)) {
            return method.getAnnotation(WriteLock.class);
        }

        return null;
    }

    private Object invoke(MethodInvocation invocation, Annotation annotation, String name, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        LockType lockType = getLockType(annotation);
        if (lockType == null) {
            throw new AquariusException("Lock type is null for " + annotation);
        }

        String lockTypeValue = lockType.getValue();

        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Annotation [" + lockTypeValue + "]'s name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Annotation [" + lockTypeValue + "]'s key is null or empty");
        }

        String spelKey = getSpelKey(invocation, key);
        String compositeKey = KeyUtil.getCompositeKey(prefix, name, spelKey);
        String proxyType = getProxyType(invocation);
        String proxiedClassName = getProxiedClassName(invocation);
        String methodName = getMethodName(invocation);

        if (frequentLogPrint) {
            LOG.info("Intercepted for annotation - {} [key={}, leaseTime={}, waitTime={}, async={}, fair={}, proxyType={}, proxiedClass={}, method={}]", lockTypeValue, compositeKey, leaseTime, waitTime, async, fair, proxyType, proxiedClassName, methodName);
        }

        return lockDelegate.invoke(invocation, lockType, compositeKey, leaseTime, waitTime, async, fair);
    }

    private LockType getLockType(Annotation annotation) {
        if (annotation instanceof Lock) {
            return LockType.LOCK;
        } else if (annotation instanceof ReadLock) {
            return LockType.READ_LOCK;
        } else if (annotation instanceof WriteLock) {
            return LockType.WRITE_LOCK;
        }

        return null;
    }
}