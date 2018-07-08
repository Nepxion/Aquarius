package com.nepxion.aquarius.lock.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.entity.LockType;

public class RedisLockDelegateImpl implements LockDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLockDelegateImpl.class);

    @Autowired
    private LockExecutor<RLock> lockExecutor;

    @Value("${" + AquariusConstant.AOP_EXCEPTION_IGNORE + ":true}")
    private Boolean aopExceptionIgnore;

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        RLock lock = null;
        try {
            lock = lockExecutor.tryLock(lockType, key, leaseTime, waitTime, async, fair);
            if (lock != null) {
                return invocation.proceed();
            }
        } catch (Exception e) {
            if (aopExceptionIgnore) {
                LOG.error("Lock executes failed", e);

                return invocation.proceed();
            } else {
                throw e;
            }
        } finally {
            lockExecutor.unlock(lock);
        }

        return invocation.proceed();
    }
}