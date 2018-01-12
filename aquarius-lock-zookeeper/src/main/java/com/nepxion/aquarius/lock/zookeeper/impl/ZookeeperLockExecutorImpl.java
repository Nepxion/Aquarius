package com.nepxion.aquarius.lock.zookeeper.impl;

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
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.entity.LockType;

public class ZookeeperLockExecutorImpl implements LockExecutor<InterProcessMutex> {
    @Autowired
    private CuratorHandler curatorHandler;

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    // 可重入锁可重复使用
    private volatile Map<String, InterProcessMutex> lockMap = new ConcurrentHashMap<String, InterProcessMutex>();
    private volatile Map<String, InterProcessReadWriteLock> readWriteLockMap = new ConcurrentHashMap<String, InterProcessReadWriteLock>();
    private boolean lockCached = true;

    @PreDestroy
    public void destroy() {
        try {
            curatorHandler.close();
        } catch (Exception e) {
            throw new AquariusException("Close Curator failed", e);
        }
    }

    @Override
    public InterProcessMutex tryLock(LockType lockType, String name, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Exception {
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
    public InterProcessMutex tryLock(LockType lockType, String compositeKey, long leaseTime, long waitTime, boolean async, boolean fair) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        if (fair) {
            throw new AquariusException("Fair lock of Zookeeper isn't support for " + lockType);
        }

        if (async) {
            throw new AquariusException("Async lock of Zookeeper isn't support for " + lockType);
        }

        curatorHandler.validateStartedStatus();

        InterProcessMutex interProcessMutex = getLock(lockType, compositeKey);
        boolean acquired = interProcessMutex.acquire(waitTime, TimeUnit.MILLISECONDS);

        return acquired ? interProcessMutex : null;
    }

    @Override
    public void unlock(InterProcessMutex interProcessMutex) throws Exception {
        if (curatorHandler.isStarted()) {
            if (interProcessMutex != null && interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
            }
        }
    }

    private InterProcessMutex getLock(LockType lockType, String key) {
        if (lockCached) {
            return getCachedLock(lockType, key);
        } else {
            return getNewLock(lockType, key);
        }
    }

    private InterProcessMutex getNewLock(LockType lockType, String key) {
        String path = curatorHandler.getPath(prefix, key);
        CuratorFramework curator = curatorHandler.getCurator();
        switch (lockType) {
            case LOCK:
                return new InterProcessMutex(curator, path);
            case READ_LOCK:
                return getCachedReadWriteLock(lockType, key).readLock();
                // return new InterProcessReadWriteLock(curator, path).readLock();
            case WRITE_LOCK:
                return getCachedReadWriteLock(lockType, key).writeLock();
                // return new InterProcessReadWriteLock(curator, path).writeLock();
        }

        throw new AquariusException("Invalid Zookeeper lock type for " + lockType);
    }

    private InterProcessMutex getCachedLock(LockType lockType, String key) {
        String path = curatorHandler.getPath(prefix, key);
        String newKey = path + "-" + lockType;

        InterProcessMutex lock = lockMap.get(newKey);
        if (lock == null) {
            InterProcessMutex newLock = getNewLock(lockType, key);
            lock = lockMap.putIfAbsent(newKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }

        return lock;
    }

    private InterProcessReadWriteLock getCachedReadWriteLock(LockType lockType, String key) {
        String path = curatorHandler.getPath(prefix, key);
        String newKey = path;

        InterProcessReadWriteLock readWriteLock = readWriteLockMap.get(newKey);
        if (readWriteLock == null) {
            CuratorFramework curator = curatorHandler.getCurator();
            InterProcessReadWriteLock newReadWriteLock = new InterProcessReadWriteLock(curator, path);
            readWriteLock = readWriteLockMap.putIfAbsent(newKey, newReadWriteLock);
            if (readWriteLock == null) {
                readWriteLock = newReadWriteLock;
            }
        }

        return readWriteLock;
    }
}