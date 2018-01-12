package com.nepxion.aquarius.lock.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.entity.LockType;

public class RedisLockExecutorImpl implements LockExecutor<RLock> {
    @Autowired
    private RedissonHandler redissonHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    // 可重入锁可重复使用
    private volatile Map<String, RLock> lockMap = new ConcurrentHashMap<String, RLock>();
    private volatile Map<String, RReadWriteLock> readWriteLockMap = new ConcurrentHashMap<String, RReadWriteLock>();
    private boolean lockCached = true;

    @PreDestroy
    public void destroy() {
        try {
            redissonHandler.close();
        } catch (Exception e) {
            throw new AquariusException("Close Redisson failed", e);
        }
    }

    @Override
    public RLock tryLock(LockType lockType, String name, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Key is null or empty");
        }

        String compositeKey = KeyUtil.getCompositeKey(prefix, name, key);

        return tryLock(lockType, compositeKey, leaseTime, waitTime, async, fair);
    }

    @Override
    public RLock tryLock(LockType lockType, String compositeKey, long leaseTime, long waitTime, boolean async, boolean fair) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        if (lockType != LockType.LOCK && fair) {
            throw new AquariusException("Fair lock of Redis isn't support for " + lockType);
        }

        redissonHandler.validateStartedStatus();

        if (async) {
            return invokeLockAsync(lockType, compositeKey, leaseTime, waitTime, fair);
        } else {
            return invokeLock(lockType, compositeKey, leaseTime, waitTime, fair);
        }
    }

    @Override
    public void unlock(RLock lock) throws Exception {
        if (redissonHandler.isStarted()) {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    private RLock invokeLock(LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Exception {
        RLock lock = getLock(lockType, key, fair);
        boolean acquired = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);

        return acquired ? lock : null;
    }

    private RLock invokeLockAsync(LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Exception {
        RLock lock = getLock(lockType, key, fair);
        boolean acquired = lock.tryLockAsync(waitTime, leaseTime, TimeUnit.MILLISECONDS).get();

        return acquired ? lock : null;
    }

    private RLock getLock(LockType lockType, String key, boolean fair) {
        if (lockCached) {
            return getCachedLock(lockType, key, fair);
        } else {
            return getNewLock(lockType, key, fair);
        }
    }

    private RLock getNewLock(LockType lockType, String key, boolean fair) {
        RedissonClient redisson = redissonHandler.getRedisson();
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
            RedissonClient redisson = redissonHandler.getRedisson();
            RReadWriteLock newReadWriteLock = redisson.getReadWriteLock(key);
            readWriteLock = readWriteLockMap.putIfAbsent(newKey, newReadWriteLock);
            if (readWriteLock == null) {
                readWriteLock = newReadWriteLock;
            }
        }

        return readWriteLock;
    }
}