package com.nepxion.aquarius.cache.config;

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

import com.nepxion.aquarius.cache.redis.config.RedisCacheConfig;
import com.nepxion.aquarius.common.config.AquariusConfig;

@Configuration
@Import({ AquariusConfig.class, CacheAopConfig.class, RedisCacheConfig.class })
public class CacheConfig {

}