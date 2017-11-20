package com.nepxion.aquarius.lock.zookeeper.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.constant.AquariusConstant;
import com.nepxion.aquarius.common.curator.handler.CuratorHandler;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.zookeeper.condition.ZookeeperLockCondition;
import com.nepxion.aquarius.lock.zookeeper.constant.ZookeeperLockConstant;
import com.nepxion.aquarius.lock.zookeeper.impl.ZookeeperLockDelegateImpl;
import com.nepxion.aquarius.lock.zookeeper.impl.ZookeeperLockExecutorImpl;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class ZookeeperLockConfig {
    @Value("${" + AquariusConstant.PREFIX + "}")
    private String prefix;

    @Bean(name = ZookeeperLockConstant.DELEGATE_VALUE)
    @Conditional(ZookeeperLockCondition.class)
    public LockDelegate zookeeperLockDelegate() {
        return new ZookeeperLockDelegateImpl();
    }

    @Bean(name = ZookeeperLockConstant.EXECUTOR_VALUE)
    @Conditional(ZookeeperLockCondition.class)
    public LockExecutor<InterProcessMutex> zookeeperLockExecutor() {
        return new ZookeeperLockExecutorImpl();
    }

    @Bean(name = "curator")
    @Conditional(ZookeeperLockCondition.class)
    public CuratorFramework curator() {
        return CuratorHandler.createDefaultCurator(prefix);
    }
}