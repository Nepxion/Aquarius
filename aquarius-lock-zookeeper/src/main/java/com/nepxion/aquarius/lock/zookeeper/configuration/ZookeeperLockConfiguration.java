package com.nepxion.aquarius.lock.zookeeper.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.common.curator.handler.CuratorHandlerImpl;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.zookeeper.condition.ZookeeperLockCondition;
import com.nepxion.aquarius.lock.zookeeper.impl.ZookeeperLockDelegateImpl;
import com.nepxion.aquarius.lock.zookeeper.impl.ZookeeperLockExecutorImpl;

@Configuration
public class ZookeeperLockConfiguration {
    @Bean
    @Conditional(ZookeeperLockCondition.class)
    public LockDelegate zookeeperLockDelegate() {
        return new ZookeeperLockDelegateImpl();
    }

    @Bean
    @Conditional(ZookeeperLockCondition.class)
    public LockExecutor<InterProcessMutex> zookeeperLockExecutor() {
        return new ZookeeperLockExecutorImpl();
    }

    @Bean
    @Conditional(ZookeeperLockCondition.class)
    @ConditionalOnMissingBean
    public CuratorHandler curatorHandler() {
        return new CuratorHandlerImpl();
    }
}