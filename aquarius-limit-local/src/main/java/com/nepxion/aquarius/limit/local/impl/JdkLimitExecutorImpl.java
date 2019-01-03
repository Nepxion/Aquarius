package com.nepxion.aquarius.limit.local.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.limit.LimitExecutor;

public class JdkLimitExecutorImpl implements LimitExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(JdkLimitExecutorImpl.class);

    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    private volatile Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<String, AtomicInteger>();
    private volatile Map<String, AtomicBoolean> statusMap = new ConcurrentHashMap<String, AtomicBoolean>();
    private volatile Map<String, Lock> lockMap = new ConcurrentHashMap<String, Lock>();
    private volatile Map<String, Timer> timerMap = new ConcurrentHashMap<String, Timer>();

    @Override
    public boolean tryAccess(String name, String key, int limitPeriod, int limitCount) throws Exception {
        if (StringUtils.isEmpty(name)) {
            throw new AquariusException("Name is null or empty");
        }

        if (StringUtils.isEmpty(key)) {
            throw new AquariusException("Key is null or empty");
        }

        String compositeKey = KeyUtil.getCompositeKey(prefix, name, key);

        return tryAccess(compositeKey, limitPeriod, limitCount);
    }

    @Override
    public boolean tryAccess(String compositeKey, int limitPeriod, int limitCount) throws Exception {
        if (StringUtils.isEmpty(compositeKey)) {
            throw new AquariusException("Composite key is null or empty");
        }

        Lock lock = getLock(compositeKey);

        try {
            lock.lock();

            AtomicInteger counter = getCounter(compositeKey);
            AtomicBoolean status = getStatus(compositeKey);
            Timer timer = getTimer(compositeKey);

            if (!status.get()) {
                startTimer(counter, status, timer, limitPeriod);
                while (!status.get()) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            int count = counter.get();
            if (count <= limitCount) {
                count = counter.incrementAndGet();
            }

            if (frequentLogPrint) {
                LOG.info("Access try count is {} for key={}", count, compositeKey);
            }

            return count <= limitCount;
        } finally {
            lock.unlock();
        }
    }

    private void startTimer(AtomicInteger counter, AtomicBoolean status, Timer timer, int limitPeriod) {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                counter.getAndSet(0);
                status.getAndSet(true);
            }
        }, 0L, limitPeriod * 1000L);
    }

    private AtomicInteger getCounter(String compositeKey) {
        AtomicInteger counter = counterMap.get(compositeKey);
        if (counter == null) {
            AtomicInteger newCounter = new AtomicInteger(0);
            counter = counterMap.putIfAbsent(compositeKey, newCounter);
            if (counter == null) {
                counter = newCounter;
            }
        }

        return counter;
    }

    private AtomicBoolean getStatus(String compositeKey) {
        AtomicBoolean status = statusMap.get(compositeKey);
        if (status == null) {
            AtomicBoolean newStatus = new AtomicBoolean(false);
            status = statusMap.putIfAbsent(compositeKey, newStatus);
            if (status == null) {
                status = newStatus;
            }
        }

        return status;
    }

    private Lock getLock(String compositeKey) {
        Lock lock = lockMap.get(compositeKey);
        if (lock == null) {
            Lock newLock = new ReentrantLock();
            lock = lockMap.putIfAbsent(compositeKey, newLock);
            if (lock == null) {
                lock = newLock;
            }
        }

        return lock;
    }

    private Timer getTimer(String compositeKey) {
        Timer timer = timerMap.get(compositeKey);
        if (timer == null) {
            Timer newTimer = new Timer();
            timer = timerMap.putIfAbsent(compositeKey, newTimer);
            if (timer == null) {
                timer = newTimer;
            }
        }

        return timer;
    }
}