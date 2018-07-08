package com.nepxion.aquarius.limit.redis.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;

public class RedisLimitDelegateImpl implements LimitDelegate {
    private static final Logger LOG = LoggerFactory.getLogger(RedisLimitDelegateImpl.class);

    @Autowired
    private LimitExecutor limitExecutor;

    @Value("${" + AquariusConstant.AOP_EXCEPTION_IGNORE + ":true}")
    private Boolean aopExceptionIgnore;

    @Override
    public Object invoke(MethodInvocation invocation, String key, int limitPeriod, int limitCount) throws Throwable {
        boolean status = true;
        try {
            status = limitExecutor.tryAccess(key, limitPeriod, limitCount);
        } catch (Exception e) {
            if (aopExceptionIgnore) {
                LOG.error("Limit executes failed", e);

                return invocation.proceed();
            } else {
                throw e;
            }
        }

        if (status) {
            return invocation.proceed();
        } else {
            throw new AquariusException("Reach max limited access count=" + limitCount + " within period=" + limitPeriod + " seconds");
        }
    }
}