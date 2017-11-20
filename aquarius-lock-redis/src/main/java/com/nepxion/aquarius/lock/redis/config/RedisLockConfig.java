package com.nepxion.aquarius.lock.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @email 1394997@qq.com
 * @version 1.0
 */

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;
import com.nepxion.aquarius.lock.LockDelegate;
import com.nepxion.aquarius.lock.LockExecutor;
import com.nepxion.aquarius.lock.redis.condition.RedisLockCondition;
import com.nepxion.aquarius.lock.redis.constant.RedisLockConstant;
import com.nepxion.aquarius.lock.redis.impl.RedisLockDelegateImpl;
import com.nepxion.aquarius.lock.redis.impl.RedisLockExecutorImpl;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class RedisLockConfig {
    @Bean(name = RedisLockConstant.DELEGATE_VALUE)
    @Conditional(RedisLockCondition.class)
    public LockDelegate redisLockDelegate() {
        return new RedisLockDelegateImpl();
    }

    @Bean(name = RedisLockConstant.EXECUTOR_VALUE)
    @Conditional(RedisLockCondition.class)
    public LockExecutor<RLock> redisLockExecutor() {
        return new RedisLockExecutorImpl();
    }

    @Bean(name = "redisson")
    @Conditional(RedisLockCondition.class)
    public RedissonClient redisson() {
        return RedissonHandler.createDefaultRedisson();
    }
}