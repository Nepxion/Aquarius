package com.nepxion.aquarius.limit.redis.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.common.redis.adapter.RedisAdapter;
import com.nepxion.aquarius.common.redis.constant.RedisConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;
import com.nepxion.aquarius.limit.LimitDelegate;
import com.nepxion.aquarius.limit.LimitExecutor;
import com.nepxion.aquarius.limit.redis.condition.RedisLimitCondition;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitDelegateImpl;
import com.nepxion.aquarius.limit.redis.impl.RedisLimitExecutorImpl;

@Configuration
public class RedisLimitConfiguration {
    @Value("${redis.config.path:" + RedisConstant.DEFAULT_CONFIG_PATH + "}")
    private String redisConfigPath;

    @Autowired(required = false)
    private RedisAdapter redisAdapter;

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
        if (redisAdapter != null) {
            return redisAdapter.getRedisHandler();
        }

        return new RedisHandlerImpl(redisConfigPath);
    }
}