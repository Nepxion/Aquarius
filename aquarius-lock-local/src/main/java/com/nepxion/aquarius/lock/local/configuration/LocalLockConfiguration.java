package com.nepxion.aquarius.lock.local.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.concurrent.locks.Lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.local.condition.LocalLockCondition;
import com.nepxion.aquarius.lock.local.impl.LocalLockDelegateImpl;
import com.nepxion.aquarius.lock.local.impl.LocalLockExecutorImpl;

@Configuration
public class LocalLockConfiguration {
    @Bean
    @Conditional(LocalLockCondition.class)
    public LockDelegate localLockDelegate() {
        return new LocalLockDelegateImpl();
    }

    @Bean
    @Conditional(LocalLockCondition.class)
    public LockExecutor<Lock> localLockExecutor() {
        return new LocalLockExecutorImpl();
    }
}