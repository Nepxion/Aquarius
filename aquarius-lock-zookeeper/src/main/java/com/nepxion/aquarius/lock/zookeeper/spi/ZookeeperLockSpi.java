package com.nepxion.aquarius.lock.zookeeper.spi;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import java.util.concurrent.TimeUnit;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nepxion.aquarius.common.property.AquariusProperties;
import com.nepxion.aquarius.common.zookeeper.constant.ZookeeperConstant;
import com.nepxion.aquarius.common.zookeeper.handler.ZookeeperHandler;
import com.nepxion.aquarius.lock.entity.LockType;
import com.nepxion.aquarius.lock.exception.AopException;
import com.nepxion.aquarius.lock.spi.LockSpi;

public class ZookeeperLockSpi implements LockSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ZookeeperLockSpi.class);

    private AquariusProperties properties;
    private CuratorFramework curator;

    @Override
    public void initialize() {
        try {
            properties = ZookeeperHandler.createPropertyConfig(ZookeeperConstant.CONFIG_FILE);
            curator = ZookeeperHandler.createCurator(properties);
        } catch (Exception e) {
            LOG.error("Initialize Curator failed", e);
        }
    }

    @Override
    public void destroy() {
        ZookeeperHandler.closeCurator(curator);
    }

    @Override
    public Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable {
        if (curator == null) {
            throw new AopException("Curator isn't initialized");
        }

        if (!ZookeeperHandler.isStarted(curator)) {
            throw new AopException("Curator isn't started");
        }

        if (fair) {
            throw new AopException("Fair lock of Zookeeper isn't support for " + lockType);
        }

        if (async) {
            throw new AopException("Async lock of Zookeeper isn't support for " + lockType);
        }

        switch (lockType) {
            case LOCK:
                return invokeLock(invocation, key, leaseTime, waitTime);
            case READ_LOCK:
                return invokeReadLock(invocation, key, leaseTime, waitTime);
            case WRITE_LOCK:
                return invokeWriteLock(invocation, key, leaseTime, waitTime);
        }

        throw new AopException("Invalid Zookeeper lock type for " + lockType);
    }

    private Object invokeLock(MethodInvocation invocation, String key, long leaseTime, long waitTime) throws Throwable {
        LOG.debug("Execute invokeLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

        InterProcessMutex interProcessMutex = null;
        try {
            String path = getPath(key);
            interProcessMutex = new InterProcessMutex(curator, path);
            if (interProcessMutex != null) {
                boolean status = interProcessMutex.acquire(waitTime, TimeUnit.MILLISECONDS);
                if (status) {
                    return invocation.proceed();
                }
            }
        } finally {
            unlock(interProcessMutex);
        }

        return null;
    }

    private Object invokeReadLock(MethodInvocation invocation, String key, long leaseTime, long waitTime) throws Throwable {
        LOG.debug("Execute invokeReadLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

        InterProcessMutex interProcessMutex = null;
        try {
            String path = getPath(key);
            InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curator, path);
            interProcessMutex = interProcessReadWriteLock.readLock();
            if (interProcessMutex != null) {
                boolean status = interProcessMutex.acquire(waitTime, TimeUnit.MILLISECONDS);
                if (status) {
                    return invocation.proceed();
                }
            }
        } finally {
            unlock(interProcessMutex);
        }

        return null;
    }

    private Object invokeWriteLock(MethodInvocation invocation, String key, long leaseTime, long waitTime) throws Throwable {
        LOG.debug("Execute invokeWriteLock for key={}, leaseTime={}, waitTime={}", key, leaseTime, waitTime);

        InterProcessMutex interProcessMutex = null;
        try {
            String path = getPath(key);
            InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curator, path);
            interProcessMutex = interProcessReadWriteLock.writeLock();
            if (interProcessMutex != null) {
                boolean status = interProcessMutex.acquire(waitTime, TimeUnit.MILLISECONDS);
                if (status) {
                    return invocation.proceed();
                }
            }
        } finally {
            unlock(interProcessMutex);
        }

        return null;
    }

    private String getPath(String key) {
        // 锁节点路径，对应ZooKeeper一个永久节点，下挂一系列临时节点
        return properties.getString(ZookeeperConstant.ROOT_PATH) + "/" + key;
    }

    private void unlock(InterProcessMutex interProcessMutex) throws Throwable {
        if (ZookeeperHandler.isStarted(curator)) {
            if (interProcessMutex != null && interProcessMutex.isAcquiredInThisProcess()) {
                interProcessMutex.release();
            }
        }
    }
}