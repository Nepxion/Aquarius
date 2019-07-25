package com.nepxion.aquarius.lock.zookeeper.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.constant.LockConstant;
import com.nepxion.aquarius.lock.entity.LockType;

public class ZookeeperLockDelegateImpl implements LockDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperLockDelegateImpl.class);

    @Autowired
    private LockExecutor<InterProcessMutex> lockExecutor;

    @Value("${" + LockConstant.LOCK_AOP_EXCEPTION_IGNORE + ":true}")
    private Boolean lockAopExceptionIgnore;

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        InterProcessMutex interProcessMutex = null;
        try {
            interProcessMutex = lockExecutor.tryLock(lockType, key, leaseTime, waitTime, async, fair);
            if (interProcessMutex != null) {
                try {
                    return invocation.proceed();
                } catch (Exception e) {
                    throw e;
                }
            }
        } catch (Exception e) {
            if (lockAopExceptionIgnore) {
                LOG.error("Zookeeper exception occurs while Lock, but still to proceed the invocation", e);

                return invocation.proceed();
            } else {
                throw e;
            }
        } finally {
            lockExecutor.unlock(interProcessMutex);
        }

        if (lockAopExceptionIgnore) {
            LOG.error("Acquired zookeeper lock failed, but still to proceed the invocation");

            return invocation.proceed();
        } else {
            throw new AquariusException("Acquired zookeeper lock failed, stop to proceed the invocation");
        }
    }
}