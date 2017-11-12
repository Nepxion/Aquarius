package com.nepxion.aquarius.lock.redis.spi;

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
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.redis.constant.RedisConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.lock.entity.LockType;
import com.nepxion.aquarius.lock.exception.AopException;
import com.nepxion.aquarius.lock.spi.LockSpi;

public class RedisLockSpi implements LockSpi {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLockSpi.class);

    private RedissonClient redisson;

    // 可重入锁可重复使用
    private volatile Map<String, RLock> lockMap = new ConcurrentHashMap<String, RLock>();
    private boolean lockCached = true;

    @Override
    public void initialize() {
        try {
            Config config = RedisHandler.createYamlConfig(RedisConstant.CONFIG_FILE);

            redisson = RedisHandler.createRedisson(config);
        } catch (IOException e) {
            LOG.error("Initialize Redisson failed", e);
        }
    }

    @Override
    public void destroy() {
        RedisHandler.closeRedisson(redisson);
    }

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        if (redisson == null) {
            throw new AopException("Redisson isn't initialized");
        }

        if (!RedisHandler.isStarted(redisson)) {
            throw new AopException("Redisson isn't started");
        }

        switch (lockType) {
            case LOCK: {
                if (async) {
                    if (fair) {
                        return invokeFairLockAsync(invocation, lockType, key, leaseTime, waitTime, fair);
                    } else {
                        return invokeLockAsync(invocation, lockType, key, leaseTime, waitTime, fair);
                    }
                } else {
                    if (fair) {
                        return invokeFairLock(invocation, lockType, key, leaseTime, waitTime, fair);
                    } else {
                        return invokeLock(invocation, lockType, key, leaseTime, waitTime, fair);
                    }
                }
            }
            case READ_LOCK: {
                if (fair) {
                    throw new AopException("Fair lock of Redis isn't support for " + lockType);
                }
                if (async) {
                    return invokeReadLockAsync(invocation, lockType, key, leaseTime, waitTime, fair);
                } else {
                    return invokeReadLock(invocation, lockType, key, leaseTime, waitTime, fair);
                }
            }
            case WRITE_LOCK: {
                if (fair) {
                    throw new AopException("Fair lock of Redis isn't support for " + lockType);
                }
                if (async) {
                    return invokeWriteLockAsync(invocation, lockType, key, leaseTime, waitTime, fair);
                } else {
                    return invokeWriteLock(invocation, lockType, key, leaseTime, waitTime, fair);
                }
            }
        }

        throw new AopException("Invalid Redis lock type for " + lockType);
    }

    private Object invokeLock(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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
        LOG.debug("Execute invokeLockAsync for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeFairLock(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeFairLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeFairLockAsync(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeFairLockAsync for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeReadLock(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeReadLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeReadLockAsync(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeReadLockAsync for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeWriteLock(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeWriteLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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

    private Object invokeWriteLockAsync(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean fair) throws Throwable {
        LOG.debug("Execute invokeWriteLockAsync for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

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
                return redisson.getReadWriteLock(key).readLock();
            case WRITE_LOCK:
                return redisson.getReadWriteLock(key).writeLock();
        }

        throw new AopException("Invalid Redis lock type for " + lockType);
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

    private void unlock(RLock lock) {
        if (RedisHandler.isStarted(redisson)) {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }
}