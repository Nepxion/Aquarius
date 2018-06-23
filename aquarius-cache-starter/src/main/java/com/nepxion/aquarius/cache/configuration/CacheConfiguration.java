package com.nepxion.aquarius.cache.configuration;

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

import com.nepxion.aquarius.cache.configuration.CacheAopConfiguration;
import com.nepxion.aquarius.cache.redis.configuration.RedisCacheConfiguration;
import com.nepxion.aquarius.common.configuration.AquariusConfiguration;

@Configuration
@Import({ AquariusConfiguration.class, CacheAopConfiguration.class, RedisCacheConfiguration.class })
public class CacheConfiguration {

}