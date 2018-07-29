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
import com.nepxion.aquarius.cache.constant.CacheConstant;
import com.nepxion.aquarius.cache.redis.condition.RedisCacheCondition;
import com.nepxion.aquarius.cache.redis.condition.RedisCachePluginCondition;
import com.nepxion.aquarius.cache.redis.condition.RedissonCachePluginCondition;
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
    @Value("${" + CacheConstant.CACHE_PLUGIN + "}")
    private String cachePlugin;

    @Value("${" + RedisConstant.CONFIG_PATH + ":" + RedisConstant.DEFAULT_CONFIG_PATH + "}")
    private String redisConfigPath;

    @Value("${" + RedissonConstant.PATH + ":" + RedissonConstant.DEFAULT_PATH + "}")
    private String redissonPath;

    @Autowired(required = false)
    private RedisAdapter redisAdapter;

    @Autowired(required = false)
    private RedissonAdapter redissonAdapter;

    @Bean
    @Conditional(RedisCacheCondition.class)
    public CacheDelegate redisCacheDelegate() {
        if (StringUtils.equals(cachePlugin, CacheConstant.CACHE_PLUGIN_REDIS)) {
            return new RedisCacheDelegateImpl();
        } else if (StringUtils.equals(cachePlugin, CacheConstant.CACHE_PLUGIN_REDISSON)) {
            return new RedissonCacheDelegateImpl();
        }

        throw new IllegalArgumentException("Invalid plugin type, it must be '" + CacheConstant.CACHE_PLUGIN_REDIS + "' or '" + CacheConstant.CACHE_PLUGIN_REDISSON + "'");
    }

    @Bean
    @Conditional({ RedisCacheCondition.class, RedisCachePluginCondition.class })
    public RedisHandler redisHandler() {
        if (redisAdapter != null) {
            return redisAdapter.getRedisHandler();
        }

        return new RedisHandlerImpl(redisConfigPath);

    }

    @Bean
    @Conditional({ RedisCacheCondition.class, RedissonCachePluginCondition.class })
    public RedissonHandler redissonHandler() {
        if (redissonAdapter != null) {
            return redissonAdapter.getRedissonHandler();
        }

        return new RedissonHandlerImpl(redissonPath);
    }
}