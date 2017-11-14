package com.nepxion.aquarius.lock.redis.delegate;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redisson.constant.RedissonConstant;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;
import com.nepxion.aquarius.lock.delegate.LockDelegate;
import com.nepxion.aquarius.lock.entity.LockType;

@Component("redisLockDelegate")
public class RedisLockDelegate implements LockDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLockDelegate.class);

    private RedissonClient redisson;

    // 可重入锁可重复使用
    private volatile Map<String, RLock> lockMap = new ConcurrentHashMap<String, RLock>();
    private volatile Map<String, RReadWriteLock> readWriteLockMap = new ConcurrentHashMap<String, RReadWriteLock>();
    private boolean lockCached = true;

    @Override
    public void initialize() {
        try {
            Config config = RedissonHandler.createYamlConfig(RedissonConstant.CONFIG_FILE);

            redisson = RedissonHandler.createRedisson(config);
        } catch (IOException e) {
            LOG.error("Initialize Redisson failed", e);
        }
    }

    @Override
    public void destroy() {
        RedissonHandler.closeRedisson(redisson);
    }

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        if (redisson == null) {
            throw new AquariusException("Redisson isn't initialized");
        }

        if (!RedissonHandler.isStarted(redisson)) {
            throw new AquariusException("Redisson isn't started");
        }

        if (lockType != LockType.LOCK && fair) {
            throw new AquariusException("Fair lock of Redis isn't support for " + lockType);
        }

        if (async) {
            return invokeLockAsync(invocation, lockType, key, leaseTime, waitTime, fair);
        } else {
            return invokeLock(invocation, lockType, key, leaseTime, waitTime, fair);
        }
    }

    private Object invokeLock(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        RLock lock = null;
        try {
            lock = getLock(lockType, key, fair);
            boolean status = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (status) {
                return invocation.proceed();
            }
        } finally {
            unlock(lock);
        }

        return null;
    }

    private Object invokeLockAsync(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        RLock lock = null;
        try {
            lock = getLock(lockType, key, fair);
            Future<Boolean> future = lock.tryLockAsync(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (future.get()) {
                return invocation.proceed();
            }
        } finally {
            unlock(lock);
        }

        return null;
    }

    private RLock getLock(LockType lockType, String key, boolean fair) {
        if (lockCached) {
            return getCachedLock(lockType, key, fair);
        } else {
            return getNewLock(lockType, key, fair);
        }
    }

    private RLock getNewLock(LockType lockType, String key, boolean fair) {
        switch (lockType) {
            case LOCK:
                if (fair) {
                    return redisson.getFairLock(key);
                } else {
                    return redisson.getLock(key);
                }
            case READ_LOCK:
                return getCachedReadWriteLock(lockType, key, fair).readLock();
                // return redisson.getReadWriteLock(key).readLock();
            case WRITE_LOCK:
                return getCachedReadWriteLock(lockType, key, fair).writeLock();
                // return redisson.getReadWriteLock(key).writeLock();
        }

        throw new AquariusException("Invalid Redis lock type for " + lockType);
    }

    private RLock getCachedLock(LockType lockType, String key, boolean fair) {
        String newKey = lockType + "-" + key + "-" + "fair[" + fair + "]";

        RLock lock = lockMap.get(newKey);
        if (lock == null) {
            RLock newLock = getNewLock(lockType, key, fair);
            lock = lockMap.putIfAbsent(newKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }

        return lock;
    }

    private RReadWriteLock getCachedReadWriteLock(LockType lockType, String key, boolean fair) {
        String newKey = key + "-" + "fair[" + fair + "]";

        RReadWriteLock readWriteLock = readWriteLockMap.get(newKey);
        if (readWriteLock == null) {
            RReadWriteLock newReadWriteLock = redisson.getReadWriteLock(key);
            readWriteLock = readWriteLockMap.putIfAbsent(newKey, newReadWriteLock);
            if (readWriteLock == null) {
                readWriteLock = newReadWriteLock;
            }
        }

        return readWriteLock;
    }

    private void unlock(RLock lock) {
        if (RedissonHandler.isStarted(redisson)) {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }
}