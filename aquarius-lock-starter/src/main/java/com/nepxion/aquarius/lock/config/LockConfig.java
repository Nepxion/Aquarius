package com.nepxion.aquarius.lock.config;

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

import com.nepxion.aquarius.common.config.AquariusConfig;
import com.nepxion.aquarius.lock.local.config.LocalLockConfig;
import com.nepxion.aquarius.lock.redis.config.RedisLockConfig;
import com.nepxion.aquarius.lock.zookeeper.config.ZookeeperLockConfig;

@Configuration
@Import({ AquariusConfig.class, LockAopConfig.class, RedisLockConfig.class, ZookeeperLockConfig.class, LocalLockConfig.class })
public class LockConfig {

}