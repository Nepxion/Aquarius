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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.util.concurrent.RateLimiter;
import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.common.util.KeyUtil;
import com.nepxion.aquarius.limit.LimitExecutor;

public class GuavaLocalLimitExecutorImpl implements LimitExecutor {
    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Value("${" + AquariusConstant.FREQUENT_LOG_PRINT + ":false}")
    private Boolean frequentLogPrint;

    private volatile Map<String, RateLimiterEntity> rateLimiterEntityMap = new ConcurrentHashMap<String, RateLimiterEntity>();

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
        
        if (limitPeriod != 1) {
            throw new AquariusException("Limit period must be 1 second for Guava rate limiter");
        }

        RateLimiterEntity rateLimiterEntity = getRateLimiterEntity(compositeKey, limitCount);
        RateLimiter rateLimiter = rateLimiterEntity.getRateLimiter();

        return rateLimiter.tryAcquire();
    }

    private RateLimiterEntity getRateLimiterEntity(String compositeKey, double rate) {
        RateLimiterEntity rateLimiterEntity = rateLimiterEntityMap.get(compositeKey);
        if (rateLimiterEntity == null) {
            RateLimiter newRateLimiter = RateLimiter.create(rate);

            RateLimiterEntity newRateLimiterEntity = new RateLimiterEntity();
            newRateLimiterEntity.setRateLimiter(newRateLimiter);
            newRateLimiterEntity.setRate(rate);

            rateLimiterEntity = rateLimiterEntityMap.putIfAbsent(compositeKey, newRateLimiterEntity);
            if (rateLimiterEntity == null) {
                rateLimiterEntity = newRateLimiterEntity;
            }
        } else {
            if (rateLimiterEntity.getRate() != rate) {
                rateLimiterEntity.getRateLimiter().setRate(rate);
                rateLimiterEntity.setRate(rate);
            }
        }

        return rateLimiterEntity;
    }

    // 因为 rateLimiter.setRate(permitsPerSecond)会执行一次synchronized，为避免不必要的同步，故通过RateLimiterEntity去封装，做一定的冗余设计
    private class RateLimiterEntity {
        private RateLimiter rateLimiter;
        private double rate;

        public RateLimiter getRateLimiter() {
            return rateLimiter;
        }

        public void setRateLimiter(RateLimiter rateLimiter) {
            this.rateLimiter = rateLimiter;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }
    }
}