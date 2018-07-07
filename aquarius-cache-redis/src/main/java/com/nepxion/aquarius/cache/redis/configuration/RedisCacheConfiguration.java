package com.nepxion.aquarius.cache.redis.configuration;

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

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.cache.redis.condition.RedisCacheCondition;
import com.nepxion.aquarius.cache.redis.impl.RedisCacheDelegateImpl;
import com.nepxion.aquarius.common.redis.adapter.RedisAdapter;
import com.nepxion.aquarius.common.redis.constant.RedisConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;

@Configuration
public class RedisCacheConfiguration {
    @Value("${redis.config.path:" + RedisConstant.CONFIG_FILE + "}")
    private String redisConfigPath;

    @Autowired(required = false)
    private RedisAdapter redisAdapter;

    @Bean
    @Conditional(RedisCacheCondition.class)
    public CacheDelegate redisCacheDelegate() {
        return new RedisCacheDelegateImpl();
    }

    @Bean
    @Conditional(RedisCacheCondition.class)
    public RedisHandler redisHandler() {
        if (redisAdapter != null) {
            return redisAdapter.getRedisHandler();
        }

        return new RedisHandlerImpl(redisConfigPath);
    }
}