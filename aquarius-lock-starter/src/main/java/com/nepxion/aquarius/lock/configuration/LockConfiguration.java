package com.nepxion.aquarius.lock.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.configuration.AquariusConfiguration;
import com.nepxion.aquarius.lock.local.configuration.LocalLockConfiguration;
import com.nepxion.aquarius.lock.redis.configuration.RedisLockConfiguration;
import com.nepxion.aquarius.lock.zookeeper.configuration.ZookeeperLockConfiguration;

@Configuration
@Import({ AquariusConfiguration.class, LockAopConfiguration.class, RedisLockConfiguration.class, ZookeeperLockConfiguration.class, LocalLockConfiguration.class })
public class LockConfiguration {

}