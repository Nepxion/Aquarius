package com.nepxion.aquarius.limit.configuration;

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

import com.nepxion.aquarius.common.configuration.AquariusConfiguration;
import com.nepxion.aquarius.limit.local.configuration.LocalLimitConfiguration;
import com.nepxion.aquarius.limit.redis.configuration.RedisLimitConfiguration;

@Configuration
@Import({ AquariusConfiguration.class, LimitAopConfiguration.class, RedisLimitConfiguration.class, LocalLimitConfiguration.class })
public class LimitConfiguration {

}