package com.nepxion.aquarius.limit.config;

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

import com.nepxion.aquarius.common.config.AquariusConfig;
import com.nepxion.aquarius.limit.local.config.LocalLimitConfig;
import com.nepxion.aquarius.limit.redis.config.RedisLimitConfig;

@Configuration
@Import({ AquariusConfig.class, LimitAopConfig.class, RedisLimitConfig.class, LocalLimitConfig.class })
public class LimitConfig {

}