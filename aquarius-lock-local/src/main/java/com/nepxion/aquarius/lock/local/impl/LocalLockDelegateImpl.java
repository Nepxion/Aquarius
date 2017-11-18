package com.nepxion.aquarius.lock.local.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.aopalliance.intercept.MethodInvocation;

import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.entity.LockType;

public class LocalLockDelegateImpl implements LockDelegate {
    // 可重入锁可重复使用
    private volatile Map<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();
    private volatile Map<String, ReadWriteLock> readWriteLockMap = new ConcurrentHashMap<String, ReadWriteLock>();
    private boolean lockCached = true;

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        if (async) {
            throw new AquariusException("Async lock of Local isn't support for " + lockType);
        }

        Lock lock = null;
        try {
            lock = getLock(lockType, key, fair);
            boolean status = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            if (status) {
                return invocation.proceed();
            }
        } finally {
            unlock(lock);
        }

        return null;
    }

    private Lock getLock(LockType lockType, String key, boolean fair) {
        if (lockCached) {
            return getCachedLock(lockType, key, fair);
        } else {
            return getNewLock(lockType, key, fair);
        }
    }

    private Lock getNewLock(LockType lockType, String key, boolean fair) {
        switch (lockType) {
            case LOCK:
                return new ReentrantLock(fair);
            case READ_LOCK:
                return getCachedReadWriteLock(lockType, key, fair).readLock();
            case WRITE_LOCK:
                return getCachedReadWriteLock(lockType, key, fair).writeLock();
        }

        throw new AquariusException("Invalid Local lock type for " + lockType);
    }

    private Lock getCachedLock(LockType lockType, String key, boolean fair) {
        String newKey = lockType + "-" + key + "-" + "fair[" + fair + "]";

        Lock lock = lockMap.get(newKey);
        if (lock == null) {
            Lock newLock = getNewLock(lockType, key, fair);
            lock = lockMap.putIfAbsent(newKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }

        return lock;
    }

    private ReadWriteLock getCachedReadWriteLock(LockType lockType, String key, boolean fair) {
        String newKey = key + "-" + "fair[" + fair + "]";

        ReadWriteLock readWriteLock = readWriteLockMap.get(newKey);
        if (readWriteLock == null) {
            ReadWriteLock newReadWriteLock = new ReentrantReadWriteLock(fair);
            readWriteLock = readWriteLockMap.putIfAbsent(newKey, newReadWriteLock);
            if (readWriteLock == null) {
                readWriteLock = newReadWriteLock;
            }
        }

        return readWriteLock;
    }

    private void unlock(Lock lock) {
        if (lock instanceof ReentrantLock) {
            ReentrantLock reentrantLock = (ReentrantLock) lock;
            // 只有ReentrantLock提供isLocked方法
            if (reentrantLock.isLocked()) {
                reentrantLock.unlock();
            }
        } else {
            lock.unlock();
        }
    }
}