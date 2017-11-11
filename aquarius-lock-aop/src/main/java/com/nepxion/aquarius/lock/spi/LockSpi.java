package com.nepxion.aquarius.lock.spi;

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

import com.nepxion.aquarius.lock.entity.LockType;

public interface LockSpi {
    // 初始锁上下文
    void initialize();

    // 销毁锁上下文
    void destroy();

    // 方法调用
    Object invoke(MethodInvocation invocation, LockType lockType, String key, long leaseTime, long waitTime, boolean async, boolean fair) throws Throwable;
}