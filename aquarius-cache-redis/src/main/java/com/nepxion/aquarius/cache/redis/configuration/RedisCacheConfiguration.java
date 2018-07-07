package com.nepxion.aquarius.cache.redis.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.nepxion.aquarius.cache.CacheDelegate;
import com.nepxion.aquarius.cache.redis.condition.RedisCacheCondition;
import com.nepxion.aquarius.cache.redis.impl.RedisCacheDelegateImpl;
import com.nepxion.aquarius.cache.redis.impl.RedissonCacheDelegateImpl;
import com.nepxion.aquarius.common.redis.adapter.RedisAdapter;
import com.nepxion.aquarius.common.redis.constant.RedisConstant;
import com.nepxion.aquarius.common.redis.handler.RedisHandler;
import com.nepxion.aquarius.common.redis.handler.RedisHandlerImpl;
import com.nepxion.aquarius.common.redisson.adapter.RedissonAdapter;
import com.nepxion.aquarius.common.redisson.constant.RedissonConstant;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandler;
import com.nepxion.aquarius.common.redisson.handler.RedissonHandlerImpl;

@Configuration
public class RedisCacheConfiguration {
    @Value("${redis.cache.plugin:" + RedisConstant.PLUGIN + "}")
    private String redisCachePlugin;

    @Value("${redis.config.path:" + RedisConstant.CONFIG_FILE + "}")
    private String redisConfigPath;

    @Value("${redisson.config.path:" + RedissonConstant.CONFIG_FILE + "}")
    private String redissonConfigPath;

    @Autowired(required = false)
    private RedisAdapter redisAdapter;

    @Autowired(required = false)
    private RedissonAdapter redissonAdapter;

    @Bean
    @Conditional(RedisCacheCondition.class)
    public CacheDelegate redisCacheDelegate() {
        if (StringUtils.equals(redisCachePlugin, RedisConstant.PLUGIN)) {
            return new RedisCacheDelegateImpl();
        } else if (StringUtils.equals(redisCachePlugin, RedissonConstant.PLUGIN)) {
            return new RedissonCacheDelegateImpl();
        }

        throw new IllegalArgumentException("Invalid plugin type, it must be " + RedisConstant.PLUGIN + " or " + RedissonConstant.PLUGIN);
    }

    @Bean
    @Conditional(RedisCacheCondition.class)
    public RedisHandler redisHandler() {
        if (StringUtils.equals(redisCachePlugin, RedisConstant.PLUGIN)) {
            if (redisAdapter != null) {
                return redisAdapter.getRedisHandler();
            }

            return new RedisHandlerImpl(redisConfigPath);
        }

        return null;
    }

    @Bean
    @Conditional(RedisCacheCondition.class)
    public RedissonHandler redissonHandler() {
        if (StringUtils.equals(redisCachePlugin, RedissonConstant.PLUGIN)) {
            if (redissonAdapter != null) {
                return redissonAdapter.getRedissonHandler();
            }

            return new RedissonHandlerImpl(redissonConfigPath);
        }

        return null;
    }
}