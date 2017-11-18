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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.lock.delegate.LockDelegate;
import com.nepxion.aquarius.lock.zookeeper.condition.ZookeeperLockCondition;
import com.nepxion.aquarius.lock.zookeeper.constant.ZookeeperLockConstant;
import com.nepxion.aquarius.lock.zookeeper.delegate.ZookeeperLockDelegate;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class ZookeeperLockConfig {
    @Bean(name = ZookeeperLockConstant.DELEGATE_VALUE)
    @Conditional(ZookeeperLockCondition.class)
    public LockDelegate zookeeperLockDelegate() {
        return new ZookeeperLockDelegate();
    }
}