package com.nepxion.aquarius.lock.zookeeper.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;

import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.entity.LockType;

public class ZookeeperLockDelegateImpl implements LockDelegate {
    @Autowired
    private LockExecutor<InterProcessMutex> lockExecutor;

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        InterProcessMutex interProcessMutex = null;
        try {
            interProcessMutex = lockExecutor.tryLock(lockType, key, leaseTime, waitTime, async, fair);
            if (interProcessMutex != null) {
                return invocation.proceed();
            }
        } finally {
            lockExecutor.unlock(interProcessMutex);
        }

        return null;
    }
}