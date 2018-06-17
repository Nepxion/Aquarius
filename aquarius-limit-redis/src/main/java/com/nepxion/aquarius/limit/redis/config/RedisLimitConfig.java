package com.nepxion.aquarius.limit.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;
import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.redis.condition.RedisLimitCondition;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitDelegateImpl;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitExecutorImpl;

@Configuration
public class RedisLimitConfig {
    @Bean
    @Conditional(RedisLimitCondition.class)
    public LimitDelegate redisLimitDelegate() {
        return new RedisLimitDelegateImpl();
    }

    @Bean
    @Conditional(RedisLimitCondition.class)
    public LimitExecutor redisLimitExecutor() {
        return new RedisLimitExecutorImpl();
    }

    @Bean
    @Conditional(RedisLimitCondition.class)
    public RedisHandler redisHandler() {
        return new RedisHandlerImpl();
    }
}