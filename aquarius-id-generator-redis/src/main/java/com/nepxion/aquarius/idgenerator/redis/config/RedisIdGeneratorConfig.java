package com.nepxion.aquarius.idgenerator.redis.config;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.nepxion.aquarius.common.redis.handler.RedisHandler;

@Configuration
@Import({ com.nepxion.aquarius.common.config.AquariusConfig.class })
public class RedisIdGeneratorConfig {
    @Bean(name = "redisHandler")
    public RedisHandler redisHandler() {
        return new RedisHandler();
    }
}