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
import org.springframework.beans.factory.annotation.Autowired;

import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.entity.LockType;

public class RedisLockDelegateImpl implements LockDelegate {
    @Autowired
    private LockExecutor<RLock> lockExecutor;

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        RLock lock = null;
        try {
            lock = lockExecutor.tryLock(lockType, key, leaseTime, waitTime, async, fair);
            if (lock != null) {
                return invocation.proceed();
            }
        } finally {
            lockExecutor.unlock(lock);
        }

        return null;
    }
}