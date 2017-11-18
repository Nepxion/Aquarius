package com.nepxion.aquarius.limit.redis.delegate;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;

import com.nepxion.aquarius.common.exception.AquariusException;
import com.nepxion.aquarius.limit.delegate.LimitDelegate;
import com.nepxion.aquarius.limit.redis.RedisLimit;

public class RedisLimitDelegate implements LimitDelegate {
    @Autowired
    private RedisLimit redisLimit;

    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public Object invoke(MethodInvocation invocation, String key, int limitPeriod, int limitCount) throws Throwable {
        boolean status = redisLimit.tryAccess(key, limitPeriod, limitCount);
        if (status) {
            return invocation.proceed();
        } else {
            throw new AquariusException("Reach max limited access count=" + limitCount + " within period=" + limitPeriod);
        }
    }
}