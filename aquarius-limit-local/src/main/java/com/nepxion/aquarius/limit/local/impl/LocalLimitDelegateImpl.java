package com.nepxion.aquarius.limit.local.impl;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.LimitDelegate;

public class LocalLimitDelegateImpl implements LimitDelegate {
    @Autowired
    private LimitExecutor limitExecutor;

    @Override
    public Object invoke(MethodInvocation invocation, String key, int limitPeriod, int limitCount) throws Throwable {
        boolean status = limitExecutor.tryAccess(key, limitPeriod, limitCount);
        if (status) {
            return invocation.proceed();
        } else {
            throw new AquariusException("Reach max limited access count=" + limitCount + " within period=" + limitPeriod + " seconds");
        }
    }
}