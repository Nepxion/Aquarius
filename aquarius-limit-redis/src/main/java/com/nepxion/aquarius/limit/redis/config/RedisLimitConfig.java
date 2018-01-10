package com.nepxion.aquarius.limit.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2020</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.redis.condition.RedisLimitCondition;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitDelegateImpl;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitExecutorImpl;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class RedisLimitConfig {
    @Bean(name = "redisLimitDelegate")
    @Conditional(RedisLimitCondition.class)
    public LimitDelegate redisLimitDelegate() {
        return new RedisLimitDelegateImpl();
    }

    @Bean(name = "redisLimitExecutor")
    @Conditional(RedisLimitCondition.class)
    public LimitExecutor redisLimitExecutor() {
        return new RedisLimitExecutorImpl();
    }

    @Bean(name = "redisHandler")
    @Conditional(RedisLimitCondition.class)
    public RedisHandler redisHandler() {
        return new RedisHandler();
    }
}