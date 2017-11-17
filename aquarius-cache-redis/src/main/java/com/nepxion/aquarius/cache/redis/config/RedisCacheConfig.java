package com.nepxion.aquarius.cache.redis.config;

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
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.cache.delegate.CacheDelegate;
import com.nepxion.aquarius.cache.redis.condition.RedisCacheCondition;
import com.nepxion.aquarius.cache.redis.constant.RedisCacheConstant;
import com.nepxion.aquarius.cache.redis.delegate.RedisCacheDelegate;

@Configuration
@ComponentScan(basePackages = { "com.nepxion.aquarius.common.context" })
@Import({ com.nepxion.aquarius.common.redis.config.RedisConfig.class })
public class RedisCacheConfig {
    @Bean(name = RedisCacheConstant.DELEGATE_VALUE)
    @Conditional(RedisCacheCondition.class)
    public CacheDelegate redisCacheDelegate() {
        return new RedisCacheDelegate();
    }
}