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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.lock.delegate.LockDelegate;
import com.nepxion.aquarius.lock.redis.condition.RedisLockCondition;
import com.nepxion.aquarius.lock.redis.constant.RedisLockConstant;
import com.nepxion.aquarius.lock.redis.delegate.RedisLockDelegate;

@Configuration
@ComponentScan(basePackages = { "com.nepxion.aquarius.common" })
public class RedisLockConfig {
    @Bean(name = RedisLockConstant.DELEGATE_VALUE)
    @Conditional(RedisLockCondition.class)
    public LockDelegate redisLockDelegate() {
        return new RedisLockDelegate();
    }
}